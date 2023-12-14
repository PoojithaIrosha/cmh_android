package lk.cmh.app.ceylonmarkethub.ui.activity.settings.user_profile;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import lk.cmh.app.ceylonmarkethub.R;
import lk.cmh.app.ceylonmarkethub.data.model.user.User;
import lk.cmh.app.ceylonmarkethub.databinding.ActivityUserProfileBinding;

public class UserProfileActivity extends AppCompatActivity {

    private ActivityUserProfileBinding binding;
    private UserProfileVM userProfileVM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.mainTopAppBar.setNavigationOnClickListener(v -> finish());
        userProfileVM = new ViewModelProvider(this, ViewModelProvider.Factory.from(UserProfileVM.initializer)).get(UserProfileVM.class);

        userProfileVM.getUser().observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                binding.etFirstName.setText(user.getFirstName());
                binding.etLastName.setText(user.getLastName());
                binding.etEmail.setText(user.getEmail());
                binding.etRole.setText(user.getRole().split("_")[1]);

                if (user.getPicture() == null) {
                    binding.userProfileImage.setImageDrawable(getDrawable(R.drawable.shop_img));
                } else {
                    Picasso.get().load(user.getPicture()).placeholder(R.drawable.shop_img).into(binding.userProfileImage);
                }
            }
        });

        binding.userProfileImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            launcher.launch(intent);
        });

        binding.btnSave.setOnClickListener(v -> {
            String firstName = binding.etFirstName.getText().toString();
            String lastName = binding.etLastName.getText().toString();

            if (firstName.isEmpty()) {
                binding.etFirstName.setError("First name is required");
            } else if (lastName.isEmpty()) {
                binding.etLastName.setError("Last name is required");
            } else {
                binding.etFirstName.setError(null);
                binding.etLastName.setError(null);
                userProfileVM.updateUserProfile(firstName, lastName, () -> {
                    Toast.makeText(UserProfileActivity.this, "User Profile Updated", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private final ActivityResultLauncher<Intent> launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult o) {
            Intent data = o.getData();
            if (data != null) {
                Uri uri = data.getData();
                userProfileVM.uploadImage(uri, () -> {
                    Toast.makeText(UserProfileActivity.this, "User Profile Updated", Toast.LENGTH_SHORT).show();
                });
            }
        }
    });
}