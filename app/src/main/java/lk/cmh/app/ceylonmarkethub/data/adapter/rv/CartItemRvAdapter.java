package lk.cmh.app.ceylonmarkethub.data.adapter.rv;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.util.List;

import lk.cmh.app.ceylonmarkethub.R;
import lk.cmh.app.ceylonmarkethub.data.model.cart.CartItem;
import lk.cmh.app.ceylonmarkethub.data.model.cart.CartItemUpdateDto;
import lk.cmh.app.ceylonmarkethub.data.model.product.Product;
import lk.cmh.app.ceylonmarkethub.databinding.LayoutCartItemBinding;
import lk.cmh.app.ceylonmarkethub.ui.activity.product.product_view.ProductViewActivity;
import lk.cmh.app.ceylonmarkethub.ui.fragment.cart.CartViewModel;

public class CartItemRvAdapter extends RecyclerView.Adapter<CartItemRvAdapter.ViewHolder> {

    private static final String TAG = CartItemRvAdapter.class.getSimpleName();
    private List<CartItem> cartItems;
    private FirebaseStorage storage;
    private LayoutCartItemBinding binding;
    private CartViewModel cartViewModel;

    public CartItemRvAdapter() {
        storage = FirebaseStorage.getInstance();
    }

    public CartItemRvAdapter(List<CartItem> cartItems, CartViewModel cartViewModel) {
        this.cartItems = cartItems;
        this.cartViewModel = cartViewModel;
        storage = FirebaseStorage.getInstance();
    }

    public List<CartItem> getCartItems() {
        return cartItems;
    }

    public void setCartItems(List<CartItem> cartItems) {
        this.cartItems = cartItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        binding = LayoutCartItemBinding.inflate(inflater);
        binding.getRoot().setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        return new ViewHolder(binding.getRoot(), binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CartItem cartItem = cartItems.get(position);
        Product product = cartItem.getProduct();

        holder.productTitle.setText(product.getName());

        Double price = cartItem.getPrice() * cartItem.getQuantity();
        String format = String.format("%.2f", price);
        String[] split = format.split("\\.");
        holder.price.setText(split[0]);
        holder.cents.setText("." + split[1]);

        holder.qty.setText(String.valueOf(cartItem.getQuantity()));

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

        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    cartViewModel.addSelectedItem(cartItem);
                } else {
                    cartViewModel.removeSelectedItem(cartItem);
                }
            }
        });

        holder.btnIncrement.setOnClickListener(v -> {
            Long id = cartItem.getId();
            int qty = cartItem.getQuantity() + 1;

            if (qty <= product.getQuantity()) {
                CartItemUpdateDto updateDto = new CartItemUpdateDto(id, qty);
                cartViewModel.updateCartItem(updateDto);
            } else {
                Toast.makeText(v.getContext(), "Product quantity exceeded", Toast.LENGTH_SHORT).show();
            }

        });

        holder.btnDecrement.setOnClickListener(v -> {
            Long id = cartItem.getId();

            if (cartItem.getQuantity() > 1) {
                int qty = cartItem.getQuantity() - 1;
                CartItemUpdateDto updateDto = new CartItemUpdateDto(id, qty);
                cartViewModel.updateCartItem(updateDto);
            }
        });

        StringBuilder options = new StringBuilder();

        if (cartItem.getProductColor() != null) {
            options.append(cartItem.getProductColor().getName().toUpperCase());
        }

        if (cartItem.getProductSize() != null) {
            if(options.length() > 0) {
                options.append(" / ");
            }
            options.append(cartItem.getProductSize().getName().toUpperCase());
        }

        holder.productOptions.setText(options.toString());
    }

    @Override
    public int getItemCount() {
        return cartItems != null ? cartItems.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        MaterialCheckBox checkBox;
        ImageView productImage;
        TextView productTitle, productOptions, currency, price, cents, shipping, qty;
        ImageButton btnIncrement, btnDecrement;

        public ViewHolder(@NonNull View itemView, LayoutCartItemBinding binding) {
            super(itemView);
            productImage = binding.productImage;
            productTitle = binding.tvProductTitle;
            productOptions = binding.tvOptions;
            currency = binding.tvCurrency;
            price = binding.tvPrice;
            cents = binding.tvCents;
            shipping = binding.tvShipping;
            qty = binding.tvQty;
            checkBox = binding.checkBox;
            btnIncrement = binding.btnIncrement;
            btnDecrement = binding.btnDecrement;
        }
    }


}
