package lk.cmh.app.ceylonmarkethub.data.adapter.rv;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.util.List;

import lk.cmh.app.ceylonmarkethub.R;
import lk.cmh.app.ceylonmarkethub.data.model.product.Product;
import lk.cmh.app.ceylonmarkethub.databinding.LayoutMainProductItemBinding;
import lk.cmh.app.ceylonmarkethub.ui.activity.product.product_view.ProductViewActivity;

public class ProductsRvAdapter extends RecyclerView.Adapter<ProductsRvAdapter.ViewHolder> {
    private static final String TAG = ProductsRvAdapter.class.getSimpleName();
    private List<Product> productList;
    private FirebaseStorage storage;
    private LayoutMainProductItemBinding binding;

    public ProductsRvAdapter() {
        this.storage = FirebaseStorage.getInstance();
    }

    public List<Product> getProductList() {
        return productList;
    }

    public void setProductList(List<Product> productList) {
        this.productList = productList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        binding = LayoutMainProductItemBinding.inflate(inflater);
        return new ViewHolder(binding.getRoot(), binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = productList.get(position);

        holder.productName.setText(product.getName());

        Double price = product.getPrice();
        String format = String.format("%.2f", price);
        String[] split = format.split("\\.");
        holder.price.setText(split[0]);
        holder.cents.setText("." + split[1]);

        storage.getReference("products/" + product.getProductImages().get(0).getUrl()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).placeholder(R.drawable.placeholder_category_item_image).fit().centerCrop().into(holder.productImage);
                Log.i(TAG, "Image added");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i(TAG, e.getMessage());
            }
        });


        holder.rating.setText("20");
        holder.rating.setText("5.5");

        holder.item.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), ProductViewActivity.class);
            intent.putExtra("productId", product.getId());
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return  productList != null ? productList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView productImage;
        TextView productName, currency, price, cents, orderCount, rating;
        LinearLayout item;

        public ViewHolder(@NonNull View itemView, LayoutMainProductItemBinding binding) {
            super(itemView);
            productImage = binding.productImage;
            productName = binding.productName;
            currency = binding.currency;
            price = binding.priceMain;
            cents = binding.priceCents;
            orderCount = binding.orderCount;
            rating = binding.rating;
            item = binding.item;
        }
    }

}
