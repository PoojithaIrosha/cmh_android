package lk.cmh.app.ceylonmarkethub.ui.activity.seller.my_products;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;

import lk.cmh.app.ceylonmarkethub.R;
import lk.cmh.app.ceylonmarkethub.data.util.FragmentUtil;
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

        FragmentUtil.loadFragment(new ProductListFragment(), R.id.fragmentContainerView, this);

        binding.mainTopAppBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId() == R.id.smpAddProduct) {
                    FragmentUtil.loadFragment(new AddProductFragment(), R.id.fragmentContainerView, MyProductsActivity.this);
                }
                return false;
            }
        });
    }

}