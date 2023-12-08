package lk.cmh.app.ceylonmarkethub.data.adapter.rv;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.util.List;

import lk.cmh.app.ceylonmarkethub.R;
import lk.cmh.app.ceylonmarkethub.data.model.product.Product;
import lk.cmh.app.ceylonmarkethub.databinding.LayoutTopProductBinding;
import lk.cmh.app.ceylonmarkethub.ui.activity.product.product_view.ProductViewActivity;

public class TopProductsRvAdapter extends RecyclerView.Adapter<TopProductsRvAdapter.ViewHolder> {

    private static final String TAG = TopProductsRvAdapter.class.getSimpleName();
    private List<Product> productList;
    private FirebaseStorage storage;
    private LayoutTopProductBinding binding;

    public TopProductsRvAdapter() {
        this.storage = FirebaseStorage.getInstance();
    }

    public TopProductsRvAdapter(List<Product> productList) {
        this.productList = productList;
        this.storage = FirebaseStorage.getInstance();
    }

    public void setProductList(List<Product> productList) {
        this.productList = productList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        binding = LayoutTopProductBinding.inflate(inflater);
        return new ViewHolder(binding.getRoot(), binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = productList.get(position);

        Double price = product.getPrice();
        String format = String.format("%.2f", price);
        String[] split = format.split("\\.");
        holder.price.setText(split[0]);
        holder.cents.setText("." + split[1]);

        double oldPrice = price + (price * 0.1);
        holder.oldPrice.setText("LKR " + String.format("%.2f", oldPrice));

        storage.getReference("products/" + product.getProductImages().get(0).getUrl()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).placeholder(R.drawable.placeholder_category_item_image).fit().centerCrop().into(holder.productImage);
            }
        });

        holder.item.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), ProductViewActivity.class);
            intent.putExtra("productId", product.getId());
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return productList != null ? productList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView productImage;
        TextView price, cents, currency, orderCount, oldPrice, rating;
        LinearLayout item;

        public ViewHolder(@NonNull View itemView, LayoutTopProductBinding binding) {
            super(itemView);
            productImage = binding.productImage;
            price = binding.priceMain;
            cents = binding.priceCents;
            currency = binding.currency;
            orderCount = binding.orderCount;
            rating = binding.rating;
            oldPrice = binding.oldPrice;
            item = binding.item;
        }
    }
}
