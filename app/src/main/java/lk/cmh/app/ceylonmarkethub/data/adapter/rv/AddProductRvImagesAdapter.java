package lk.cmh.app.ceylonmarkethub.data.adapter.rv;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import lk.cmh.app.ceylonmarkethub.R;
import lk.cmh.app.ceylonmarkethub.databinding.LayoutAddProductImageBinding;

public class AddProductRvImagesAdapter extends RecyclerView.Adapter<AddProductRvImagesAdapter.ViewHolder> {

    private LayoutAddProductImageBinding binding;
    private List<Uri> uriList;

    public AddProductRvImagesAdapter(List<Uri> uriList) {
        this.uriList = uriList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater from = LayoutInflater.from(parent.getContext());
        binding = LayoutAddProductImageBinding.inflate(from);
        return new ViewHolder(binding.getRoot(), binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Uri uri = uriList.get(position);

        Picasso.get()
                .load(uri)
                .placeholder(R.drawable.placeholder_category_item_image)
                .fit()
                .centerCrop()
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return uriList != null ? uriList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;

        public ViewHolder(@NonNull View itemView, LayoutAddProductImageBinding binding) {
            super(itemView);
            imageView = binding.imageView2;
        }
    }

    public List<Uri> getUriList() {
        return uriList;
    }

    public void setUriList(List<Uri> uriList) {
        this.uriList = uriList;
    }
}
