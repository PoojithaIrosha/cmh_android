package lk.cmh.app.ceylonmarkethub.data.adapter.lv;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.util.List;

import lk.cmh.app.ceylonmarkethub.R;
import lk.cmh.app.ceylonmarkethub.data.model.product.Product;
import lk.cmh.app.ceylonmarkethub.databinding.LayoutSearchItemBinding;
import lk.cmh.app.ceylonmarkethub.ui.activity.product.product_view.ProductViewActivity;

public class SearchLvAdapter extends BaseAdapter {

    private static final String TAG = SearchLvAdapter.class.getSimpleName();
    private List<Product> productList;
    private FirebaseStorage storage;

    public SearchLvAdapter() {
        this.storage = FirebaseStorage.getInstance();
    }

    public SearchLvAdapter(List<Product> productList) {
        this.productList = productList;
        this.storage = FirebaseStorage.getInstance();
    }

    public void setProductList(List<Product> productList) {
        this.productList = productList;
    }

    @Override
    public int getCount() {
        return productList.size();
    }

    @Override
    public Product getItem(int position) {
        return productList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return productList.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        LayoutSearchItemBinding binding = LayoutSearchItemBinding.inflate(inflater);
        Product product = productList.get(position);

        storage.getReference("products/" + product.getProductImages().get(0).getUrl()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).placeholder(R.drawable.placeholder_category_item_image).fit().centerCrop().into(binding.productImage);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i(TAG, e.getMessage());
            }
        });
        binding.productName.setText(product.getName());

        binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ProductViewActivity.class);
                intent.putExtra("productId", product.getId());
                v.getContext().startActivity(intent);
            }
        });

        return binding.getRoot();
    }
}
