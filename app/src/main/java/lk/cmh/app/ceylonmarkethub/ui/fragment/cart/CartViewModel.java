package lk.cmh.app.ceylonmarkethub.ui.fragment.cart;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.viewmodel.ViewModelInitializer;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import lk.cmh.app.ceylonmarkethub.CMH;
import lk.cmh.app.ceylonmarkethub.data.model.cart.Cart;
import lk.cmh.app.ceylonmarkethub.data.model.cart.CartItem;
import lk.cmh.app.ceylonmarkethub.data.model.cart.CartItemDto;
import lk.cmh.app.ceylonmarkethub.data.model.cart.CartItemUpdateDto;
import lk.cmh.app.ceylonmarkethub.data.repository.CartRepository;
import lk.cmh.app.ceylonmarkethub.data.util.RetrofitUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartViewModel extends ViewModel {

    private static final String TAG = CartViewModel.class.getSimpleName();
    private MutableLiveData<Cart> cart = new MutableLiveData<>();
    private MutableLiveData<List<CartItem>> selectedItems = new MutableLiveData<>();
    private CartRepository cartRepository;

    public CartViewModel(CartRepository cartRepository) {
        this.cartRepository = cartRepository;
    }

    public static final ViewModelInitializer<CartViewModel> initializer = new ViewModelInitializer<>(CartViewModel.class, creationExtras -> {
        CMH instance = CMH.getInstance();
        SharedPreferences sharedPreferences = instance.getSharedPreferences("auth", Context.MODE_PRIVATE);
        CartRepository cartRepository = RetrofitUtil.getInstance().getRepository(CartRepository.class, sharedPreferences);
        return new CartViewModel(cartRepository);
    });

    public void loadCart() {

        cartRepository.getCart().enqueue(new Callback<Cart>() {
            @Override
            public void onResponse(Call<Cart> call, Response<Cart> response) {
                cart.setValue(response.body());
                selectedItems.setValue(new ArrayList<>());
            }

            @Override
            public void onFailure(Call<Cart> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public MutableLiveData<Cart> getCart() {
        return cart;
    }

    public void addSelectedItem(CartItem cartItem) {
        List<CartItem> value = selectedItems.getValue();
        if (value == null) {
            value = new ArrayList<>();
        }
        value.add(cartItem);
        selectedItems.setValue(value);
    }

    public void removeSelectedItem(CartItem item) {
        List<CartItem> value = selectedItems.getValue();
        if (value == null) {
            value = new ArrayList<>();
        }
        List<CartItem> collect = value.stream().filter(cartItem -> cartItem.getId() != item.getId()).collect(Collectors.toList());
        selectedItems.setValue(collect);
    }

    public MutableLiveData<List<CartItem>> getSelectedItems() {
        return selectedItems;
    }

    public void deleteItems(List<CartItem> items) {
        items.forEach(cartItem -> cartRepository.deleteItem(cartItem.getId()).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                loadCart();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                t.printStackTrace();
            }
        }));
    }

    public void updateCartItem(CartItemUpdateDto updateDto) {
        cartRepository.updateCartItem(updateDto).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                loadCart();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public void addToCart(CartItemDto cartItemDto, Runnable runnable) {
        cartRepository.addToCart(cartItemDto).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                runnable.run();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }
}
