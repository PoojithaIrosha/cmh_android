package lk.cmh.app.ceylonmarkethub.ui.activity.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.navigation.NavigationBarView;

import lk.cmh.app.ceylonmarkethub.R;
import lk.cmh.app.ceylonmarkethub.databinding.ActivityMainBinding;
import lk.cmh.app.ceylonmarkethub.ui.activity.product.search.SearchProductsActivity;
import lk.cmh.app.ceylonmarkethub.ui.fragment.account.AccountFragment;
import lk.cmh.app.ceylonmarkethub.ui.fragment.cart.CartFragment;
import lk.cmh.app.ceylonmarkethub.ui.fragment.home.HomeFragment;
import lk.cmh.app.ceylonmarkethub.ui.fragment.projects.ProjectsFragment;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SplashScreen.installSplashScreen(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Bundle bundle = getIntent().getExtras();
        String menuItem = "home";

        if (bundle != null) {
            menuItem = bundle.getString("menuItem", "home");
        }

        switch (menuItem) {
            case "home":
                binding.bottomNavigationView.setSelectedItemId(R.id.bottomNavHome);
                loadFragment(new HomeFragment());
                break;
            case "shop":
                binding.bottomNavigationView.setSelectedItemId(R.id.bottomNavShop);
                loadFragment(new ProjectsFragment());
                break;
            case "cart":
                binding.bottomNavigationView.setSelectedItemId(R.id.bottomNavCart);
                loadFragment(new CartFragment());
                break;
            case "account":
                // For now go to login
                binding.bottomNavigationView.setSelectedItemId(R.id.bottomNavAccount);
                loadFragment(new AccountFragment());
                break;
        }

        binding.bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.bottomNavHome) {
                    loadFragment(new HomeFragment());
                    return true;
                } else if (item.getItemId() == R.id.bottomNavShop) {
                    loadFragment(new ProjectsFragment());
                    return true;
                } else if (item.getItemId() == R.id.bottomNavCart) {
                    loadFragment(new CartFragment());
                    return true;
                } else if (item.getItemId() == R.id.bottomNavAccount) {
                    loadFragment(new AccountFragment());
                    return true;
                }

                return false;
            }
        });
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        supportFragmentManager.beginTransaction().replace(R.id.mainFragmentContainerView, fragment).commit();
    }
}