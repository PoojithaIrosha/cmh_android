package lk.cmh.app.ceylonmarkethub.ui.activity.seller.my_projects;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;

import lk.cmh.app.ceylonmarkethub.R;
import lk.cmh.app.ceylonmarkethub.databinding.ActivityMyProjectsBinding;
import lk.cmh.app.ceylonmarkethub.ui.fragment.seller.products.add_product.AddProductFragment;
import lk.cmh.app.ceylonmarkethub.ui.fragment.seller.products.product_list.ProductListFragment;
import lk.cmh.app.ceylonmarkethub.ui.fragment.seller.projects.add_project.AddProjectFragment;
import lk.cmh.app.ceylonmarkethub.ui.fragment.seller.projects.project_list.ProjectListFragment;

public class MyProjectsActivity extends AppCompatActivity {

    private ActivityMyProjectsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyProjectsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.mainTopAppBar.setNavigationOnClickListener(v -> {
            finish();
        });

        loadFragment(new ProjectListFragment());

        binding.mainTopAppBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId() == R.id.smpAddProduct) {
                    loadFragment(new AddProjectFragment());
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