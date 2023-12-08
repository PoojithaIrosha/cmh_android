package lk.cmh.app.ceylonmarkethub.ui.fragment.home;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.List;

import lk.cmh.app.ceylonmarkethub.data.adapter.rv.CategoriesRvAdapter;
import lk.cmh.app.ceylonmarkethub.data.adapter.rv.ProductsRvAdapter;
import lk.cmh.app.ceylonmarkethub.data.adapter.rv.TopProductsRvAdapter;
import lk.cmh.app.ceylonmarkethub.data.model.category.Category;
import lk.cmh.app.ceylonmarkethub.data.model.product.Product;
import lk.cmh.app.ceylonmarkethub.databinding.FragmentHomeBinding;
import lk.cmh.app.ceylonmarkethub.ui.activity.product.search.SearchProductsActivity;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;
    private ProductsRvAdapter productsRvAdapter;
    private TopProductsRvAdapter topProductsRvAdapter;
    private CategoriesRvAdapter categoriesRvAdapter;
    private static final String TAG = HomeFragment.class.getSimpleName();

    public HomeFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        binding = FragmentHomeBinding.inflate(getLayoutInflater());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding.nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            private final Handler handler = new Handler();
            private boolean isScrolling = false;
            private int threshold = 50;


            @Override
            public void onScrollChange(@NonNull NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (isScrolling) {
                    handler.removeCallbacksAndMessages(null);
                }

                handler.postDelayed(() -> {
                    if (scrollY > oldScrollY) {
                        Log.i(TAG, "Scroll DOWN");
                    } else if (scrollY < oldScrollY) {
                        Log.i(TAG, "Scroll UP");
                    }

                    if (scrollY == 0) {
                        Log.i(TAG, "TOP SCROLL");
                    }


                    if (scrollY >= (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight() - threshold)) {
                        Log.i(TAG, "BOTTOM SCROLL");
                        if (!homeViewModel.isLast && !homeViewModel.isLoading.getValue()) {
                            homeViewModel.getAllProducts();
                        }
                    }

                    isScrolling = false;
                }, 300);
                isScrolling = true;
            }

        });

        productsRvAdapter = new ProductsRvAdapter();
        categoriesRvAdapter = new CategoriesRvAdapter();
        topProductsRvAdapter = new TopProductsRvAdapter();

        loadData();

        binding.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                homeViewModel.refresh();
                binding.rvProducts.setPadding(0, 0, 0, 0);
                binding.swipeRefreshLayout.setRefreshing(false);
            }
        });

        binding.searchBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), SearchProductsActivity.class));
            }
        });

        return binding.getRoot();
    }

    private void loadData() {

        homeViewModel.getCategoriesLiveData().observe(getViewLifecycleOwner(), new Observer<List<Category>>() {
            @Override
            public void onChanged(List<Category> categories) {
                if (categories != null && !categories.isEmpty()) {
                    categoriesRvAdapter.setCategoryList(categories);
                    binding.rvCategories.setAdapter(categoriesRvAdapter);
                }
            }
        });

        homeViewModel.getTopProductsLiveData().observe(getViewLifecycleOwner(), new Observer<List<Product>>() {
            @Override
            public void onChanged(List<Product> products) {
                Log.i(TAG, products.toString());
                if (products != null && !products.isEmpty()) {
                    topProductsRvAdapter.setProductList(products);
                    binding.rvTopProducts.setAdapter(topProductsRvAdapter);
                }
            }
        });

        homeViewModel.getProductsLiveData().observe(getViewLifecycleOwner(), new Observer<List<Product>>() {
            @Override
            public void onChanged(List<Product> products) {
                if (binding.rvProducts.getAdapter() == null || homeViewModel.currentPage == 0) {
                    productsRvAdapter.setProductList(products);
                    binding.rvProducts.setAdapter(productsRvAdapter);
                } else {
                    ProductsRvAdapter adapter = (ProductsRvAdapter) binding.rvProducts.getAdapter();
                    int newCount = products.size();
                    int oldCount = adapter.getProductList().size();

                    adapter.getProductList().addAll(products);
                    for (int x = 1; x <= newCount; x++) {
                        binding.rvProducts.getAdapter().notifyItemInserted((oldCount - 1) + x);
                    }
                }
            }
        });

        homeViewModel.isLoading.observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isLoading) {
                if (isLoading) {
                    binding.tvLoading.setVisibility(View.VISIBLE);
                } else {
                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                        binding.tvLoading.setVisibility(View.INVISIBLE);

                        if (homeViewModel.isLast) {
                            binding.tvLoading.setVisibility(View.GONE);

                            TypedValue typedValue = new TypedValue();
                            getActivity().getTheme().resolveAttribute(android.R.attr.actionBarSize, typedValue, true);
                            int actionBarSize = getResources().getDimensionPixelSize(typedValue.resourceId);

                            binding.rvProducts.setPadding(binding.rvProducts.getPaddingLeft(), binding.rvProducts.getPaddingTop(), binding.rvProducts.getPaddingRight(), actionBarSize);
                        }
                    }, 3000);
                }
            }
        });

    }
}