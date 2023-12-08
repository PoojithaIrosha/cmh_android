package lk.cmh.app.ceylonmarkethub.data.adapter.rv;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.util.List;

import lk.cmh.app.ceylonmarkethub.CMH;
import lk.cmh.app.ceylonmarkethub.R;
import lk.cmh.app.ceylonmarkethub.data.model.product.Product;
import lk.cmh.app.ceylonmarkethub.data.model.seller.SellerOrderItemDto;
import lk.cmh.app.ceylonmarkethub.data.repository.SellerRepository;
import lk.cmh.app.ceylonmarkethub.data.util.RetrofitUtil;
import lk.cmh.app.ceylonmarkethub.databinding.LayoutSellerMyOrdersItemBinding;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SellerMyOrdersItemRvAdapter extends RecyclerView.Adapter<SellerMyOrdersItemRvAdapter.ViewHolder> {

    private static final String TAG = SellerMyOrdersItemRvAdapter.class.getSimpleName();
    private List<SellerOrderItemDto> orderItemDtos;
    private FirebaseStorage storage;

    private SellerRepository sellerRepository;
    private boolean isUserInteraction = false;

    public SellerMyOrdersItemRvAdapter(List<SellerOrderItemDto> orderItemDtos) {
        this.orderItemDtos = orderItemDtos;
        storage = FirebaseStorage.getInstance();
        this.sellerRepository = RetrofitUtil.getInstance().getRepository(SellerRepository.class, CMH.getInstance().getSharedPreferences("auth", Context.MODE_PRIVATE));
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        LayoutSellerMyOrdersItemBinding binding = LayoutSellerMyOrdersItemBinding.inflate(inflater);
        binding.getRoot().setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        return new ViewHolder(binding.getRoot(), binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SellerOrderItemDto sellerOrderItemDto = orderItemDtos.get(position);
        Product product = sellerOrderItemDto.getProduct();

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                holder.status.getContext(),
                R.array.order_status_array,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        holder.status.setAdapter(adapter);

        switch (sellerOrderItemDto.getStatus()) {
            case PENDING:
                holder.status.setSelection(0);
                break;
            case CONFIRMED:
                holder.status.setSelection(1);
                break;
            case CANCELLED:
                holder.status.setSelection(2);
                break;
            case DELIVERED:
                holder.status.setSelection(3);
                break;
        }

        holder.status.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                isUserInteraction = true;
                return false;
            }
        });

        holder.status.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isUserInteraction) {
                    String status = (String) parent.getItemAtPosition(position);
                    sellerRepository.updateOrderStatus(sellerOrderItemDto.getId(), status).enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            Toast.makeText(parent.getContext(), "Order status updated", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {

                        }
                    });

                    isUserInteraction = false;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        holder.name.setText(product.getName());
        holder.price.setText("LKR " + String.format("%.2f", sellerOrderItemDto.getPrice()));
        holder.qty.setText("x " + sellerOrderItemDto.getQuantity());

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
        return orderItemDtos != null ? orderItemDtos.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView name, price, qty;
        Spinner status;

        public ViewHolder(@NonNull View itemView, LayoutSellerMyOrdersItemBinding binding) {
            super(itemView);
            name = binding.tvProductTitle;
            price = binding.tvPrice;
            qty = binding.tvQty;
            productImage = binding.ivProductImage;
            status = binding.spinnerStatus;
        }
    }

}
