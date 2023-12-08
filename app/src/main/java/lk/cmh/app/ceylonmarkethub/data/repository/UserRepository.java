package lk.cmh.app.ceylonmarkethub.data.repository;

import com.google.gson.JsonObject;

import java.util.Map;

import lk.cmh.app.ceylonmarkethub.data.model.user.Seller;
import lk.cmh.app.ceylonmarkethub.data.model.user.User;
import lk.cmh.app.ceylonmarkethub.data.model.user.UserAddress;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface UserRepository {

    @GET("users/me")
    Call<User> getMe();

    @GET("users/me/address")
    Call<UserAddress> getAddress();

    @GET("users/seller/{id}")
    Call<Seller> getSeller(@Path("id") long id);

    @POST("users/me/address")
    Call<UserAddress> updateAddress(@Body UserAddress userAddress);

    @PUT("users/me/become-seller")
    Call<Map<String, String>> becomeSeller();

    @PUT("users/me/update-profile")
    Call<User> updateProfilePic(@Body JsonObject uri);

    @PUT("users/me/update-user")
    Call<User> updateProfile(@Body JsonObject jsonObject);
}
