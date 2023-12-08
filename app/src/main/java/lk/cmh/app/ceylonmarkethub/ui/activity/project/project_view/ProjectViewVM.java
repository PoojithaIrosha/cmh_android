package lk.cmh.app.ceylonmarkethub.ui.activity.project.project_view;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import lk.cmh.app.ceylonmarkethub.data.model.projects.Project;
import lk.cmh.app.ceylonmarkethub.data.repository.ProjectRepository;
import lk.cmh.app.ceylonmarkethub.data.util.RetrofitUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProjectViewVM extends ViewModel {

    private static final String TAG = ProjectViewVM.class.getSimpleName();
    private MutableLiveData<Boolean> loading = new MutableLiveData<>();
    private MutableLiveData<Project> project = new MutableLiveData<>();
    private ProjectRepository projectRepository;

    public ProjectViewVM() {
        projectRepository = RetrofitUtil.getInstance().getRepository(ProjectRepository.class);
    }

    public void loadProduct(long id) {
        loading.setValue(true);
        projectRepository.getProject(id).enqueue(new Callback<Project>() {
            @Override
            public void onResponse(Call<Project> call, Response<Project> response) {
                loading.setValue(false);
                if (response.isSuccessful()) {
                    project.setValue(response.body());
                } else {
                    Log.e(TAG, "onResponse: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Project> call, Throwable t) {
                loading.setValue(false);
                Log.e(TAG, "onFailure: " + t.getMessage());
            }
        });
    }

    public MutableLiveData<Boolean> getLoading() {
        return loading;
    }

    public MutableLiveData<Project> getProject() {
        return project;
    }
}
