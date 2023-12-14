package lk.cmh.app.ceylonmarkethub.ui.activity.orders;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import lk.cmh.app.ceylonmarkethub.R;
import lk.cmh.app.ceylonmarkethub.data.adapter.rv.OrderItemRvAdapter;
import lk.cmh.app.ceylonmarkethub.data.model.OrderDto;
import lk.cmh.app.ceylonmarkethub.data.model.order_history.OrderHistoryDto;
import lk.cmh.app.ceylonmarkethub.data.model.order_history.OrderHistoryItemDto;
import lk.cmh.app.ceylonmarkethub.data.model.user.User;
import lk.cmh.app.ceylonmarkethub.data.model.user.UserAddress;
import lk.cmh.app.ceylonmarkethub.data.util.GsonUtil;
import lk.cmh.app.ceylonmarkethub.databinding.ActivityOrderConfirmationBinding;
import lk.cmh.app.ceylonmarkethub.ui.activity.order_history.OrderHistoryActivity;
import lk.cmh.app.ceylonmarkethub.ui.activity.shipping_address.ShippingAddressActivity;
import lk.payhere.androidsdk.PHConfigs;
import lk.payhere.androidsdk.PHConstants;
import lk.payhere.androidsdk.PHMainActivity;
import lk.payhere.androidsdk.PHResponse;
import lk.payhere.androidsdk.model.InitRequest;
import lk.payhere.androidsdk.model.StatusResponse;

public class OrderConfirmationActivity extends AppCompatActivity {

    private static final String TAG = OrderConfirmationActivity.class.getSimpleName();
    private ActivityOrderConfirmationBinding binding;
    private OrderConfirmationVM confirmationVM;
    private List<OrderDto> orders;
    private double grandTotal;

    @Override
    protected void onStart() {
        super.onStart();
        confirmationVM.loadAddress();
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrderConfirmationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        confirmationVM = new ViewModelProvider(this, ViewModelProvider.Factory.from(OrderConfirmationVM.initializer)).get(OrderConfirmationVM.class);

        confirmationVM.loadAddress();

        Intent intent = getIntent();
        String json = intent.getExtras().getString("order");
        TypeToken<List<OrderDto>> typeToken = new TypeToken<List<OrderDto>>() {
        };
        Type type = typeToken.getType();

        orders = GsonUtil.getInstance().fromJson(json, type);

        assert orders != null;
        OrderItemRvAdapter adapter = (OrderItemRvAdapter) binding.rvOrderItems.getAdapter();
        if (adapter != null) {
            adapter.setOrderDtos(orders);
            adapter.notifyDataSetChanged();
        } else {
            binding.rvOrderItems.setAdapter(new OrderItemRvAdapter(orders));
        }

        AtomicReference<Double> total = new AtomicReference<>((double) 0);
        orders.forEach(orderDto -> {
            double price = orderDto.getProduct().getPrice() * orderDto.getQuantity();
            total.updateAndGet(v -> Double.valueOf(v + price));
        });

        String totalStr = String.format("%.2f", total.get());
        binding.tvTotal.setText("LKR " + totalStr);

        grandTotal = total.get() + Double.parseDouble(getString(R.string.shipping));
        String grandTotalStr = String.format("%.2f", grandTotal);
        binding.tvGrandTotal.setText("LKR " + grandTotalStr);

        ZonedDateTime zonedDateTime = ZonedDateTime.now(ZoneOffset.UTC).plusDays(7);
        binding.estdate.setText(zonedDateTime.getMonth() + " " + zonedDateTime.getDayOfMonth());

        binding.tvShipping.setText("LKR " + getString(R.string.shipping));
        binding.tvShippingFee.setText("LKR " + getString(R.string.shipping));

        binding.addNewAddress.setOnClickListener(v -> {
            Intent intent1 = new Intent(OrderConfirmationActivity.this, ShippingAddressActivity.class);
            intent1.putExtra("order", json);
            startActivity(intent1);
            finish();
        });

        binding.mainTopAppBar.setNavigationOnClickListener(v -> {
            finish();
        });


        confirmationVM.getAddress().observe(this, new Observer<UserAddress>() {
            @Override
            public void onChanged(UserAddress address) {
                if (address != null && address.getAddressLine1() != null) {
                    binding.tvAddressLine1.setText(address.getAddressLine1());
                    if (address.getAddressLine2() == null || address.getAddressLine2().isEmpty()) {
                        binding.tvAddressLine2.setVisibility(View.GONE);
                    } else {
                        binding.tvAddressLine2.setText(address.getAddressLine2());
                    }
                    binding.tvCityProvinceZipcode.setText(address.getCity() + ", " + address.getProvince() + ", " + address.getPostalCode());
                    binding.tvContactName.setText(address.getContactName());
                    binding.tvMobileNumber.setText(address.getMobileNumber());

                    binding.btnCheckOut.setEnabled(true);
                } else {
                    binding.llShippingDetails.setVisibility(View.GONE);
                    binding.addNewAddress.setVisibility(View.VISIBLE);
                    binding.btnCheckOut.setEnabled(false);
                }
            }
        });


        binding.btnCheckOut.setOnClickListener(v -> {
            StringBuilder description = new StringBuilder();
            orders.forEach(order -> {
                if (description.length() > 0) {
                    description.append(", ");
                }
                description.append(order.getProduct().getName());
            });

            checkout(description.toString(), grandTotal);
        });
    }

