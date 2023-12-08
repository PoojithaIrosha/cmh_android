package lk.cmh.app.ceylonmarkethub.data.repository;

import com.google.gson.JsonObject;

import lk.cmh.app.ceylonmarkethub.data.model.auth.login.LoginReqDto;
import lk.cmh.app.ceylonmarkethub.data.model.auth.register.RegisterReqDto;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthRepository {

    @POST("auth/login")
    Call<JsonObject> login(@Body LoginReqDto loginReqDto);

    @POST("auth/refresh-token")
    Call<JsonObject> refreshToken(@Body JsonObject jsonObject);

    @POST("auth/google")
    Call<JsonObject> googleLogin(@Body JsonObject jsonObject);

    @POST("auth/register")
    Call<JsonObject> register(@Body RegisterReqDto registerReqDto);

}
