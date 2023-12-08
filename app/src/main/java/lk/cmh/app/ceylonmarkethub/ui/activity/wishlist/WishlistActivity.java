package lk.cmh.app.ceylonmarkethub.ui.activity.wishlist;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import lk.cmh.app.ceylonmarkethub.data.adapter.rv.WishlistItemRvAdapter;
import lk.cmh.app.ceylonmarkethub.data.model.wishlist.Wishlist;
import lk.cmh.app.ceylonmarkethub.databinding.ActivityWishlistBinding;

public class WishlistActivity extends AppCompatActivity {

    private ActivityWishlistBinding binding;
    private WishlistViewModel wishlistViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWishlistBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        wishlistViewModel = new ViewModelProvider(this, ViewModelProvider.Factory.from(WishlistViewModel.initializer)).get(WishlistViewModel.class);

        wishlistViewModel.loadWishlist();

        wishlistViewModel.getWishlist().observe(this, new Observer<Wishlist>() {
            @Override
            public void onChanged(Wishlist wishlist) {
                if(wishlist != null) {
                    binding.rvWishlistItems.setAdapter(new WishlistItemRvAdapter(wishlist.getWishListItems(), wishlistViewModel));
                }
            }
        });

        binding.backBtn.setOnClickListener(v -> {
            finish();
        });

        binding.srlReload.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                wishlistViewModel.loadWishlist();
                binding.srlReload.setRefreshing(false);
            }
        });
    }
}