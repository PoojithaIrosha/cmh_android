package lk.cmh.app.ceylonmarkethub.ui.activity.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;

import com.google.android.material.navigation.NavigationBarView;

import lk.cmh.app.ceylonmarkethub.R;
import lk.cmh.app.ceylonmarkethub.data.util.FragmentUtil;
import lk.cmh.app.ceylonmarkethub.databinding.ActivityMainBinding;
import lk.cmh.app.ceylonmarkethub.ui.IntroActivity;
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


        if (getSharedPreferences("cmh", MODE_PRIVATE).getBoolean("isFirstRun", true)) {
            startActivity(new Intent(this, IntroActivity.class));
        }

        Bundle bundle = getIntent().getExtras();
        String menuItem = "home";

        if (bundle != null) {
            menuItem = bundle.getString("menuItem", "home");
        }

        switch (menuItem) {
            case "home":
                binding.bottomNavigationView.setSelectedItemId(R.id.bottomNavHome);
                FragmentUtil.loadFragment(new HomeFragment(), R.id.mainFragmentContainerView, MainActivity.this);
                break;
            case "shop":
                binding.bottomNavigationView.setSelectedItemId(R.id.bottomNavShop);
                FragmentUtil.loadFragment(new ProjectsFragment(), R.id.mainFragmentContainerView, MainActivity.this);
                break;
            case "cart":
                binding.bottomNavigationView.setSelectedItemId(R.id.bottomNavCart);
                FragmentUtil.loadFragment(new CartFragment(), R.id.mainFragmentContainerView, MainActivity.this);
                break;
            case "account":
                // For now go to login
                binding.bottomNavigationView.setSelectedItemId(R.id.bottomNavAccount);
                FragmentUtil.loadFragment(new AccountFragment(), R.id.mainFragmentContainerView, MainActivity.this);
                break;
        }

        binding.bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.bottomNavHome) {
                    FragmentUtil.loadFragment(new HomeFragment(), R.id.mainFragmentContainerView, MainActivity.this);
                    return true;
                } else if (item.getItemId() == R.id.bottomNavShop) {
                    FragmentUtil.loadFragment(new ProjectsFragment(), R.id.mainFragmentContainerView, MainActivity.this);
                    return true;
                } else if (item.getItemId() == R.id.bottomNavCart) {
                    FragmentUtil.loadFragment(new CartFragment(), R.id.mainFragmentContainerView, MainActivity.this);
                    return true;
                } else if (item.getItemId() == R.id.bottomNavAccount) {
                    FragmentUtil.loadFragment(new AccountFragment(), R.id.mainFragmentContainerView, MainActivity.this);
                    return true;
                }

                return false;
            }
        });
    }
}