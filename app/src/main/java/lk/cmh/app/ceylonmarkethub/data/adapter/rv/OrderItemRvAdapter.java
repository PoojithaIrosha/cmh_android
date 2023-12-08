package lk.cmh.app.ceylonmarkethub.data.adapter.rv;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;
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
import java.util.Optional;

import lk.cmh.app.ceylonmarkethub.CMH;
import lk.cmh.app.ceylonmarkethub.R;
import lk.cmh.app.ceylonmarkethub.data.model.OrderDto;
import lk.cmh.app.ceylonmarkethub.data.model.product.Product;
import lk.cmh.app.ceylonmarkethub.data.model.product.ProductColors;
import lk.cmh.app.ceylonmarkethub.data.model.product.ProductSize;
import lk.cmh.app.ceylonmarkethub.databinding.LayoutOrderItemBinding;
import lk.cmh.app.ceylonmarkethub.ui.activity.product.product_view.ProductViewActivity;

public class OrderItemRvAdapter extends RecyclerView.Adapter<OrderItemRvAdapter.ViewHolder> {

    private static final String TAG = OrderItemRvAdapter.class.getSimpleName();
    private List<OrderDto> orderDtos;
    private LayoutOrderItemBinding binding;

    private FirebaseStorage storage;

    public OrderItemRvAdapter(List<OrderDto> orderDtos) {
        this.orderDtos = orderDtos;
        this.storage = FirebaseStorage.getInstance();
    }

    public void setOrderDtos(List<OrderDto> orderDtos) {
        this.orderDtos = orderDtos;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        binding = LayoutOrderItemBinding.inflate(layoutInflater);
        binding.getRoot().setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        return new ViewHolder(binding.getRoot(), binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrderDto orderDto = orderDtos.get(position);
        Product product = orderDto.getProduct();

        holder.productTitle.setText(product.getName());

        Double price = product.getPrice() * orderDto.getQuantity();
        String format = String.format("%.2f", price);
        String[] split = format.split("\\.");
        holder.price.setText(split[0]);
        holder.cents.setText("." + split[1]);

        holder.qty.setText(orderDto.getQuantity() + " Qty");

        storage.getReference("products/" + product.getProductImages().get(0).getUrl()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).placeholder(R.drawable.placeholder_category_item_image).fit().centerCrop().into(holder.productImage);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i(TAG, e.getMessage());
            }
        });

        holder.productImage.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), ProductViewActivity.class);
            intent.putExtra("productId", product.getId());
            v.getContext().startActivity(intent);
        });

        StringBuilder options = new StringBuilder();

        if (orderDto.getColor() != null && orderDto.getColor() != 0) {
            Long colorId = orderDto.getColor();
            Optional<ProductColors> first = product.getProductColors().stream().filter(productColors -> productColors.getId() == colorId).findFirst();
            if (first.isPresent()) {
                ProductColors c = first.get();
                options.append(c.getName().toUpperCase());
            }
        }

        if (orderDto.getSize() != null && orderDto.getSize() != 0) {
            Long sizeId = orderDto.getSize();
            Optional<ProductSize> first = product.getProductSizes().stream().filter(productSize -> productSize.getId() == sizeId).findFirst();
            if (first.isPresent()) {
                ProductSize s = first.get();
                if (options.length() > 0) {
                    options.append(" / ");
                }

                options.append(s.getName().toUpperCase());
            }
        }

        holder.productOptions.setText(options.toString());
        holder.shipping.setText("LKR " + CMH.getInstance().getApplicationContext().getString(R.string.shipping));
    }

    @Override
    public int getItemCount() {
        return orderDtos.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView productTitle, productOptions, currency, price, cents, shipping, qty, estimatedDate;

        public ViewHolder(@NonNull View itemView, LayoutOrderItemBinding binding) {
            super(itemView);
            productImage = binding.productImage;
            productTitle = binding.tvProductTitle;
            productOptions = binding.tvOptions;
            currency = binding.tvCurrency;
            price = binding.tvPrice;
            cents = binding.tvCents;
            shipping = binding.tvShipping;
            qty = binding.tvQty;
        }
    }

}
