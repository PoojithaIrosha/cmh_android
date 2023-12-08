package lk.cmh.app.ceylonmarkethub.ui.activity.auth.register;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import lk.cmh.app.ceylonmarkethub.data.model.ErrorResponse;
import lk.cmh.app.ceylonmarkethub.data.model.auth.register.RegisterResult;
import lk.cmh.app.ceylonmarkethub.databinding.ActivityRegisterBinding;
import lk.cmh.app.ceylonmarkethub.ui.activity.auth.AuthViewModel;
import lk.cmh.app.ceylonmarkethub.ui.activity.auth.login.LoginActivity;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private AuthViewModel authViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        binding.mainTopAppBar.setNavigationOnClickListener(v -> {
            finish();
        });

        authViewModel.getRegisterFormState().observe(this, new Observer<RegisterFormState>() {
            @Override
            public void onChanged(RegisterFormState registerFormState) {
                if (registerFormState == null) {
                    return;
                }
                binding.btnRegister.setEnabled(registerFormState.isValid());
                if (registerFormState.getEmailError() != null) {
                    binding.etEmail.setError(registerFormState.getEmailError());
                }
                if (registerFormState.getPasswordError() != null) {
                    binding.etPassword.setError(registerFormState.getPasswordError());
                }
                if (registerFormState.getFirstNameError() != null) {
                    binding.etFirstName.setError(registerFormState.getFirstNameError());
                }
            }
        });

        authViewModel.getRegisterResult().observe(this, new Observer<RegisterResult>() {
            @Override
            public void onChanged(RegisterResult registerResult) {

                if (registerResult == null) {
                    return;
                }

                if (registerResult instanceof RegisterResult.Success) {
                    String data = ((RegisterResult.Success) registerResult).getData();
                    binding.progressBar.setVisibility(View.GONE);
                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                    finish();
                } else if (registerResult instanceof RegisterResult.Error) {
                    binding.progressBar.setVisibility(View.GONE);
                    ErrorResponse response = ((RegisterResult.Error) registerResult).getError();
                    binding.tvError.setText(response.getDetail());
                    binding.tvError.setVisibility(View.VISIBLE);
                }

            }
        });


        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                authViewModel.registerDataChanged(binding.etFirstName.getText().toString(), binding.etEmail.getText().toString(),
                        binding.etPassword.getText().toString());
            }
        };
        binding.etFirstName.addTextChangedListener(afterTextChangedListener);
        binding.etEmail.addTextChangedListener(afterTextChangedListener);
        binding.etPassword.addTextChangedListener(afterTextChangedListener);

        binding.etPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    authViewModel.register(binding.etFirstName.getText().toString(), binding.etLastName.getText().toString(), binding.etEmail.getText().toString(), binding.etPassword.getText().toString());
                }
                return false;
            }
        });

        binding.btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.progressBar.setVisibility(View.VISIBLE);
                authViewModel.register(binding.etFirstName.getText().toString(), binding.etLastName.getText().toString(), binding.etEmail.getText().toString(), binding.etPassword.getText().toString());
            }
        });
    }
}