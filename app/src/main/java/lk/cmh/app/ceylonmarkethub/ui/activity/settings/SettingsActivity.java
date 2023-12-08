package lk.cmh.app.ceylonmarkethub.ui.activity.settings;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import java.util.ArrayList;
import java.util.List;

import lk.cmh.app.ceylonmarkethub.R;
import lk.cmh.app.ceylonmarkethub.data.adapter.lv.SettingsLvAdapter;
import lk.cmh.app.ceylonmarkethub.data.model.settings.SettingsItem;
import lk.cmh.app.ceylonmarkethub.databinding.ActivitySettingsBinding;
import lk.cmh.app.ceylonmarkethub.ui.activity.settings.user_profile.UserProfileActivity;
import lk.cmh.app.ceylonmarkethub.ui.activity.shipping_address.ShippingAddressActivity;

public class SettingsActivity extends AppCompatActivity {

    private ActivitySettingsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        List<SettingsItem> settings = new ArrayList<>();
        settings.add(new SettingsItem("Profile", "Update user profile", UserProfileActivity.class));
        settings.add(new SettingsItem("Address", "Update your billing address", ShippingAddressActivity.class));

        binding.settingsListView.setAdapter(new SettingsLvAdapter(this, settings));

        binding.mainTopAppBar.setNavigationOnClickListener(v -> {
            finish();
        });

    }
}