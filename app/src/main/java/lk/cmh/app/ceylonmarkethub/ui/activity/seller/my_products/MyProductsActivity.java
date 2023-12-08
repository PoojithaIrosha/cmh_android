package lk.cmh.app.ceylonmarkethub.ui.activity.seller.my_products;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;

import lk.cmh.app.ceylonmarkethub.R;
import lk.cmh.app.ceylonmarkethub.databinding.ActivityMyProductsBinding;
import lk.cmh.app.ceylonmarkethub.ui.fragment.seller.products.add_product.AddProductFragment;
import lk.cmh.app.ceylonmarkethub.ui.fragment.seller.products.product_list.ProductListFragment;

public class MyProductsActivity extends AppCompatActivity {

    private ActivityMyProductsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyProductsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.mainTopAppBar.setNavigationOnClickListener(v -> {
            finish();
        });

        loadFragment(new ProductListFragment());

        binding.mainTopAppBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId() == R.id.smpAddProduct) {
                    loadFragment(new AddProductFragment());
                }
                return false;
            }
        });
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = supportFragmentManager.beginTransaction();
        transaction.replace(R.id.fragmentContainerView, fragment);
        transaction.commit();
    }
}