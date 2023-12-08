package lk.cmh.app.ceylonmarkethub.ui.activity.auth;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.util.Patterns;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import lk.cmh.app.ceylonmarkethub.CMH;
import lk.cmh.app.ceylonmarkethub.data.model.ErrorResponse;
import lk.cmh.app.ceylonmarkethub.data.model.auth.login.LoginReqDto;
import lk.cmh.app.ceylonmarkethub.data.model.auth.login.LoginRespDto;
import lk.cmh.app.ceylonmarkethub.data.model.auth.login.LoginResult;
import lk.cmh.app.ceylonmarkethub.data.model.auth.register.RegisterReqDto;
import lk.cmh.app.ceylonmarkethub.data.model.auth.register.RegisterResult;
import lk.cmh.app.ceylonmarkethub.data.repository.AuthRepository;
import lk.cmh.app.ceylonmarkethub.data.util.RetrofitUtil;
import lk.cmh.app.ceylonmarkethub.ui.activity.auth.login.LoginFormState;
import lk.cmh.app.ceylonmarkethub.ui.activity.auth.register.RegisterFormState;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthViewModel extends ViewModel {

    private static final String TAG = AuthViewModel.class.getSimpleName();
    private MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();
    private MutableLiveData<LoginResult> loginResult = new MutableLiveData<>();

    private MutableLiveData<RegisterResult> registerResult = new MutableLiveData<>();

    private MutableLiveData<RegisterFormState> registerFormState = new MutableLiveData<>();
    private FirebaseAuth firebaseAuth;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
    private AuthRepository authRepository;

    public AuthViewModel() {
        authRepository = RetrofitUtil.getInstance().getRepository(AuthRepository.class);
        firebaseAuth = FirebaseAuth.getInstance();
    }

    public void loginDataChanged(String email, String password) {
        if (!isUserNameValid(email)) {
            loginFormState.setValue(new LoginFormState("Not a valid email", null));
        } else if (!isPasswordValid(password)) {
            loginFormState.setValue(new LoginFormState(null, "Password must be > 5 characters"));
        } else {
            loginFormState.setValue(new LoginFormState(true));
        }
    }

    public void registerDataChanged(String firstName, String email, String password) {
        RegisterFormState value = registerFormState.getValue();
        if (value == null) {
            value = new RegisterFormState(null, null, null);
        }

        if (firstName.isEmpty()) {
            value.setFirstNameError("First name cannot be empty");
            registerFormState.setValue(value);
        }

        if (!isUserNameValid(email)) {
            value.setEmailError("Not a valid email");
            registerFormState.setValue(value);
        }

        if (password.isEmpty()) {
            value.setPasswordError("Password cannot be empty");
            registerFormState.setValue(value);
        }

        if (password.length() <= 5) {
            value.setPasswordError("Password must be > 5 characters");
            registerFormState.setValue(value);
        }

        if (!firstName.isEmpty() && isUserNameValid(email) && !password.isEmpty() && password.length() > 5) {
            registerFormState.setValue(new RegisterFormState(true));
        }

    }

    private boolean isUserNameValid(String username) {
        if (username == null) {
            return false;
        }

        if (!username.contains("@")) {
            return false;
        } else {
            return Patterns.EMAIL_ADDRESS.matcher(username).matches();
        }
    }

    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 5;
    }

    public void login(String email, String password) {
        authRepository.login(new LoginReqDto(email, password)).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                if (response.isSuccessful()) {
                    Log.i(TAG, "Success: " + response.body());
                    JsonObject body = response.body();
                    String email = body.get("email").getAsString();
                    String accessToken = body.get("accessToken").getAsString();
                    String refreshToken = body.get("refreshToken").getAsString();
                    String firebaseToken = body.get("firebaseToken").getAsString();
                    String expiresIn = body.get("expiresIn").getAsString();
                    String role = body.get("role").getAsString();
                    Date expireDate = null;
                    try {
                        expireDate = dateFormat.parse(expiresIn);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    LoginResult.Success loginRespDtoSuccess = new LoginResult.Success(new LoginRespDto(email, accessToken, refreshToken, firebaseToken, expireDate, role));

                    firebaseAuth.signInWithCustomToken(firebaseToken).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser user = task.getResult().getUser();
                                loginResult.setValue(loginRespDtoSuccess);
                            }
                        }
                    });
                } else {
                    try {
                        JSONObject errorJson = new JSONObject(response.errorBody().string());
                        String type = errorJson.getString("type");
                        String title = errorJson.getString("title");
                        String detail = errorJson.getString("detail");
                        int status = errorJson.getInt("status");

                        LoginResult.Error error = new LoginResult.Error(new ErrorResponse(type, title, status, detail));
                        loginResult.setValue(error);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.i(TAG, t.getMessage());
            }
        });
    }

    public void googleAuth(String idToken) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("idToken", idToken);

        authRepository.googleLogin(jsonObject).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject body = response.body();
                    String email = body.get("email").getAsString();
                    String accessToken = body.get("accessToken").getAsString();
                    String refreshToken = body.get("refreshToken").getAsString();
                    String firebaseToken = body.get("firebaseToken").getAsString();
                    String expiresIn = body.get("expiresIn").getAsString();
                    String role = body.get("role").getAsString();
                    Date expireDate = null;
                    try {
                        expireDate = dateFormat.parse(expiresIn);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    LoginResult.Success loginRespDtoSuccess = new LoginResult.Success(new LoginRespDto(email, accessToken, refreshToken, firebaseToken, expireDate, role));
                    loginResult.setValue(loginRespDtoSuccess);
                } else {
                    try {
                        JSONObject errorJson = new JSONObject(response.errorBody().string());
                        String type = errorJson.getString("type");
                        String title = errorJson.getString("title");
                        String detail = errorJson.getString("detail");
                        int status = errorJson.getInt("status");

                        LoginResult.Error error = new LoginResult.Error(new ErrorResponse(type, title, status, detail));
                        loginResult.setValue(error);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public void register(String firstName, String lastName, String email, String password) {
        authRepository.register(new RegisterReqDto(firstName, lastName, email, password)).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                if (response.isSuccessful()) {
                    Log.i(TAG, "Success: " + response.body());
                    JsonObject body = response.body();
                    String message = body.get("message").getAsString();

                    RegisterResult.Success success = new RegisterResult.Success(message);
                    registerResult.setValue(success);
                } else {
                    try {
                        JSONObject errorJson = new JSONObject(response.errorBody().string());
                        String type = errorJson.getString("type");
                        String title = errorJson.getString("title");
                        String detail = errorJson.getString("detail");
                        int status = errorJson.getInt("status");

                        RegisterResult.Error error = new RegisterResult.Error(new ErrorResponse(type, title, status, detail));
                        registerResult.setValue(error);
                    } catch (JSONException | IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.i(TAG, t.getMessage());
            }
        });
    }

    public void signOut(Context context) {
        firebaseAuth.signOut();
        FirebaseMessaging.getInstance().deleteToken();

        SharedPreferences auth = context.getSharedPreferences("auth", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = auth.edit();
        edit.clear().apply();

        SharedPreferences topics = context.getSharedPreferences("topics", Context.MODE_PRIVATE);
        SharedPreferences.Editor topicsEdit = topics.edit();
        topicsEdit.clear().apply();

        if(CMH.getInstance().getNotificationListener() != null) {
            CMH.getInstance().getNotificationListener().remove();
            CMH.getInstance().setNotificationListener(null);
        }
    }

    public LiveData<LoginFormState> getLoginFormState() {
        return loginFormState;
    }

    public LiveData<LoginResult> getLoginResult() {
        return loginResult;
    }

    public LiveData<RegisterFormState> getRegisterFormState() {
        return registerFormState;
    }

    public MutableLiveData<RegisterResult> getRegisterResult() {
        return registerResult;
    }
}
