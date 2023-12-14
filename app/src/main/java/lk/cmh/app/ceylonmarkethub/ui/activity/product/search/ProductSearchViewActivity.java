package lk.cmh.app.ceylonmarkethub.ui.activity.product.search;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.util.List;

import lk.cmh.app.ceylonmarkethub.data.adapter.rv.ProductsRvAdapter;
import lk.cmh.app.ceylonmarkethub.data.model.product.Product;
import lk.cmh.app.ceylonmarkethub.databinding.ActivityProductSearchViewBinding;

public class ProductSearchViewActivity extends AppCompatActivity {

    private static final String TAG = ProductSearchViewActivity.class.getSimpleName();
    private ActivityProductSearchViewBinding binding;
    private SearchProductVM productVM;
    private String search;
    private long category;
    private BottomSheetBehavior<View> bottomSheetBehavior;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProductSearchViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        bottomSheetBehavior = BottomSheetBehavior.from(binding.standardBottomSheet);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        productVM = new ViewModelProvider(this).get(SearchProductVM.class);

        category = getIntent().getExtras().getLong("category", 0);
        if (category != 0) {
            productVM.searchProducts(category);
        }else {
            search = getIntent().getExtras().getString("search");
            productVM.searchProducts(search);
            binding.searchBox.setText(search);
        }

        binding.searchBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProductSearchViewActivity.this, SearchProductsActivity.class));
            }
        });

        binding.btnFilter.setOnClickListener(v -> {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        });

        binding.mainTopAppBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        productVM.getSearchLiveData().observe(this, new Observer<List<Product>>() {
            @Override
            public void onChanged(List<Product> products) {

                if (products == null || products.size() < 1) {
                    binding.rvSearchProducts.setVisibility(View.GONE);
                    binding.animNoResultFound.setVisibility(View.VISIBLE);
                } else {
                    if (binding.rvSearchProducts.getAdapter() == null || productVM.currentPage == 0) {
                        ProductsRvAdapter adapter = new ProductsRvAdapter();
                        adapter.setProductList(products);
                        binding.rvSearchProducts.setAdapter(adapter);
                    } else {
                        ProductsRvAdapter adapter = (ProductsRvAdapter) binding.rvSearchProducts.getAdapter();
                        int newCount = products.size();
                        int oldCount = adapter.getProductList().size();

                        adapter.getProductList().addAll(products);
                        for (int x = 1; x <= newCount; x++) {
                            binding.rvSearchProducts.getAdapter().notifyItemInserted((oldCount - 1) + x);
                        }
                    }
                }


            }
        });

        binding.rvSearchProducts.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                GridLayoutManager layoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                if (!productVM.isLoading.getValue() && !productVM.isLast) {
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                            && firstVisibleItemPosition >= 0
                            && totalItemCount >= productVM.SIZE) {
                        Log.i(TAG, "Loading new products");
                        productVM.searchProducts(search);
                    }
                }
            }
        });

        productVM.isLoading.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isLoading) {
                if (isLoading) {
                    binding.tvLoadingAnim.setVisibility(View.VISIBLE);
                    binding.tvLoading.setVisibility(View.VISIBLE);
                } else {
                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                        binding.tvLoading.setVisibility(View.INVISIBLE);
                        binding.tvLoadingAnim.setVisibility(View.INVISIBLE);
                    }, 1000);
                }
            }
        });

        binding.btnApply.setOnClickListener(v -> {
            String priceMin = binding.etPriceMin.getText().toString().trim();
            String priceMax = binding.etPriceMax.getText().toString().trim();

            productVM.currentPage = 0;
            productVM.searchProducts(search, priceMin, priceMax);
        });
    }
}