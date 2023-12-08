package lk.cmh.app.ceylonmarkethub.data.repository;

import com.google.gson.JsonObject;

import lk.cmh.app.ceylonmarkethub.data.model.cart.Cart;
import lk.cmh.app.ceylonmarkethub.data.model.cart.CartItemDto;
import lk.cmh.app.ceylonmarkethub.data.model.cart.CartItemUpdateDto;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface CartRepository {

    @GET("cart")
    Call<Cart> getCart();

    @POST("cart")
    Call<JsonObject> addToCart(@Body CartItemDto updateDto);

    @PUT("cart")
    Call<JsonObject> updateCartItem(@Body CartItemUpdateDto updateDto);

    @DELETE("cart/{id}")
    Call<JsonObject> deleteItem(@Path("id") Long id);
}
