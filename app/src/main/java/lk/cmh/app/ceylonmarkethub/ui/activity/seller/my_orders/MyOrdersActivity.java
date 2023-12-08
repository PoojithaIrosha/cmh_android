package lk.cmh.app.ceylonmarkethub.ui.activity.seller.my_orders;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import java.util.List;

import lk.cmh.app.ceylonmarkethub.data.adapter.rv.SellerMyOrdersRvAdapter;
import lk.cmh.app.ceylonmarkethub.data.model.seller.SellerOrderDto;
import lk.cmh.app.ceylonmarkethub.databinding.ActivityMyOrdersBinding;

public class MyOrdersActivity extends AppCompatActivity {

    private ActivityMyOrdersBinding binding;
    private MyOrdersVM myOrdersVM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyOrdersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        myOrdersVM = new ViewModelProvider(this, ViewModelProvider.Factory.from(MyOrdersVM.initializer)).get(MyOrdersVM.class);

        binding.mainTopAppBar.setNavigationOnClickListener(v -> {
            finish();
        });

        myOrdersVM.getSellerOrders().observe(this, new Observer<List<SellerOrderDto>>() {
            @Override
            public void onChanged(List<SellerOrderDto> sellerOrderDtos) {
                binding.rvMyOrders.setAdapter(new SellerMyOrdersRvAdapter(sellerOrderDtos));
            }
        });
    }
}