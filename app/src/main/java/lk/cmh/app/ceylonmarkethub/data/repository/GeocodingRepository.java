package lk.cmh.app.ceylonmarkethub.data.repository;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GeocodingRepository {

    @GET("maps/api/geocode/json")
    Call<JsonObject> getLatLng(@Query("address") String address, @Query("key") String apiKey);

}
