package lk.cmh.app.ceylonmarkethub.ui.fragment.seller.projects.update_project;

import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import lk.cmh.app.ceylonmarkethub.R;
import lk.cmh.app.ceylonmarkethub.data.adapter.rv.AddProductRvImagesAdapter;
import lk.cmh.app.ceylonmarkethub.data.model.product.ProductColors;
import lk.cmh.app.ceylonmarkethub.data.model.product.ProductDto;
import lk.cmh.app.ceylonmarkethub.data.model.product.ProductImage;
import lk.cmh.app.ceylonmarkethub.data.model.product.ProductSize;
import lk.cmh.app.ceylonmarkethub.data.model.projects.Project;
import lk.cmh.app.ceylonmarkethub.data.model.projects.ProjectDto;
import lk.cmh.app.ceylonmarkethub.data.model.projects.ProjectImage;
import lk.cmh.app.ceylonmarkethub.data.util.FragmentUtil;
import lk.cmh.app.ceylonmarkethub.databinding.FragmentUpdateProjectBinding;
import lk.cmh.app.ceylonmarkethub.ui.fragment.seller.products.product_list.ProductListFragment;
import lk.cmh.app.ceylonmarkethub.ui.fragment.seller.products.update_product.UpdateProductVM;
import lk.cmh.app.ceylonmarkethub.ui.fragment.seller.projects.add_project.AddProjectFS;

public class UpdateProjectFragment extends Fragment {

    private static String TAG = UpdateProjectFragment.class.getSimpleName();
    private FragmentUpdateProjectBinding binding;
    private FirebaseStorage storage;
    private UpdateProjectVM updateProjectVM;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = FragmentUpdateProjectBinding.inflate(getLayoutInflater());
        storage = FirebaseStorage.getInstance();
        updateProjectVM = new ViewModelProvider(this, ViewModelProvider.Factory.from(UpdateProjectVM.initializer)).get(UpdateProjectVM.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        long id = arguments.getLong("pid");

        updateProjectVM.loadProject(id);

        updateProjectVM.getProject().observe(getActivity(), new Observer<Project>() {
            @Override
            public void onChanged(Project project) {
                binding.etProjectName.setText(project.getName());
                binding.etDescription.setText(project.getDescription());
                binding.etCategory.setText(project.getCategory());
                binding.etSkills.setText(project.getSkills());
                binding.etBudget.setText(String.valueOf(project.getBudget()));
                binding.etDuration.setText(project.getDuration());

                updateProjectVM.getUpdateProjectFS().setValue(null);

                binding.etProjectName.setError(null);
                binding.etDescription.setError(null);
                binding.etCategory.setError(null);
                binding.etSkills.setError(null);
                binding.etBudget.setError(null);
                binding.etDuration.setError(null);

                for (ProjectImage projectImage : project.getProjectImages()) {
                    storage.getReference("projects/" + projectImage.getUrl()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            updateProjectVM.getUpdateProjectFS().setValue(null);
                            AddProductRvImagesAdapter adapter = (AddProductRvImagesAdapter) binding.rvProjectImages.getAdapter();
                            if (adapter == null) {
                                binding.rvProjectImages.setAdapter(new AddProductRvImagesAdapter(Arrays.asList(uri)));
                            } else {
                                List<Uri> uriList = new ArrayList<>(adapter.getUriList());
                                uriList.add(uri);
                                adapter.setUriList(uriList);
                                adapter.notifyDataSetChanged();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.i(TAG, e.getMessage());
                        }
                    });
                }
            }
        });

        binding.btnClose.setOnClickListener(v -> {
            FragmentUtil.loadFragment(new ProductListFragment(), R.id.fragmentContainerView, getActivity());
        });

        binding.btnChooseImages.setOnClickListener(v -> {
            String[] mimeType = {"image/*"};
            activityResultLauncher.launch(mimeType);
        });

        updateProjectVM.getUpdateProjectFS().observe(getActivity(), new Observer<UpdateProjectFS>() {
            @Override
            public void onChanged(UpdateProjectFS updateProjectFS) {
                if (updateProjectFS == null) {
                    return;
                }
                binding.btnSave.setEnabled(updateProjectFS.isValid());

                if (updateProjectFS.getNameError() != null) {
                    binding.etProjectName.setError(updateProjectFS.getNameError());
                }

                if (updateProjectFS.getDescriptionError() != null) {
                    binding.etDescription.setError(updateProjectFS.getDescriptionError());
                }

                if (updateProjectFS.getCategoryError() != null) {
                    binding.etCategory.setError(updateProjectFS.getCategoryError());
                }

                if (updateProjectFS.getSkillsError() != null) {
                    binding.etSkills.setError(updateProjectFS.getSkillsError());
                }

                if (updateProjectFS.getBudgetError() != null) {
                    binding.etBudget.setError(updateProjectFS.getBudgetError());
                }

                if (updateProjectFS.getDurationError() != null) {
                    binding.etDuration.setError(updateProjectFS.getDurationError());
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
                updateProjectVM.addProjectDataChanged(
                        binding.etProjectName.getText().toString(),
                        binding.etDescription.getText().toString(),
                        binding.etCategory.getText().toString(),
                        binding.etSkills.getText().toString(),
                        binding.etBudget.getText().toString(),
                        binding.etDuration.getText().toString()
                );
            }
        };
        binding.etProjectName.addTextChangedListener(afterTextChangedListener);
        binding.etDescription.addTextChangedListener(afterTextChangedListener);
        binding.etCategory.addTextChangedListener(afterTextChangedListener);
        binding.etSkills.addTextChangedListener(afterTextChangedListener);
        binding.etBudget.addTextChangedListener(afterTextChangedListener);
        binding.etDuration.addTextChangedListener(afterTextChangedListener);

        binding.btnSave.setOnClickListener(v -> {
            AddProductRvImagesAdapter adapter = (AddProductRvImagesAdapter) binding.rvProjectImages.getAdapter();
            if (adapter == null || adapter.getUriList().size() < 1) {
                Toast.makeText(getContext(), "Please select at least one product image", Toast.LENGTH_SHORT).show();
                return;
            }

            ProjectDto projectDto = new ProjectDto(
                    binding.etProjectName.getText().toString(),
                    binding.etDescription.getText().toString(),
                    binding.etCategory.getText().toString(),
                    binding.etSkills.getText().toString(),
                    Double.parseDouble(binding.etBudget.getText().toString()),
                    binding.etDuration.getText().toString(),
                    null);

            updateProjectVM.updateProject(id, projectDto, adapter.getUriList(), () -> {
                getActivity().finish();
            });

        });

        return binding.getRoot();
    }

    private ActivityResultLauncher<String[]> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.OpenMultipleDocuments(), new ActivityResultCallback<List<Uri>>() {
        @Override
        public void onActivityResult(List<Uri> o) {
            updateProjectVM.getIsImageChanged().setValue(true);
            binding.rvProjectImages.setAdapter(new AddProductRvImagesAdapter(o));
        }
    });
}