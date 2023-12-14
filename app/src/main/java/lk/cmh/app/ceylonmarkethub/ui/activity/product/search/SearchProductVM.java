package lk.cmh.app.ceylonmarkethub.ui.activity.product.search;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import lk.cmh.app.ceylonmarkethub.data.model.product.PageableDto;
import lk.cmh.app.ceylonmarkethub.data.model.product.Product;
import lk.cmh.app.ceylonmarkethub.data.repository.ProductRepository;
import lk.cmh.app.ceylonmarkethub.data.util.RetrofitUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchProductVM extends ViewModel {

    private static final String TAG = SearchProductVM.class.getSimpleName();
    private ProductRepository productRepository;
    private MutableLiveData<List<Product>> searchLiveData = new MutableLiveData<>();
    public static final int SIZE = 5;
    public int currentPage = 0;
    public boolean isLast;
    public int totalPages;
    public MutableLiveData<Boolean> isLoading = new MutableLiveData<>();

    public SearchProductVM() {
        productRepository = RetrofitUtil.getInstance().getRepository(ProductRepository.class);
    }

    public MutableLiveData<List<Product>> getSearchLiveData() {
        return searchLiveData;
    }

    public void getSearchProduct(String text) {
        productRepository.searchProducts(0, SIZE, text, "", "").enqueue(new Callback<PageableDto>() {
            @Override
            public void onResponse(Call<PageableDto> call, Response<PageableDto> response) {
                searchLiveData.setValue(response.body().getContent());
            }

            @Override
            public void onFailure(Call<PageableDto> call, Throwable t) {
                Log.i(TAG, t.toString());
            }
        });
    }

    public void searchProducts(String text) {
        isLoading.setValue(true);

        productRepository.searchProducts(currentPage, SIZE, text, "", "").enqueue(new Callback<PageableDto>() {
            @Override
            public void onResponse(Call<PageableDto> call, Response<PageableDto> response) {
                if (response.isSuccessful()) {
                    PageableDto dto = response.body();
                    totalPages = dto.getTotalPages();
                    isLast = dto.isLast();

                    if (totalPages >= currentPage) {
                        List<Product> currentProducts = searchLiveData.getValue();
                        List<Product> newProducts = dto.getContent();
                        currentProducts = newProducts;
                        searchLiveData.setValue(currentProducts);
                        currentPage++;
                    }
                }

                isLoading.setValue(false);
            }

            @Override
            public void onFailure(Call<PageableDto> call, Throwable t) {
                Log.i(TAG, t.toString());
                isLoading.setValue(false);
            }
        });
    }

    public void searchProducts(long category) {
        isLoading.setValue(true);

        productRepository.getProductsByCategory(category, currentPage, SIZE).enqueue(new Callback<PageableDto>() {
            @Override
            public void onResponse(Call<PageableDto> call, Response<PageableDto> response) {
                if (response.isSuccessful()) {
                    PageableDto dto = response.body();
                    totalPages = dto.getTotalPages();
                    isLast = dto.isLast();

                    if (totalPages >= currentPage) {
                        List<Product> currentProducts = searchLiveData.getValue();
                        List<Product> newProducts = dto.getContent();
                        currentProducts = newProducts;
                        searchLiveData.setValue(currentProducts);
                        currentPage++;
                    }
                }

                isLoading.setValue(false);
            }

            @Override
            public void onFailure(Call<PageableDto> call, Throwable t) {
                Log.i(TAG, t.toString());
                isLoading.setValue(false);
            }
        });
    }

    public void searchProducts(String text, String priceMin, String priceMax) {
        isLoading.setValue(true);

        productRepository.searchProducts(currentPage, SIZE, text, priceMin, priceMax).enqueue(new Callback<PageableDto>() {
            @Override
            public void onResponse(Call<PageableDto> call, Response<PageableDto> response) {
                if (response.isSuccessful()) {
                    Log.i(TAG, "Products: " + response.body().getContent());
                    PageableDto dto = response.body();
                    totalPages = dto.getTotalPages();
                    isLast = dto.isLast();

                    if (totalPages >= currentPage) {
                        List<Product> currentProducts = searchLiveData.getValue();
                        List<Product> newProducts = dto.getContent();
                        currentProducts = newProducts;
                        searchLiveData.setValue(currentProducts);
                        currentPage++;
                    }
                }

                isLoading.setValue(false);
            }

            @Override
            public void onFailure(Call<PageableDto> call, Throwable t) {
                Log.i(TAG, t.toString());
                isLoading.setValue(false);
            }
        });
    }

}
