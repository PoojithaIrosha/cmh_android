package lk.cmh.app.ceylonmarkethub.ui;

import static android.Manifest.permission.*;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import lk.cmh.app.ceylonmarkethub.R;
import lk.cmh.app.ceylonmarkethub.data.util.GsonUtil;
import lk.cmh.app.ceylonmarkethub.databinding.ActivityIntroBinding;
import lk.cmh.app.ceylonmarkethub.ui.activity.main.MainActivity;

public class IntroActivity extends AppCompatActivity {

    private static final int NOTIFICATIONS_REQ_CODE = 101;
    private ActivityIntroBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityIntroBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (!getSharedPreferences("cmh", MODE_PRIVATE).getBoolean("isFirstRun", true)) {
            startActivity(new Intent(this, MainActivity.class));
        } else {
            binding.button.setOnClickListener(v -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (checkSelfPermission(ACCESS_FINE_LOCATION) != PERMISSION_GRANTED || checkSelfPermission(ACCESS_COARSE_LOCATION) != PERMISSION_GRANTED || checkSelfPermission(POST_NOTIFICATIONS) != PERMISSION_GRANTED) {
                        requestPermissions(new String[]{ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION, POST_NOTIFICATIONS}, NOTIFICATIONS_REQ_CODE);
                    } else {
                        getSharedPreferences("cmh", MODE_PRIVATE).edit().putBoolean("isFirstRun", false).apply();
                        startActivity(new Intent(this, MainActivity.class));
                    }
                } else {
                    if (checkSelfPermission(ACCESS_FINE_LOCATION) != PERMISSION_GRANTED || checkSelfPermission(ACCESS_COARSE_LOCATION) != PERMISSION_GRANTED) {
                        requestPermissions(new String[]{ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION}, NOTIFICATIONS_REQ_CODE);
                    } else {
                        getSharedPreferences("cmh", MODE_PRIVATE).edit().putBoolean("isFirstRun", false).apply();
                        startActivity(new Intent(this, MainActivity.class));
                    }
                }
            });
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == NOTIFICATIONS_REQ_CODE) {

            Log.i("ABC", "onRequestPermissionsResult: " + GsonUtil.getInstance().toJson(grantResults));
            boolean allPermissionsGranted = true;
            for (int result : grantResults) {
                if (result != PERMISSION_GRANTED) {
                    allPermissionsGranted = false;
                    break;
                }
            }

            if (allPermissionsGranted) {
                getSharedPreferences("cmh", MODE_PRIVATE).edit().putBoolean("isFirstRun", false).apply();
                startActivity(new Intent(this, MainActivity.class));
            } else {
                Toast.makeText(this, "You missed something. Please try again", Toast.LENGTH_SHORT).show();
            }
        }
    }
}