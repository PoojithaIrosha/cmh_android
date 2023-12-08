package lk.cmh.app.ceylonmarkethub.data.repository;

import com.google.gson.JsonObject;

import lk.cmh.app.ceylonmarkethub.data.model.wishlist.Wishlist;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface WishlistRepository {

    @GET("wishlist")
    Call<Wishlist> getWishlist();

    @POST("wishlist/{id}")
    Call<JsonObject> addToWishlist(@Path("id") long id);

    @DELETE("wishlist/{id}")
    Call<JsonObject> deleteItem(@Path("id") long id);


}
