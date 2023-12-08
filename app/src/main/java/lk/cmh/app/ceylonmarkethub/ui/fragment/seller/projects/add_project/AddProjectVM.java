package lk.cmh.app.ceylonmarkethub.ui.fragment.seller.projects.add_project;

import android.content.Context;
import android.content.SharedPreferences;
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
import com.google.firebase.storage.UploadTask;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import kotlin.jvm.functions.Function1;
import lk.cmh.app.ceylonmarkethub.CMH;
import lk.cmh.app.ceylonmarkethub.data.model.projects.Project;
import lk.cmh.app.ceylonmarkethub.data.model.projects.ProjectDto;
import lk.cmh.app.ceylonmarkethub.data.model.user.User;
import lk.cmh.app.ceylonmarkethub.data.repository.CategoryRepository;
import lk.cmh.app.ceylonmarkethub.data.repository.ProductRepository;
import lk.cmh.app.ceylonmarkethub.data.repository.ProjectRepository;
import lk.cmh.app.ceylonmarkethub.data.repository.UserRepository;
import lk.cmh.app.ceylonmarkethub.data.util.RetrofitUtil;
import lk.cmh.app.ceylonmarkethub.ui.fragment.seller.products.add_product.AddProductFormState;
import lk.cmh.app.ceylonmarkethub.ui.fragment.seller.products.add_product.AddProductVM;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddProjectVM extends ViewModel {
    private static final String TAG = AddProjectVM.class.getSimpleName();
    private ProjectRepository projectRepository;
    private UserRepository userRepository;
    private FirebaseStorage storage;
    private MutableLiveData<User> me = new MutableLiveData<>();

    private MutableLiveData<AddProjectFS> addProjectFS = new MutableLiveData<>();

    public AddProjectVM(ProjectRepository projectRepository, UserRepository userRepository) {
        this.projectRepository = projectRepository;
        this.storage = FirebaseStorage.getInstance();
        this.userRepository = userRepository;
        loadMe();
    }

    public static ViewModelInitializer<AddProjectVM> initializer = new ViewModelInitializer<>(AddProjectVM.class, new Function1<CreationExtras, AddProjectVM>() {
        @Override
        public AddProjectVM invoke(CreationExtras creationExtras) {
            SharedPreferences auth = CMH.getInstance().getSharedPreferences("auth", Context.MODE_PRIVATE);
            ProjectRepository pr = RetrofitUtil.getInstance().getRepository(ProjectRepository.class, auth);
            UserRepository ur = RetrofitUtil.getInstance().getRepository(UserRepository.class, auth);
            return new AddProjectVM(pr, ur);
        }
    });

    public void loadMe() {
        userRepository.getMe().enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                me.setValue(response.body());
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public void addProjectDataChanged(String name, String description, String category, String skills, String budget, String duration) {
        AddProjectFS formState = addProjectFS.getValue();
        if (formState == null) {
            formState = new AddProjectFS(null, null, null, null, null, null);
        }

        if (name.isEmpty()) {
            formState.setNameError("Name cannot be empty");
            addProjectFS.setValue(formState);
        } else {
            formState.setNameError(null);
        }

        if (description.isEmpty()) {
            formState.setDescriptionError("Description cannot be empty");
            addProjectFS.setValue(formState);
        } else {
            formState.setDescriptionError(null);
        }

        if (category.isEmpty()) {
            formState.setCategoryError("Category cannot be empty");
            addProjectFS.setValue(formState);
        } else {
            formState.setCategoryError(null);
        }

        if (skills.isEmpty()) {
            formState.setSkillsError("Skills cannot be empty");
            addProjectFS.setValue(formState);
        } else {
            formState.setSkillsError(null);
        }

        if (budget.isEmpty()) {
            formState.setBudgetError("Budget cannot be empty");
            addProjectFS.setValue(formState);
        } else {
            formState.setBudgetError(null);
        }

        if (duration.isEmpty()) {
            formState.setDurationError("Duration cannot be empty");
            addProjectFS.setValue(formState);
        } else {
            formState.setDurationError(null);
        }

        if (!name.isEmpty() && !description.isEmpty() && !budget.isEmpty() && !category.isEmpty() && !skills.isEmpty() && !duration.isEmpty()) {
            addProjectFS.setValue(new AddProjectFS(true));
        }
    }

    public void addProject(ProjectDto projectDto, List<Uri> imageList, Runnable runnable) {
        List<String> url = new ArrayList<>();

        imageList.forEach(uri -> {
            String uuid = UUID.randomUUID().toString();
            url.add(uuid);
            StorageReference products = storage.getReference("projects").child(uuid);
            products.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.i(TAG, "onSuccess: " + uuid);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    url.remove(uuid);
                }
            });
        });

        projectDto.setProjectImages(url.stream().map(ProjectDto.ProjectImageDto::new).collect(Collectors.toSet()));

        projectRepository.createProject(projectDto).enqueue(new Callback<Project>() {
            @Override
            public void onResponse(Call<Project> call, Response<Project> response) {
                runnable.run();
            }

            @Override
            public void onFailure(Call<Project> call, Throwable t) {
                t.printStackTrace();
            }
        });

    }

    public MutableLiveData<AddProjectFS> getAddProjectFS() {
        return addProjectFS;
    }

    public MutableLiveData<User> getMe() {
        return me;
    }
}
