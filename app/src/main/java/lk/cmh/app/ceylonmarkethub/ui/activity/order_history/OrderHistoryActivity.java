package lk.cmh.app.ceylonmarkethub.ui.activity.order_history;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import java.util.List;

import lk.cmh.app.ceylonmarkethub.data.adapter.rv.OrderHistoryRvAdapter;
import lk.cmh.app.ceylonmarkethub.data.model.order_history.OrderHistory;
import lk.cmh.app.ceylonmarkethub.databinding.ActivityOrderHistoryBinding;

public class OrderHistoryActivity extends AppCompatActivity {

    private ActivityOrderHistoryBinding binding;
    private OrderHistoryVM orderHistoryVM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrderHistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        orderHistoryVM = new ViewModelProvider(this, ViewModelProvider.Factory.from(OrderHistoryVM.initializer)).get(OrderHistoryVM.class);

        orderHistoryVM.getOrderHistory().observe(this, new Observer<List<OrderHistory>>() {
            @Override
            public void onChanged(List<OrderHistory> orderHistories) {
                binding.rvOrderHistory.setAdapter(new OrderHistoryRvAdapter(orderHistories));
            }
        });
    }
}