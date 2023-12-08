package lk.cmh.app.ceylonmarkethub.ui.activity.product.product_view;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;
import java.util.stream.Collectors;

import lk.cmh.app.ceylonmarkethub.CMH;
import lk.cmh.app.ceylonmarkethub.data.model.product.PageableDto;
import lk.cmh.app.ceylonmarkethub.data.model.product.Product;
import lk.cmh.app.ceylonmarkethub.data.model.product.ProductColors;
import lk.cmh.app.ceylonmarkethub.data.model.product.ProductSize;
import lk.cmh.app.ceylonmarkethub.data.model.review.Review;
import lk.cmh.app.ceylonmarkethub.data.model.review.ReviewDto;
import lk.cmh.app.ceylonmarkethub.data.repository.ProductRepository;
import lk.cmh.app.ceylonmarkethub.data.repository.ReviewRepository;
import lk.cmh.app.ceylonmarkethub.data.util.RetrofitUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ProductViewVM extends ViewModel {

    private static final String TAG = ProductViewVM.class.getSimpleName();
    private MutableLiveData<Product> product = new MutableLiveData<>();
    private ProductRepository productRepository;
    private ReviewRepository reviewRepository;
    private static final int C_PAGE = 0;
    private static final int C_PAGE_SIZE = 10;

    private MutableLiveData<Boolean> loading = new MutableLiveData<>();

    private MutableLiveData<List<Product>> recommendations = new MutableLiveData<>();

    public LiveData<Product> getProduct() {
        return product;
    }

    public MutableLiveData<Boolean> getLoading() {
        return loading;
    }

    public MutableLiveData<List<Product>> getRecommendations() {
        return recommendations;
    }

    private MutableLiveData<String> selectedColor = new MutableLiveData<>();

    private MutableLiveData<String> selectedSize = new MutableLiveData<>();
    private MutableLiveData<List<ProductColors>> productColors = new MutableLiveData<>();
    private MutableLiveData<List<ProductSize>> productSizes = new MutableLiveData<>();

    public ProductViewVM() {
        productRepository = RetrofitUtil.getInstance().getRepository(ProductRepository.class);
        reviewRepository = RetrofitUtil.getInstance().getRepository(ReviewRepository.class, CMH.getInstance().getSharedPreferences("auth", Context.MODE_PRIVATE));
    }

    public void loadProduct(long id) {
        productRepository.getProductById(id).enqueue(new Callback<Product>() {
            @Override
            public void onResponse(Call<Product> call, Response<Product> response) {
                product.setValue(response.body());
            }

            @Override
            public void onFailure(Call<Product> call, Throwable t) {
                Log.i(TAG, t.getMessage());
            }
        });
    }

    public void loadProductsByCategory(long category, long productId) {
        productRepository.getProductsByCategory(category, C_PAGE, C_PAGE_SIZE).enqueue(new Callback<PageableDto>() {
            @Override
            public void onResponse(Call<PageableDto> call, Response<PageableDto> response) {
                recommendations.setValue(response.body().getContent().stream().filter(p -> p.getId() != productId).collect(Collectors.toList()));
            }

            @Override
            public void onFailure(Call<PageableDto> call, Throwable t) {
                Log.i(TAG, t.getMessage());
            }
        });
    }

    public MutableLiveData<String> getSelectedColor() {
        return selectedColor;
    }

    public MutableLiveData<String> getSelectedSize() {
        return selectedSize;
    }

    public MutableLiveData<List<ProductColors>> getProductColors() {
        return productColors;
    }

    public MutableLiveData<List<ProductSize>> getProductSizes() {
        return productSizes;
    }

    public void addReview(long productId, String review, int rating) {
        ReviewDto reviewDto = new ReviewDto(review, rating, productId);
        reviewRepository.addReview(reviewDto).enqueue(new Callback<Review>() {
            @Override
            public void onResponse(Call<Review> call, Response<Review> response) {
                if(response.isSuccessful()) {
                    product.getValue().getReviews().add(response.body());
                    product.setValue(product.getValue());
                }
            }

            @Override
            public void onFailure(Call<Review> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
}
