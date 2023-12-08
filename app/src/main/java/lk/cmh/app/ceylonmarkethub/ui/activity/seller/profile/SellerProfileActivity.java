package lk.cmh.app.ceylonmarkethub.ui.activity.seller.profile;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import java.util.List;

import lk.cmh.app.ceylonmarkethub.R;
import lk.cmh.app.ceylonmarkethub.data.adapter.rv.ProductsRvAdapter;
import lk.cmh.app.ceylonmarkethub.data.model.product.Product;
import lk.cmh.app.ceylonmarkethub.data.model.user.Seller;
import lk.cmh.app.ceylonmarkethub.databinding.ActivitySellerProfileBinding;

public class SellerProfileActivity extends AppCompatActivity {

    private static final String TAG = SellerProfileActivity.class.getSimpleName();
    private ActivitySellerProfileBinding binding;
    private SellerProfileVM sellerProfileVM;

    private GoogleMap gMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySellerProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sellerProfileVM = new ViewModelProvider(this, ViewModelProvider.Factory.from(SellerProfileVM.initializer)).get(SellerProfileVM.class);

        long sellerId = getIntent().getExtras().getLong("sellerId", 0);
        if (sellerId == 0) {
            finish();
        }

        sellerProfileVM.loadSeller(sellerId);
        sellerProfileVM.loadSellersProducts(sellerId);

        sellerProfileVM.getProducts().observe(this, new Observer<List<Product>>() {
            @Override
            public void onChanged(List<Product> products) {
                ProductsRvAdapter productsRvAdapter = new ProductsRvAdapter();
                productsRvAdapter.setProductList(products);
                binding.rvSellerProducts.setAdapter(productsRvAdapter);
            }
        });

        sellerProfileVM.getSeller().observe(this, new Observer<Seller>() {
            @Override
            public void onChanged(Seller seller) {
                binding.sellerName.setText(seller.getFirstName() + " " + seller.getLastName());

                if(seller.getPicture() == null || seller.getPicture().isEmpty()) {
                    Log.i(TAG, "No Profile Picture");
                    binding.sellerProfileImage.setImageDrawable(getDrawable(R.drawable.shop_img));
                }else {
                    Picasso.get().load(seller.getPicture()).placeholder(R.drawable.placeholder_category_item_image).fit().centerCrop().into(binding.sellerProfileImage);
                }
            }
        });

        sellerProfileVM.getLatLng().observe(this, new Observer<LatLng>() {
            @Override
            public void onChanged(LatLng latLng) {
                if (gMap != null) {
                    gMap.addMarker(new MarkerOptions().position(latLng).title("Seller's Location"));
                    gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 50));
                }
            }
        });

        binding.mainTopAppBar.setNavigationOnClickListener(v -> {
            finish();
        });

        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap googleMap) {
                gMap = googleMap;
                gMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                gMap.getUiSettings().setZoomControlsEnabled(true);
            }
        });

    }
}