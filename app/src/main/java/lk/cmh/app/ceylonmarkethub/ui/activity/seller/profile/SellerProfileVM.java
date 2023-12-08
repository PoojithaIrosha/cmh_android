package lk.cmh.app.ceylonmarkethub.ui.activity.seller.profile;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.viewmodel.CreationExtras;
import androidx.lifecycle.viewmodel.ViewModelInitializer;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.List;

import kotlin.jvm.functions.Function1;
import lk.cmh.app.ceylonmarkethub.CMH;
import lk.cmh.app.ceylonmarkethub.R;
import lk.cmh.app.ceylonmarkethub.data.model.product.Product;
import lk.cmh.app.ceylonmarkethub.data.model.user.Seller;
import lk.cmh.app.ceylonmarkethub.data.repository.GeocodingRepository;
import lk.cmh.app.ceylonmarkethub.data.repository.ProductRepository;
import lk.cmh.app.ceylonmarkethub.data.repository.UserRepository;
import lk.cmh.app.ceylonmarkethub.data.util.RetrofitUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SellerProfileVM extends ViewModel {

    private static final String TAG = SellerProfileVM.class.getSimpleName();
    private MutableLiveData<Seller> seller = new MutableLiveData<>();
    private UserRepository userRepository;
    private MutableLiveData<LatLng> latLng = new MutableLiveData<>();
    private MutableLiveData<List<Product>> products = new MutableLiveData<>();
    private GeocodingRepository geocodingRepository;
    private ProductRepository productRepository;

    private Retrofit gMapRetrofit;

    public SellerProfileVM(Retrofit gMapRetrofit, UserRepository userRepository, ProductRepository productRepository, GeocodingRepository geocodingRepository) {
        this.gMapRetrofit = gMapRetrofit;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.geocodingRepository = geocodingRepository;
    }

    public static ViewModelInitializer<SellerProfileVM> initializer = new ViewModelInitializer<>(SellerProfileVM.class, new Function1<CreationExtras, SellerProfileVM>() {
        @Override
        public SellerProfileVM invoke(CreationExtras creationExtras) {
            Retrofit gMapRetrofit = new Retrofit.Builder()
                    .baseUrl("https://maps.googleapis.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            UserRepository userRepository = RetrofitUtil.getInstance().getRepository(UserRepository.class);
            ProductRepository productRepository = RetrofitUtil.getInstance().getRepository(ProductRepository.class, CMH.getInstance().getSharedPreferences("auth", Context.MODE_PRIVATE));
            GeocodingRepository geocodingRepository = gMapRetrofit.create(GeocodingRepository.class);

            return new SellerProfileVM(gMapRetrofit, userRepository, productRepository, geocodingRepository);
        }
    });

    public void loadSellersProducts(long id) {
        productRepository.getSellersEnabledProducts(id).enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                products.setValue(response.body());
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public void loadSeller(long id) {
        userRepository.getSeller(id).enqueue(new Callback<Seller>() {
            @Override
            public void onResponse(Call<Seller> call, Response<Seller> response) {
                if (response.isSuccessful()) {
                    if (response.isSuccessful()) {
                        Seller s = response.body();
                        seller.setValue(s);

                        if (s.getAddress() != null) {
                            String address = s.getAddress().getAddressLine1() + ", " + s.getAddress().getAddressLine2() + ", " + s.getAddress().getCity();

                            new Handler(Looper.getMainLooper()).post(() -> {
//                            String address = "1600 Amphitheatre Parkway, Mountain View, CA";
                                Log.i(TAG, "Address " + address);
                                String apiKey = CMH.getInstance().getString(R.string.googleMapsApi);
                                Log.i(TAG, "API KEY: " + apiKey);

                                geocodingRepository.getLatLng(address, apiKey).enqueue(new Callback<JsonObject>() {
                                    @Override
                                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                                        if (response.isSuccessful()) {
                                            Log.i(TAG, response.body().toString());

                                            JsonObject jsonObject = response.body();
                                            JsonArray results = jsonObject.getAsJsonArray("results");
                                            if (results.size() > 0) {
                                                JsonObject resultObject = results.get(0).getAsJsonObject(); // Assuming there is at least one result

                                                JsonObject geometry = resultObject.getAsJsonObject("geometry");
                                                JsonObject location = geometry.getAsJsonObject("location");

                                                double latitude = location.getAsJsonPrimitive("lat").getAsDouble();
                                                double longitude = location.getAsJsonPrimitive("lng").getAsDouble();

                                                Log.i(TAG, "Latitude: " + latitude);
                                                Log.i(TAG, "Longitude: " + longitude);

                                                latLng.setValue(new LatLng(latitude, longitude));
                                            }
                                        } else {
                                            try {
                                                System.out.println(response.errorBody().string());
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
                            });
                        }

                    } else {
                        try {
                            System.out.println(response.errorBody().string());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                } else {
                    try {
                        Log.i(TAG, response.errorBody().string());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void onFailure(Call<Seller> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public MutableLiveData<Seller> getSeller() {
        return seller;
    }

    public MutableLiveData<LatLng> getLatLng() {
        return latLng;
    }

    public MutableLiveData<List<Product>> getProducts() {
        return products;
    }
}
