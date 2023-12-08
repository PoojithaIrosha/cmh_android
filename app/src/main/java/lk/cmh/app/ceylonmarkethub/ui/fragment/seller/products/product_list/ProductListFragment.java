package lk.cmh.app.ceylonmarkethub.ui.fragment.seller.products.product_list;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.List;

import lk.cmh.app.ceylonmarkethub.data.adapter.rv.SellerProductsRvAdapter;
import lk.cmh.app.ceylonmarkethub.data.model.product.Product;
import lk.cmh.app.ceylonmarkethub.data.repository.ProductRepository;
import lk.cmh.app.ceylonmarkethub.data.util.RetrofitUtil;
import lk.cmh.app.ceylonmarkethub.databinding.FragmentProductListBinding;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductListFragment extends Fragment {

    private FragmentProductListBinding binding;
    private ProductRepository productRepository;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = FragmentProductListBinding.inflate(getLayoutInflater());
        productRepository = RetrofitUtil.getInstance().getRepository(ProductRepository.class, getActivity().getSharedPreferences("auth", Context.MODE_PRIVATE));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        loadProducts();

        binding.refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadProducts();
                binding.refresh.setRefreshing(false);
            }
        });

        return binding.getRoot();
    }

    private void loadProducts() {
        productRepository.getSellersProducts().enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                SellerProductsRvAdapter adapter = new SellerProductsRvAdapter(response.body(), productRepository);
                binding.rvSellerProducts.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
}