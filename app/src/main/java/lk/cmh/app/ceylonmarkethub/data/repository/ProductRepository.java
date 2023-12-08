package lk.cmh.app.ceylonmarkethub.data.repository;

import com.google.gson.JsonObject;

import java.util.List;
import java.util.Map;

import lk.cmh.app.ceylonmarkethub.data.model.product.PageableDto;
import lk.cmh.app.ceylonmarkethub.data.model.product.Product;
import lk.cmh.app.ceylonmarkethub.data.model.product.ProductDto;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ProductRepository {
    @GET("products")
    Call<PageableDto> getAllProducts(@Query("page") int page, @Query("size") int size);

    @GET("products/top")
    Call<List<Product>> getTopProducts();

    @GET("products/{id}")
    Call<Product> getProductById(@Path("id") long id);

    @GET("products/search")
    Call<PageableDto> searchProducts(@Query("page") int page, @Query("size") int size, @Query("text") String text, @Query("priceMin") String priceMin, @Query("priceMax") String priceMax);

    @GET("products/category/{id}")
    Call<PageableDto> getProductsByCategory(@Path("id") long id, @Query("page") int page, @Query("size") int size);

    @GET("products/seller")
    Call<List<Product>> getSellersProducts();

    @GET("products/seller/enabled/{id}")
    Call<List<Product>> getSellersEnabledProducts(@Path("id") long id);

    @POST("products")
    Call<Product> saveProduct(@Body ProductDto productDto);

    @PUT("products/{id}")
    Call<Product> updateProduct(@Path("id") Long id, @Body ProductDto productDto);

    @PUT("products/enable/{id}")
    Call<JsonObject> changeStatus(@Path("id") Long id);
}
