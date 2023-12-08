package lk.cmh.app.ceylonmarkethub.data.util;

import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import lk.cmh.app.ceylonmarkethub.data.interceptor.AuthInterceptor;
import lk.cmh.app.ceylonmarkethub.data.model.ErrorResponse;
import lk.cmh.app.ceylonmarkethub.data.model.auth.login.LoginRespDto;
import lk.cmh.app.ceylonmarkethub.data.repository.AuthRepository;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitUtil {

    private static final String TAG = RetrofitUtil.class.getSimpleName();
    private static Retrofit retrofit;
    private static RetrofitUtil instance;
    //    private static final String baseUrl = "http://10.0.2.2:8080/api/v1/";
    private static String baseUrl = "http://192.168.1.3:8080/api/v1/";
//    private static final String baseUrl = "http://192.168.43.144:8080/api/v1/";
//    private static final String baseUrl = "https://fec5-112-134-190-182.ngrok-free.app/api/v1/";

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

    private RetrofitUtil() {
    }

    public static RetrofitUtil getInstance() {
        if (instance == null) {
            instance = new RetrofitUtil();
        }
        return instance;
    }

    public static RetrofitUtil getInstance(String baseUrl) {
        RetrofitUtil.baseUrl = baseUrl;
        if (instance == null) {
            instance = new RetrofitUtil();
        }
        return instance;
    }

    public <T> T getRepository(Class<T> repository) {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return retrofit.create(repository);
    }

    public <T> T getRepository(Class<T> repository, SharedPreferences preferences) {
        String accessToken = preferences.getString("accessToken", "");
        String refreshToken = preferences.getString("refreshToken", "");

        if (retrofit == null) {

            if (!accessToken.isEmpty()) {
                long expiresIn = preferences.getLong("expiresIn", 0);
                if (expiresIn != 0 && new Date(System.currentTimeMillis()).after(new Date(expiresIn))) {
                    AuthRepository authRepository = getRepository(AuthRepository.class);

                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("refreshToken", refreshToken);

                    authRepository.refreshToken(jsonObject).enqueue(new Callback<JsonObject>() {
                        @Override
                        public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                            if (response.isSuccessful()) {

                                JsonObject body = response.body();
                                String email = body.get("email").getAsString();
                                String accessToken = body.get("accessToken").getAsString();
                                String refreshToken = body.get("refreshToken").getAsString();
                                String firebaseToken = body.get("firebaseToken").getAsString();
                                String expiresIn = body.get("expiresIn").getAsString();
                                String role = body.get("role").getAsString();
                                Date expireDate = null;
                                try {
                                    expireDate = dateFormat.parse(expiresIn);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                LoginRespDto loginRespDto = new LoginRespDto(email, accessToken, refreshToken, firebaseToken, expireDate, role);

                                SharedPreferences.Editor edit = preferences.edit();
                                edit.putString("email", email);
                                edit.putString("accessToken", accessToken);
                                edit.putString("refreshToken", refreshToken);
                                edit.putString("firebaseToken", firebaseToken);
                                edit.putLong("expiresIn", expireDate.getTime());
                                edit.apply();

                                getRepository(repository, preferences);
                            } else {
                                System.out.println(response.body());
                                try {
                                    JSONObject errorJson = new JSONObject(response.errorBody().string());
                                    String type = errorJson.getString("type");
                                    String title = errorJson.getString("title");
                                    String detail = errorJson.getString("detail");
                                    int status = errorJson.getInt("status");

                                    ErrorResponse errorResponse = new ErrorResponse(type, title, status, detail);
                                    Log.i(TAG, errorResponse.toString());
                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<JsonObject> call, Throwable t) {
                            t.printStackTrace();
                        }
                    });

                } else {
                    OkHttpClient okHttpClient = new OkHttpClient.Builder().addInterceptor(new AuthInterceptor(accessToken)).build();
                    retrofit = new Retrofit.Builder()
                            .baseUrl(baseUrl)
                            .client(okHttpClient)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
                }
            }
        } else {
            retrofit = retrofit.newBuilder().client(new OkHttpClient.Builder().addInterceptor(new AuthInterceptor(accessToken)).build()).build();
        }

        return retrofit.create(repository);
    }
}
