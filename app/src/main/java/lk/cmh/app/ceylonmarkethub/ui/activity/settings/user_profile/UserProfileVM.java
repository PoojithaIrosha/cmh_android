package lk.cmh.app.ceylonmarkethub.ui.activity.settings.user_profile;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.viewmodel.CreationExtras;
import androidx.lifecycle.viewmodel.ViewModelInitializer;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.JsonObject;

import java.util.UUID;

import kotlin.jvm.functions.Function1;
import lk.cmh.app.ceylonmarkethub.CMH;
import lk.cmh.app.ceylonmarkethub.data.model.user.User;
import lk.cmh.app.ceylonmarkethub.data.repository.UserRepository;
import lk.cmh.app.ceylonmarkethub.data.util.RetrofitUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserProfileVM extends ViewModel {

    private UserRepository userRepository;
    private MutableLiveData<User> user = new MutableLiveData<>();
    private FirebaseStorage storage;
    private static final String TAG = UserProfileVM.class.getSimpleName();

    public UserProfileVM(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.storage = FirebaseStorage.getInstance();
        loadUser();
    }

    public static final ViewModelInitializer<UserProfileVM> initializer = new ViewModelInitializer<>(UserProfileVM.class, new Function1<CreationExtras, UserProfileVM>() {
        @Override
        public UserProfileVM invoke(CreationExtras creationExtras) {
            UserRepository userRepository = RetrofitUtil.getInstance().getRepository(UserRepository.class, CMH.getInstance().getSharedPreferences("auth", Context.MODE_PRIVATE));
            return new UserProfileVM(userRepository);
        }
    });

    public void loadUser() {
        userRepository.getMe().enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                user.setValue(response.body());
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public MutableLiveData<User> getUser() {
        return user;
    }

    public void uploadImage(Uri uri, Runnable runnable) {
        String uuid = UUID.randomUUID().toString();
        StorageReference reference = storage.getReference("users/profiles").child(uuid);
        reference.putFile(uri).addOnSuccessListener(taskSnapshot -> {
            reference.getDownloadUrl().addOnSuccessListener(uri1 -> {

                storage.getReference("users/profiles/"+uuid).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Log.i(TAG, uri.toString());

                        JsonObject jsonObject = new JsonObject();
                        jsonObject.addProperty("picture", uri.toString());

                        userRepository.updateProfilePic(jsonObject).enqueue(new Callback<User>() {
                            @Override
                            public void onResponse(Call<User> call, Response<User> response) {
                                user.setValue(response.body());
                                runnable.run();
                            }

                            @Override
                            public void onFailure(Call<User> call, Throwable t) {
                                t.printStackTrace();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                    }
                });
            });
        });

    }

    public void updateUserProfile(String firstName, String lastName, Runnable runnable) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("firstName", firstName);
        jsonObject.addProperty("lastName", lastName);

        userRepository.updateProfile(jsonObject).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                user.setValue(response.body());
                runnable.run();
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
}