    private void checkout(String description, double total) {
        User user = confirmationVM.getUser().getValue();

        InitRequest req = new InitRequest();
        req.setMerchantId(getString(R.string.merchantId));
        req.setCurrency(getString(R.string.currency));
        req.setAmount(total);             // Final Amount to be charged
        req.setOrderId(String.valueOf(System.currentTimeMillis()));
        req.setItemsDescription(description);
        req.getCustomer().setFirstName(user.getFirstName());
        if(user.getLastName() == null || user.getLastName().isEmpty()) {
            req.getCustomer().setLastName("");
        }else {
            req.getCustomer().setLastName(user.getLastName());
        }
        req.getCustomer().setEmail(user.getEmail());

        if (user.getAddress() != null) {
            req.getCustomer().setPhone(user.getAddress().getMobileNumber());
            req.getCustomer().getAddress().setAddress(user.getAddress().getAddressLine1() + ", " + user.getAddress().getAddressLine2() != null ? user.getAddress().getAddressLine2() : "");
            req.getCustomer().getAddress().setCity(user.getAddress().getCity());
            req.getCustomer().getAddress().setCountry("Sri Lanka");
        }else {
            req.getCustomer().setPhone("");
            req.getCustomer().getAddress().setAddress("");
            req.getCustomer().getAddress().setCity("");
            req.getCustomer().getAddress().setCountry("Sri Lanka");
        }

        Intent intent = new Intent(this, PHMainActivity.class);
        intent.putExtra(PHConstants.INTENT_EXTRA_DATA, req);
        PHConfigs.setBaseUrl(PHConfigs.SANDBOX_URL);
        payHereLauncher.launch(intent);
    }

    private final ActivityResultLauncher<Intent> payHereLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult o) {
            if (o.getData() != null && o.getData().hasExtra(PHConstants.INTENT_EXTRA_RESULT)) {
                PHResponse<StatusResponse> response = (PHResponse<StatusResponse>) o.getData().getSerializableExtra(PHConstants.INTENT_EXTRA_RESULT);
                if (o.getResultCode() == Activity.RESULT_OK) {
                    if (response != null && response.isSuccess()) {
                        completeOrder(orders);
                    } else {
                        Log.d(TAG, response.toString());
                        Toast.makeText(OrderConfirmationActivity.this, response.getData().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else if (o.getResultCode() == Activity.RESULT_CANCELED) {
                    if (response != null) {
                        Log.i(TAG, response.toString());
                        Toast.makeText(OrderConfirmationActivity.this, response.toString(), Toast.LENGTH_SHORT).show();
                    } else {
                        Log.i(TAG, "User canceled the request");
                        Toast.makeText(OrderConfirmationActivity.this, "User canceled the request", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    });

    private void completeOrder(List<OrderDto> orders) {
        OrderHistoryDto orderHistoryDto = new OrderHistoryDto(grandTotal, new ArrayList<>());

        orders.forEach(orderDto -> {
            OrderHistoryItemDto orderHistoryItemDto = new OrderHistoryItemDto(orderDto.getProductId(), orderDto.getQuantity(), orderDto.getSize(), orderDto.getColor());
            orderHistoryDto.getOrderItems().add(orderHistoryItemDto);
        });

        confirmationVM.completeOrder(orderHistoryDto, () -> {
            startActivity(new Intent(OrderConfirmationActivity.this, OrderHistoryActivity.class));
            finish();
        });
    }
}