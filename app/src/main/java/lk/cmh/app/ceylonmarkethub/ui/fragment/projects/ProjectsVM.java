package lk.cmh.app.ceylonmarkethub.ui.fragment.projects;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import lk.cmh.app.ceylonmarkethub.data.model.projects.Project;
import lk.cmh.app.ceylonmarkethub.data.repository.ProjectRepository;
import lk.cmh.app.ceylonmarkethub.data.util.RetrofitUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProjectsVM extends ViewModel {

    private static final String TAG = ProjectsVM.class.getSimpleName();
    private MutableLiveData<List<Project>> projects = new MutableLiveData<>();
    private ProjectRepository projectRepository;

    public ProjectsVM() {
        projectRepository = RetrofitUtil.getInstance().getRepository(ProjectRepository.class);
        loadProjects();
    }

    public MutableLiveData<List<Project>> getProjects() {
        return projects;
    }

    public void loadProjects() {
        projectRepository.getAllEnabledProjects().enqueue(new Callback<List<Project>>() {
            @Override
            public void onResponse(Call<List<Project>> call, Response<List<Project>> response) {
                if (response.isSuccessful()) {
                    projects.setValue(response.body());
                }else {
                    projects.setValue(null);
                    Log.i(TAG, "onResponse: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<List<Project>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public void searchProjects(String text) {
        projectRepository.searchProjects(text).enqueue(new Callback<List<Project>>() {
            @Override
            public void onResponse(Call<List<Project>> call, Response<List<Project>> response) {
                if (response.isSuccessful()) {
                    projects.setValue(response.body());
                }else {
                    projects.setValue(null);
                    Log.i(TAG, "onResponse: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<List<Project>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

}
