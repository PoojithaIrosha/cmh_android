package lk.cmh.app.ceylonmarkethub.ui.fragment.seller.projects.update_project;

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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import kotlin.jvm.functions.Function1;
import lk.cmh.app.ceylonmarkethub.CMH;
import lk.cmh.app.ceylonmarkethub.data.model.product.Product;
import lk.cmh.app.ceylonmarkethub.data.model.product.ProductDto;
import lk.cmh.app.ceylonmarkethub.data.model.projects.Project;
import lk.cmh.app.ceylonmarkethub.data.model.projects.ProjectDto;
import lk.cmh.app.ceylonmarkethub.data.repository.ProjectRepository;
import lk.cmh.app.ceylonmarkethub.data.util.GsonUtil;
import lk.cmh.app.ceylonmarkethub.data.util.RetrofitUtil;
import lk.cmh.app.ceylonmarkethub.ui.fragment.seller.projects.add_project.AddProjectFS;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateProjectVM extends ViewModel {

    private static final String TAG = UpdateProjectVM.class.getSimpleName();
    private ProjectRepository projectRepository;
    private MutableLiveData<Project> project = new MutableLiveData<>();
    private MutableLiveData<UpdateProjectFS> updateProjectFS = new MutableLiveData<>();
    private MutableLiveData<Boolean> isImageChanged = new MutableLiveData<>(false);
    private FirebaseStorage storage;

    public UpdateProjectVM(ProjectRepository projectRepository) {
        this.storage = FirebaseStorage.getInstance();
        this.projectRepository = projectRepository;
    }

    public static ViewModelInitializer<UpdateProjectVM> initializer = new ViewModelInitializer<>(UpdateProjectVM.class, new Function1<CreationExtras, UpdateProjectVM>() {
        @Override
        public UpdateProjectVM invoke(CreationExtras creationExtras) {
            SharedPreferences auth = CMH.getInstance().getSharedPreferences("auth", Context.MODE_PRIVATE);
            ProjectRepository pr = RetrofitUtil.getInstance().getRepository(ProjectRepository.class, auth);
            return new UpdateProjectVM(pr);
        }
    });

    public void loadProject(Long id) {
        projectRepository.getProject(id).enqueue(new Callback<Project>() {
            @Override
            public void onResponse(Call<Project> call, Response<Project> response) {
                project.setValue(response.body());
            }

            @Override
            public void onFailure(Call<Project> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public void addProjectDataChanged(String name, String description, String category, String skills, String budget, String duration) {
        UpdateProjectFS formState = updateProjectFS.getValue();
        if (formState == null) {
            formState = new UpdateProjectFS(null, null, null, null, null, null);
        }

        if (name.isEmpty()) {
            formState.setNameError("Name cannot be empty");
            updateProjectFS.setValue(formState);
        } else {
            formState.setNameError(null);
        }

        if (description.isEmpty()) {
            formState.setDescriptionError("Description cannot be empty");
            updateProjectFS.setValue(formState);
        } else {
            formState.setDescriptionError(null);
        }

        if (category.isEmpty()) {
            formState.setCategoryError("Category cannot be empty");
            updateProjectFS.setValue(formState);
        } else {
            formState.setCategoryError(null);
        }

        if (skills.isEmpty()) {
            formState.setSkillsError("Skills cannot be empty");
            updateProjectFS.setValue(formState);
        } else {
            formState.setSkillsError(null);
        }

        if (budget.isEmpty()) {
            formState.setBudgetError("Budget cannot be empty");
            updateProjectFS.setValue(formState);
        } else if (!budget.matches("^[0-9]+(\\.[0-9]{1,2})?$")) {
            formState.setBudgetError("Invalid budget");
            updateProjectFS.setValue(formState);
        } else {
            formState.setBudgetError(null);
        }

        if (duration.isEmpty()) {
            formState.setDurationError("Duration cannot be empty");
            updateProjectFS.setValue(formState);
        } else {
            formState.setDurationError(null);
        }

        if (!name.isEmpty() && !description.isEmpty() && !budget.isEmpty() && !category.isEmpty() && !skills.isEmpty() && !duration.isEmpty()) {
            updateProjectFS.setValue(new UpdateProjectFS(true));
        }
    }

    public void updateProject(Long id, ProjectDto projectDto, List<Uri> imageList, Runnable runnable) {
        List<String> url = new ArrayList<>();
        if (isImageChanged.getValue()) {
            imageList.forEach(uri -> {
                String uuid = UUID.randomUUID().toString();
                url.add(uuid);
                StorageReference projects = storage.getReference("projects").child(uuid);
                projects.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Log.i(TAG, "onSuccess: " + taskSnapshot.getStorage().getDownloadUrl().toString());
                        Log.i(TAG, "onSuccess: " + uuid);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                        url.remove(uuid);
                    }
                });
            });
            Set<ProjectDto.ProjectImageDto> imageDtos = url.stream().map(ProjectDto.ProjectImageDto::new).collect(Collectors.toSet());
            projectDto.setProjectImages(imageDtos);
        } else {
            projectDto.setProjectImages(null);
        }

        Log.i(TAG, "Project Dto: " + GsonUtil.getInstance().toJson(projectDto));
        projectRepository.updateProject(id, projectDto).enqueue(new Callback<Project>() {
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

    public MutableLiveData<Project> getProject() {
        return project;
    }

    public MutableLiveData<UpdateProjectFS> getUpdateProjectFS() {
        return updateProjectFS;
    }

    public MutableLiveData<Boolean> getIsImageChanged() {
        return isImageChanged;
    }
}
