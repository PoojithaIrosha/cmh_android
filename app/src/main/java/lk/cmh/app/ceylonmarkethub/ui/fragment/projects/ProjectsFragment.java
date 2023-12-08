package lk.cmh.app.ceylonmarkethub.ui.fragment.projects;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.List;

import lk.cmh.app.ceylonmarkethub.data.adapter.rv.ProjectsRvAdapter;
import lk.cmh.app.ceylonmarkethub.data.model.projects.Project;
import lk.cmh.app.ceylonmarkethub.databinding.FragmentProjectsBinding;

public class ProjectsFragment extends Fragment {

    private static String TAG = ProjectsFragment.class.getSimpleName();
    private FragmentProjectsBinding binding;
    private ProjectsVM projectsVM;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = FragmentProjectsBinding.inflate(getLayoutInflater());
        projectsVM = new ViewModelProvider(this).get(ProjectsVM.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                projectsVM.loadProjects();
                binding.swipeRefreshLayout.setRefreshing(false);
            }
        });

        projectsVM.getProjects().observe(getActivity(), new Observer<List<Project>>() {
            @Override
            public void onChanged(List<Project> projects) {
                if (projects != null) {
                    ProjectsRvAdapter adapter = new ProjectsRvAdapter(projects);
                    binding.rvProjects.setAdapter(adapter);
                }
            }
        });

        binding.searchBox.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    projectsVM.searchProjects(v.getText().toString());
                    return true;
                }

                return false;
            }
        });

        binding.searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                projectsVM.searchProjects(s.toString());
            }
        });

        return binding.getRoot();
    }
}