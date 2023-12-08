package lk.cmh.app.ceylonmarkethub.ui.fragment.home;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import lk.cmh.app.ceylonmarkethub.data.model.category.Category;
import lk.cmh.app.ceylonmarkethub.data.model.product.PageableDto;
import lk.cmh.app.ceylonmarkethub.data.model.product.Product;
import lk.cmh.app.ceylonmarkethub.data.repository.CategoryRepository;
import lk.cmh.app.ceylonmarkethub.data.repository.ProductRepository;
import lk.cmh.app.ceylonmarkethub.data.util.RetrofitUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeViewModel extends ViewModel {

    private static final String TAG = HomeViewModel.class.getSimpleName();
    private CategoryRepository categoryRepository;
    private ProductRepository productRepository;
    private MutableLiveData<List<Category>> categoriesLiveData = new MutableLiveData<>();
    private MutableLiveData<List<Product>> topProductsLiveData = new MutableLiveData<>();
    private MutableLiveData<List<Product>> productsLiveData = new MutableLiveData<>();
    public int currentPage = 0;
    public int pageSize = 10;
    public int totalPages;
    public boolean isLast;
    public MutableLiveData<Boolean> isLoading = new MutableLiveData<>();

    public HomeViewModel() {
        categoryRepository = RetrofitUtil.getInstance().getRepository(CategoryRepository.class);
        productRepository = RetrofitUtil.getInstance().getRepository(ProductRepository.class);
        if (categoriesLiveData.getValue() == null)
            getAllCategories();

        if (topProductsLiveData.getValue() == null)
            getTopProducts();

        if (productsLiveData.getValue() == null)
            getAllProducts();

    }

    public LiveData<List<Category>> getCategoriesLiveData() {
        return categoriesLiveData;
    }

    public LiveData<List<Product>> getTopProductsLiveData() {
        return topProductsLiveData;
    }

    public LiveData<List<Product>> getProductsLiveData() {
        return productsLiveData;
    }

    private void getAllCategories() {
        categoryRepository.getCategories().enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                categoriesLiveData.setValue(response.body());
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                Log.i(TAG, t.getMessage());
            }
        });
    }

    private void getTopProducts() {
        productRepository.getTopProducts().enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                topProductsLiveData.setValue(response.body());
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Log.i(TAG, t.getMessage());
            }
        });
    }

    public void getAllProducts() {
        isLoading.setValue(true);
        productRepository.getAllProducts(currentPage, pageSize).enqueue(new Callback<PageableDto>() {
            @Override
            public void onResponse(Call<PageableDto> call, Response<PageableDto> response) {
                if (response.isSuccessful()) {
                    PageableDto body = response.body();
                    totalPages = body.getTotalPages();
                    isLast = body.isLast();

                    if (totalPages >= currentPage) {
                        List<Product> currentProducts = productsLiveData.getValue();
                        List<Product> newProducts = body.getContent();
                        currentProducts = newProducts;
                        productsLiveData.setValue(currentProducts);
                        currentPage++;
                    }
                }
                isLoading.setValue(false);
            }

            @Override
            public void onFailure(Call<PageableDto> call, Throwable t) {
                Log.i(TAG, t.getMessage());
                isLoading.setValue(false);
            }
        });
    }

    public void refresh() {
        currentPage = 0;
        totalPages = 0;
        isLast = false;
        isLoading.setValue(false);

        if(categoriesLiveData.getValue() != null)
            categoriesLiveData.getValue().clear();

        if(topProductsLiveData.getValue() != null)
            topProductsLiveData.getValue().clear();

        if(productsLiveData.getValue() != null)
            productsLiveData.getValue().clear();

        getAllCategories();
        getTopProducts();
        getAllProducts();
    }

}
