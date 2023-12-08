package lk.cmh.app.ceylonmarkethub.data.adapter.rv;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.storage.FirebaseStorage;

import java.text.SimpleDateFormat;
import java.util.List;

import lk.cmh.app.ceylonmarkethub.data.model.order_history.OrderHistory;
import lk.cmh.app.ceylonmarkethub.databinding.LayoutOrderHistoryItemBinding;

public class OrderHistoryRvAdapter extends RecyclerView.Adapter<OrderHistoryRvAdapter.ViewHolder> {

    private List<OrderHistory> orderHistoryList;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM yyyy hh:mm:ss");

    private FirebaseStorage storage;

    public OrderHistoryRvAdapter(List<OrderHistory> orderHistoryList) {
        this.orderHistoryList = orderHistoryList;
        storage = FirebaseStorage.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        LayoutOrderHistoryItemBinding binding = LayoutOrderHistoryItemBinding.inflate(layoutInflater);
        binding.getRoot().setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        return new ViewHolder(binding.getRoot(), binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrderHistory orderHistory = orderHistoryList.get(position);

        holder.orderId.setText("Order #" + orderHistory.getId());
        holder.orderDate.setText(simpleDateFormat.format(orderHistory.getPurchased_at()));
        holder.total.setText("LKR " + String.valueOf(orderHistory.getTotal()));
        holder.qty.setText(orderHistory.getOrderHistoryItems().size() + " Items, Total:");

        holder.rvOrderHistoryItems.setAdapter(new OrderHistoryItemProductRvAdapter(orderHistory.getOrderHistoryItems()));
    }

    @Override
    public int getItemCount() {
        return orderHistoryList != null ? orderHistoryList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView orderId, orderDate, total, qty;
        RecyclerView rvOrderHistoryItems;

        public ViewHolder(@NonNull View itemView, LayoutOrderHistoryItemBinding binding) {
            super(itemView);
            orderId = binding.tvOrderId;
            orderDate = binding.tvOrderDate;
            total = binding.tvItemTotal;
            rvOrderHistoryItems = binding.rvOrderHistoryItems;
            qty = binding.tvQty;
        }
    }

}
