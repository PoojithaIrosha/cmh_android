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
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.util.List;

import lk.cmh.app.ceylonmarkethub.R;
import lk.cmh.app.ceylonmarkethub.data.model.category.Category;
import lk.cmh.app.ceylonmarkethub.databinding.LayoutCategoryItemBinding;
import lk.cmh.app.ceylonmarkethub.ui.activity.product.search.ProductSearchViewActivity;
import lk.cmh.app.ceylonmarkethub.ui.activity.product.search.SearchProductsActivity;

public class CategoriesRvAdapter extends RecyclerView.Adapter<CategoriesRvAdapter.ViewHolder> {

    private static final String TAG = CategoriesRvAdapter.class.getSimpleName();
    private LayoutCategoryItemBinding binding;
    private List<Category> categoryList;
    private FirebaseStorage storage;

    public CategoriesRvAdapter() {
        storage = FirebaseStorage.getInstance();
    }

    public CategoriesRvAdapter(List<Category> categoryList) {
        this.categoryList = categoryList;
        storage = FirebaseStorage.getInstance();
    }

    public void setCategoryList(List<Category> categoryList) {
        this.categoryList = categoryList;
    }

    @NonNull
    @Override
    public CategoriesRvAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        binding = LayoutCategoryItemBinding.inflate(inflater);
        return new ViewHolder(binding.getRoot(), binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoriesRvAdapter.ViewHolder holder, int position) {
        Category category = categoryList.get(position);
        holder.categoryName.setText(category.getName());

        storage.getReference().child("categories/" + category.getImage()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Log.i(TAG, uri.toString());
                Picasso.get().load(uri).placeholder(R.drawable.placeholder_category_item_image).fit().centerCrop().into(holder.categoryImage);
            }
        });

        holder.item.setOnClickListener((v) -> {
            Intent intent = new Intent(v.getContext(), ProductSearchViewActivity.class);
            intent.putExtra("category", category.getId());
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return categoryList != null ? categoryList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView categoryName;
        ImageView categoryImage;
        ConstraintLayout item;

        public ViewHolder(@NonNull View itemView, LayoutCategoryItemBinding binding) {
            super(itemView);
            categoryImage = binding.ivCategoryImage;
            categoryName = binding.tvCategoryName;
            item = binding.clCategoryItem;
        }

    }
}
