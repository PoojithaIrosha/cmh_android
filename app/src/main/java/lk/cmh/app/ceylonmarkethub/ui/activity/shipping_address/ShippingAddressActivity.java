package lk.cmh.app.ceylonmarkethub.ui.activity.shipping_address;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import lk.cmh.app.ceylonmarkethub.R;
import lk.cmh.app.ceylonmarkethub.data.model.user.UserAddress;
import lk.cmh.app.ceylonmarkethub.databinding.ActivityShippinAddressBinding;
import lk.cmh.app.ceylonmarkethub.ui.activity.orders.OrderConfirmationActivity;
import lk.cmh.app.ceylonmarkethub.ui.activity.orders.OrderConfirmationVM;

public class ShippingAddressActivity extends AppCompatActivity {

    private static final String TAG = ShippingAddressActivity.class.getSimpleName();

    private ActivityShippinAddressBinding binding;
    private ShippingVM shippingVM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityShippinAddressBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent i = getIntent();
        String json = i.getExtras().getString("order", "null");
        String email = i.getExtras().getString("context", "null");

        shippingVM = new ViewModelProvider(this, ViewModelProvider.Factory.from(ShippingVM.initializer)).get(ShippingVM.class);

        if (email.equals("settings")) {
            shippingVM.loadAddress();
        }

        binding.backBtn.setOnClickListener(v -> {
            finish();
        });

        shippingVM.getUserAddress().observe(this, new Observer<UserAddress>() {
            @Override
            public void onChanged(UserAddress userAddress) {
                binding.etContactName.setText(userAddress.getContactName());
                binding.etMobileNumber.setText(userAddress.getMobileNumber());
                binding.etAddressLine1.setText(userAddress.getAddressLine1());
                binding.etAddressLine2.setText(userAddress.getAddressLine2());
                binding.etProvince.setText(userAddress.getProvince());
                binding.etCity.setText(userAddress.getCity());
                binding.etZipCode.setText(userAddress.getPostalCode());

                shippingVM.getShippingFormState().setValue(null);
                binding.etContactName.setError(null);
                binding.etMobileNumber.setError(null);
                binding.etAddressLine1.setError(null);
                binding.etAddressLine2.setError(null);
                binding.etProvince.setError(null);
                binding.etCity.setError(null);
                binding.etZipCode.setError(null);
            }
        });

        shippingVM.getShippingFormState().observe(this, new Observer<ShippingFormState>() {
            @Override
            public void onChanged(ShippingFormState shippingFormState) {
                if (shippingFormState == null) {
                    return;
                }

                if (shippingFormState.getContactNameError() != null) {
                    binding.etContactName.setError(shippingFormState.getContactNameError());
                }

                if (shippingFormState.getMobileNumberError() != null) {
                    binding.etMobileNumber.setError(shippingFormState.getMobileNumberError());
                }

                if (shippingFormState.getAddressLine1Error() != null) {
                    binding.etAddressLine1.setError(shippingFormState.getAddressLine1Error());
                }

                if (shippingFormState.getProvinceError() != null) {
                    binding.etProvince.setError(shippingFormState.getProvinceError());
                }

                if (shippingFormState.getCityError() != null) {
                    binding.etCity.setError(shippingFormState.getCityError());
                }

                if (shippingFormState.getZipCodeError() != null) {
                    binding.etZipCode.setError(shippingFormState.getZipCodeError());
                }

                binding.btnSave.setEnabled(shippingFormState.isValid());
            }
        });

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                shippingVM.shippingAddressDataChanged(binding.etContactName.getText().toString(), binding.etMobileNumber.getText().toString(), binding.etAddressLine1.getText().toString(), binding.etProvince.getText().toString(), binding.etCity.getText().toString(), binding.etZipCode.getText().toString());
            }
        };

        binding.etContactName.addTextChangedListener(textWatcher);
        binding.etMobileNumber.addTextChangedListener(textWatcher);
        binding.etAddressLine1.addTextChangedListener(textWatcher);
        binding.etProvince.addTextChangedListener(textWatcher);
        binding.etCity.addTextChangedListener(textWatcher);
        binding.etZipCode.addTextChangedListener(textWatcher);

        binding.etZipCode.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    shippingVM.save(
                            binding.etContactName.getText().toString(),
                            binding.etMobileNumber.getText().toString(),
                            binding.etAddressLine1.getText().toString(),
                            binding.etAddressLine2.getText().toString(),
                            binding.etProvince.getText().toString(),
                            binding.etCity.getText().toString(),
                            binding.etZipCode.getText().toString()
                    );
                    finish();
                }
                return false;
            }
        });

        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shippingVM.save(
                        binding.etContactName.getText().toString(),
                        binding.etMobileNumber.getText().toString(),
                        binding.etAddressLine1.getText().toString(),
                        binding.etAddressLine2.getText().toString(),
                        binding.etProvince.getText().toString(),
                        binding.etCity.getText().toString(),
                        binding.etZipCode.getText().toString()
                );

                if (json.equals("null")) {
                    finish();
                } else {
                    Intent intent = new Intent(ShippingAddressActivity.this, OrderConfirmationActivity.class);
                    intent.putExtra("order", json);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }
}