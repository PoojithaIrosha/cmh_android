package lk.cmh.app.ceylonmarkethub.data.adapter.rv;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.storage.FirebaseStorage;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import java.util.List;

import lk.cmh.app.ceylonmarkethub.R;
import lk.cmh.app.ceylonmarkethub.data.model.product.Product;
import lk.cmh.app.ceylonmarkethub.data.repository.ProductRepository;
import lk.cmh.app.ceylonmarkethub.data.util.FragmentUtil;
import lk.cmh.app.ceylonmarkethub.databinding.LayoutSellerMyProductItemBinding;
import lk.cmh.app.ceylonmarkethub.ui.activity.product.product_view.ProductViewActivity;
import lk.cmh.app.ceylonmarkethub.ui.fragment.seller.products.update_product.UpdateProductFragment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SellerProductsRvAdapter extends RecyclerView.Adapter<SellerProductsRvAdapter.ViewHolder> {

    private static final String TAG = SellerProductsRvAdapter.class.getSimpleName();
    private List<Product> products;
    private FirebaseStorage storage;
    private ProductRepository productRepository;

    public SellerProductsRvAdapter(List<Product> products, ProductRepository productRepository) {
        this.products = products;
        this.storage = FirebaseStorage.getInstance();
        this.productRepository = productRepository;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        LayoutSellerMyProductItemBinding binding = LayoutSellerMyProductItemBinding.inflate(inflater);
        binding.getRoot().setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        return new ViewHolder(binding.getRoot(), binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = products.get(position);
        holder.title.setText(product.getName());
        holder.currency.setText("LKR");
        Double price = product.getPrice();
        String format = String.format("%.2f", price);
        String[] split = format.split("\\.");
        holder.price.setText(split[0]);
        holder.cents.setText("." + split[1]);
        holder.condition.setText(product.getProductCondition());
        holder.category.setText(product.getCategory().getName());
        holder.checkBox.setChecked(!product.isDeleted());

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

        holder.btnEdit.setOnClickListener(v -> {
            UpdateProductFragment updateProductFragment = new UpdateProductFragment();
            Bundle bundle = new Bundle();
            bundle.putLong("pid", product.getId());
            updateProductFragment.setArguments(bundle);
            FragmentUtil.loadFragment(updateProductFragment, R.id.fragmentContainerView, (FragmentActivity) v.getContext());
        });

        holder.checkBox.setOnClickListener(v -> {
            new MaterialAlertDialogBuilder(v.getContext())
                    .setTitle("Confirmation")
                    .setMessage("Are you sure you want to " + (holder.checkBox.isChecked() ? "enable" : "disable") + " the product")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        productRepository.changeStatus(product.getId()).enqueue(new Callback<JsonObject>() {
                            @Override
                            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                                Log.i(TAG, response.body().toString());
                            }

                            @Override
                            public void onFailure(Call<JsonObject> call, Throwable t) {
                                holder.checkBox.setChecked(!holder.checkBox.isChecked());
                            }
                        });
                    })
                    .setNegativeButton("No", (dialog, which) -> {
                        holder.checkBox.setChecked(!holder.checkBox.isChecked());
                    })
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return products != null ? products.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView productImage;
        TextView title, currency, price, cents, condition, category;
        ImageButton btnEdit;
        MaterialCheckBox checkBox;

        public ViewHolder(@NonNull View itemView, LayoutSellerMyProductItemBinding binding) {
            super(itemView);
            productImage = binding.productImage;
            title = binding.tvProductTitle;
            currency = binding.tvCurrency;
            price = binding.tvPrice;
            cents = binding.tvCents;
            condition = binding.tvCondition;
            category = binding.tvCategory;
            btnEdit = binding.btnEdit;
            checkBox = binding.checkBox;
        }
    }

}
