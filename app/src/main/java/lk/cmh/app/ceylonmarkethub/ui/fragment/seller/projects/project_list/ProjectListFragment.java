package lk.cmh.app.ceylonmarkethub.ui.fragment.seller.projects.project_list;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import lk.cmh.app.ceylonmarkethub.CMH;
import lk.cmh.app.ceylonmarkethub.R;
import lk.cmh.app.ceylonmarkethub.data.adapter.rv.SellerProductsRvAdapter;
import lk.cmh.app.ceylonmarkethub.data.adapter.rv.SellerProjectRvAdapter;
import lk.cmh.app.ceylonmarkethub.data.model.product.Product;
import lk.cmh.app.ceylonmarkethub.data.model.projects.Project;
import lk.cmh.app.ceylonmarkethub.data.repository.ProjectRepository;
import lk.cmh.app.ceylonmarkethub.data.util.RetrofitUtil;
import lk.cmh.app.ceylonmarkethub.databinding.FragmentProjectListBinding;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProjectListFragment extends Fragment {

    private FragmentProjectListBinding binding;
    private ProjectRepository projectRepository;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = FragmentProjectListBinding.inflate(getLayoutInflater());
        projectRepository = RetrofitUtil.getInstance().getRepository(ProjectRepository.class, CMH.getInstance().getSharedPreferences("auth", Context.MODE_PRIVATE));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        loadProjects();

        binding.refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadProjects();
                binding.refresh.setRefreshing(false);
            }
        });


        return binding.getRoot();
    }

    private void loadProjects() {
        projectRepository.getSellersProjects().enqueue(new Callback<List<Project>>() {
            @Override
            public void onResponse(Call<List<Project>> call, Response<List<Project>> response) {
                SellerProjectRvAdapter adapter = new SellerProjectRvAdapter(response.body(), projectRepository);
                binding.rvSellerProjects.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<List<Project>> call, Throwable t) {

            }
        });
    }
}