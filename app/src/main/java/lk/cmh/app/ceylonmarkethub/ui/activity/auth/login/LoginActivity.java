package lk.cmh.app.ceylonmarkethub.ui.activity.auth.login;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.BeginSignInResult;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.auth.GoogleAuthProvider;

import lk.cmh.app.ceylonmarkethub.CMH;
import lk.cmh.app.ceylonmarkethub.R;
import lk.cmh.app.ceylonmarkethub.data.model.ErrorResponse;
import lk.cmh.app.ceylonmarkethub.data.model.auth.login.LoginRespDto;
import lk.cmh.app.ceylonmarkethub.data.model.auth.login.LoginResult;
import lk.cmh.app.ceylonmarkethub.databinding.ActivityLoginBinding;
import lk.cmh.app.ceylonmarkethub.ui.activity.auth.AuthViewModel;
import lk.cmh.app.ceylonmarkethub.ui.activity.auth.register.RegisterActivity;
import lk.cmh.app.ceylonmarkethub.ui.activity.main.MainActivity;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();

    private ActivityLoginBinding binding;
    private AuthViewModel authViewModel;
    private FirebaseAuth firebaseAuth;
    private SignInClient oneTapClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater from = LayoutInflater.from(this);
        binding = ActivityLoginBinding.inflate(from);
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() != null) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("menuItem", "home");
            startActivity(intent);
            finish();
        }

        oneTapClient = Identity.getSignInClient(getApplicationContext());

        binding.mainTopAppBar.setNavigationOnClickListener(v -> {
            finish();
        });

        binding.btnSignInWithEmail.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        authViewModel.getLoginFormState().observe(this, new Observer<LoginFormState>() {
            @Override
            public void onChanged(LoginFormState loginFormState) {
                if (loginFormState == null) {
                    return;
                }
                binding.btnSignIn.setEnabled(loginFormState.isValid());
                if (loginFormState.getEmailError() != null) {
                    binding.etEmail.setError(loginFormState.getEmailError());
                }
                if (loginFormState.getPasswordError() != null) {
                    binding.etPassword.setError(loginFormState.getPasswordError());
                }
            }
        });

        authViewModel.getLoginResult().observe(this, new Observer<LoginResult>() {
            @Override
            public void onChanged(LoginResult loginResult) {

                if (loginResult == null) {
                    return;
                }

                if (loginResult instanceof LoginResult.Success) {
                    LoginRespDto data = ((LoginResult.Success) loginResult).getData();

                    SharedPreferences auth = getSharedPreferences("auth", MODE_PRIVATE);
                    SharedPreferences.Editor edit = auth.edit();
                    edit.putString("email", data.getEmail());
                    edit.putString("accessToken", data.getAccessToken());
                    edit.putString("refreshToken", data.getRefreshToken());
                    edit.putString("firebaseToken", data.getFirebaseToken());
                    edit.putString("role", data.getRole());
                    edit.putLong("expiresIn", data.getExpiresIn().getTime());
                    edit.apply();

                    CMH.getInstance().addNotificationListener(auth);

                    binding.progressBar.setVisibility(View.GONE);
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("menuItem", "home");
                    startActivity(intent);
                    finish();

                } else if (loginResult instanceof LoginResult.Error) {

                    binding.progressBar.setVisibility(View.GONE);
                    ErrorResponse response = ((LoginResult.Error) loginResult).getError();
                    binding.tvError.setText(response.getDetail());
                    binding.tvError.setVisibility(View.VISIBLE);
                    Log.i(TAG, response.toString());

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
                authViewModel.loginDataChanged(binding.etEmail.getText().toString(), binding.etPassword.getText().toString());
            }
        };
        binding.etEmail.addTextChangedListener(afterTextChangedListener);
        binding.etPassword.addTextChangedListener(afterTextChangedListener);

        binding.etPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    authViewModel.login(binding.etEmail.getText().toString(), binding.etPassword.getText().toString());
                }
                return false;
            }
        });

        binding.btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Clicked");
                binding.progressBar.setVisibility(View.VISIBLE);
                authViewModel.login(binding.etEmail.getText().toString(), binding.etPassword.getText().toString());
            }
        });

        binding.btnSignInWithGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.progressBar.setVisibility(View.VISIBLE);
                BeginSignInRequest signInRequest = BeginSignInRequest.builder().setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder().setSupported(true).setServerClientId(getString(R.string.server_client_id)).setFilterByAuthorizedAccounts(false).build()).build();

                Task<BeginSignInResult> signInTask = oneTapClient.beginSignIn(signInRequest);
                signInTask.addOnCompleteListener(new OnCompleteListener<BeginSignInResult>() {
                    @Override
                    public void onComplete(@NonNull Task<BeginSignInResult> task) {
                        if (task.isSuccessful()) {
                            BeginSignInResult result = task.getResult();
                            PendingIntent pendingIntent = result.getPendingIntent();
                            try {
                                IntentSenderRequest intentSenderRequest = new IntentSenderRequest.Builder(pendingIntent.getIntentSender()).build();
                                signInLauncher.launch(intentSenderRequest);
                            } catch (Exception e) {
                                binding.progressBar.setVisibility(View.GONE);
                                e.printStackTrace();
                            }
                        } else {
                            binding.progressBar.setVisibility(View.GONE);
                            Log.e(TAG, "Failed to create sign-in intent", task.getException());
                        }
                    }
                });
            }
        });
    }

    private void handleSignInResult(Intent intent) {
        try {
            SignInCredential credential = oneTapClient.getSignInCredentialFromIntent(intent);
            String idToken = credential.getGoogleIdToken();

            firebaseAuth.signInWithCredential(GoogleAuthProvider.getCredential(idToken, null)).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        task.getResult().getUser().getIdToken(true).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                            public void onComplete(@NonNull Task<GetTokenResult> task) {
                                if (task.isSuccessful()) {
                                    String userIdToken = task.getResult().getToken();
                                    authViewModel.googleAuth(userIdToken);
                                } else {
                                    binding.progressBar.setVisibility(View.VISIBLE);
                                }
                            }
                        });
                    } else {
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        binding.progressBar.setVisibility(View.VISIBLE);
                    }
                }
            });

        } catch (ApiException e) {
            Log.e(TAG, "handleSignInResult:error", e);
            binding.progressBar.setVisibility(View.VISIBLE);
        }
    }

    private final ActivityResultLauncher<IntentSenderRequest> signInLauncher = registerForActivityResult(new ActivityResultContracts.StartIntentSenderForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult o) {
            handleSignInResult(o.getData());
        }
    });

}