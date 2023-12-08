package lk.cmh.app.ceylonmarkethub.data.adapter.rv;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
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
import lk.cmh.app.ceylonmarkethub.data.model.wishlist.WishListItem;
import lk.cmh.app.ceylonmarkethub.databinding.LayoutWishlistItemBinding;
import lk.cmh.app.ceylonmarkethub.ui.activity.product.product_view.ProductViewActivity;
import lk.cmh.app.ceylonmarkethub.ui.activity.wishlist.WishlistViewModel;

public class WishlistItemRvAdapter extends RecyclerView.Adapter<WishlistItemRvAdapter.ViewHolder> {

    private static final String TAG = WishlistItemRvAdapter.class.getSimpleName();
    private List<WishListItem> wishListItems;
    private FirebaseStorage storage;
    private LayoutWishlistItemBinding binding;
    private WishlistViewModel wishlistViewModel;

    public WishlistItemRvAdapter() {
    }

    public WishlistItemRvAdapter(List<WishListItem> wishListItems, WishlistViewModel wishlistViewModel) {
        this.wishListItems = wishListItems;
        this.wishlistViewModel = wishlistViewModel;
        this.storage = FirebaseStorage.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        binding = LayoutWishlistItemBinding.inflate(inflater);
        binding.getRoot().setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        return new ViewHolder(binding.getRoot(), binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WishListItem wishListItem = wishListItems.get(position);
        Product product = wishListItem.getProduct();

        String format = String.format("%.2f", product.getPrice());
        holder.productTitle.setText(product.getName());
        holder.productPrice.setText("LKR " + format);

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

        holder.deleteBtn.setOnClickListener(v -> {
            wishlistViewModel.removeItem(product.getId());
        });

        holder.container.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), ProductViewActivity.class);
            intent.putExtra("productId", product.getId());
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return wishListItems != null ? wishListItems.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView productTitle, productPrice;
        ImageButton deleteBtn;
        ImageView productImage;

        LinearLayout container;

        public ViewHolder(@NonNull View itemView, LayoutWishlistItemBinding binding) {
            super(itemView);
            productTitle = binding.tvProductTitle;
            productPrice = binding.tvProductPrice;
            deleteBtn = binding.ibDelete;
            productImage = binding.ivProductImage;
            container = binding.container;
        }
    }

}
