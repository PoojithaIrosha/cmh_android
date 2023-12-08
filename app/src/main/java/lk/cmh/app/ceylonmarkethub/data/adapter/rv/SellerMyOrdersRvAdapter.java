package lk.cmh.app.ceylonmarkethub.data.adapter.rv;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;

import lk.cmh.app.ceylonmarkethub.data.model.seller.SellerOrderDto;
import lk.cmh.app.ceylonmarkethub.databinding.LayoutSellerMyOrdersBinding;

public class SellerMyOrdersRvAdapter extends RecyclerView.Adapter<SellerMyOrdersRvAdapter.ViewHolder> {

    private List<SellerOrderDto> sellerOrderDtoList;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM yyyy hh:mm:ss");
    private LayoutSellerMyOrdersBinding binding;

    public SellerMyOrdersRvAdapter(List<SellerOrderDto> sellerOrderDtoList) {
        this.sellerOrderDtoList = sellerOrderDtoList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        binding = LayoutSellerMyOrdersBinding.inflate(inflater);
        binding.getRoot().setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        return new ViewHolder(binding.getRoot(), binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SellerOrderDto sellerOrderDto = sellerOrderDtoList.get(position);

        holder.orderId.setText("Order #" + sellerOrderDto.getId());
        holder.orderDate.setText(simpleDateFormat.format(sellerOrderDto.getPurchased_at()));
        holder.total.setText(String.valueOf(sellerOrderDto.getTotal()));
        holder.qty.setText(sellerOrderDto.getOrderItems().size() + " Items, Total:");

        holder.rvOrderHistoryItems.setAdapter(new SellerMyOrdersItemRvAdapter(sellerOrderDto.getOrderItems()));
    }

    @Override
    public int getItemCount() {
        return sellerOrderDtoList != null ? sellerOrderDtoList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView orderId, orderDate, total, qty;
        RecyclerView rvOrderHistoryItems;

        public ViewHolder(@NonNull View itemView, LayoutSellerMyOrdersBinding binding) {
            super(itemView);
            orderId = binding.tvOrderId;
            orderDate = binding.tvOrderDate;
            total = binding.tvItemTotal;
            rvOrderHistoryItems = binding.rvOrderHistoryItems;
            qty = binding.tvQty;
        }
    }

}
