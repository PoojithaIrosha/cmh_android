package lk.cmh.app.ceylonmarkethub.ui.activity.wishlist;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.viewmodel.ViewModelInitializer;

import com.google.gson.JsonObject;

import java.io.IOException;

import lk.cmh.app.ceylonmarkethub.CMH;
import lk.cmh.app.ceylonmarkethub.data.model.wishlist.Wishlist;
import lk.cmh.app.ceylonmarkethub.data.repository.WishlistRepository;
import lk.cmh.app.ceylonmarkethub.data.util.RetrofitUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WishlistViewModel extends ViewModel {
    private static final String TAG = WishlistViewModel.class.getSimpleName();
    private WishlistRepository wishlistRepository;

    private MutableLiveData<Wishlist> wishlist = new MutableLiveData<>();

    public WishlistViewModel(WishlistRepository wishlistRepository) {
        this.wishlistRepository = wishlistRepository;
    }

    public static final ViewModelInitializer<WishlistViewModel> initializer = new ViewModelInitializer<>(WishlistViewModel.class, creationExtras -> {
        CMH instance = CMH.getInstance();
        SharedPreferences sharedPreferences = instance.getSharedPreferences("auth", Context.MODE_PRIVATE);
        WishlistRepository wishlistRepository = RetrofitUtil.getInstance().getRepository(WishlistRepository.class, sharedPreferences);
        return new WishlistViewModel(wishlistRepository);
    });

    public void loadWishlist() {
        wishlistRepository.getWishlist().enqueue(new Callback<Wishlist>() {
            @Override
            public void onResponse(Call<Wishlist> call, Response<Wishlist> response) {
                Log.i(TAG, "onResponse: "+ response.body());
                wishlist.setValue(response.body());
            }

            @Override
            public void onFailure(Call<Wishlist> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public void addToWishlist(long productId) {
        wishlistRepository.addToWishlist(productId).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    loadWishlist();
                } else {
                    try {
                        Log.i(TAG, "onResponse: " + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public void removeItem(long productId) {
        wishlistRepository.deleteItem(productId).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    loadWishlist();
                } else {
                    try {
                        Log.i(TAG, "onResponse: " + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public MutableLiveData<Wishlist> getWishlist() {
        return wishlist;
    }
}
