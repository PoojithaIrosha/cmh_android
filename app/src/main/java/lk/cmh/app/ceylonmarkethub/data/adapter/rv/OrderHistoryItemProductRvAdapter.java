package lk.cmh.app.ceylonmarkethub.data.adapter.rv;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.util.List;

import lk.cmh.app.ceylonmarkethub.R;
import lk.cmh.app.ceylonmarkethub.data.model.order_history.OrderHistoryItem;
import lk.cmh.app.ceylonmarkethub.data.model.product.Product;
import lk.cmh.app.ceylonmarkethub.databinding.LayoutOrderHistoryItemProductBinding;

public class OrderHistoryItemProductRvAdapter extends RecyclerView.Adapter<OrderHistoryItemProductRvAdapter.ViewHolder> {

    private List<OrderHistoryItem> orderHistoryItems;
    private FirebaseStorage storage;

    public OrderHistoryItemProductRvAdapter(List<OrderHistoryItem> orderHistoryItems) {
        this.orderHistoryItems = orderHistoryItems;
        this.storage = FirebaseStorage.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        LayoutOrderHistoryItemProductBinding binding = LayoutOrderHistoryItemProductBinding.inflate(inflater);
        return new ViewHolder(binding.getRoot(), binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrderHistoryItem orderHistoryItem = orderHistoryItems.get(position);
        Product product = orderHistoryItem.getProduct();

        holder.name.setText(product.getName());
        holder.price.setText("LKR " + String.format("%.2f", orderHistoryItem.getPrice()));
        holder.qty.setText("x " + orderHistoryItem.getQuantity());
        holder.status.setText(orderHistoryItem.getStatus().toString());

        storage.getReference("products/" + product.getProductImages().get(0).getUrl()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).placeholder(R.drawable.placeholder_category_item_image).fit().centerCrop().into(holder.productImage);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public int getItemCount() {
        return orderHistoryItems != null ? orderHistoryItems.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView productImage;
        TextView name, price, qty, status;

        public ViewHolder(@NonNull View itemView, LayoutOrderHistoryItemProductBinding binding) {
            super(itemView);
            name = binding.tvProductTitle;
            price = binding.tvPrice;
            qty = binding.tvQty;
            productImage = binding.ivProductImage;
            status = binding.tvOrderStatus;
        }
    }
}