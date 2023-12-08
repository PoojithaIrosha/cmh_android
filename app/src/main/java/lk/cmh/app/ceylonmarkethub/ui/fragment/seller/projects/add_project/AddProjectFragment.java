package lk.cmh.app.ceylonmarkethub.ui.fragment.seller.projects.add_project;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.List;

import lk.cmh.app.ceylonmarkethub.R;
import lk.cmh.app.ceylonmarkethub.data.adapter.rv.AddProductRvImagesAdapter;
import lk.cmh.app.ceylonmarkethub.data.model.product.ProductDto;
import lk.cmh.app.ceylonmarkethub.data.model.projects.ProjectDto;
import lk.cmh.app.ceylonmarkethub.data.model.user.User;
import lk.cmh.app.ceylonmarkethub.databinding.FragmentAddProjectBinding;
import lk.cmh.app.ceylonmarkethub.ui.activity.main.MainActivity;
import lk.cmh.app.ceylonmarkethub.ui.activity.orders.OrderConfirmationActivity;
import lk.cmh.app.ceylonmarkethub.ui.fragment.seller.products.add_product.AddProductFormState;
import lk.cmh.app.ceylonmarkethub.ui.fragment.seller.products.product_list.ProductListFragment;
import lk.cmh.app.ceylonmarkethub.ui.fragment.seller.projects.project_list.ProjectListFragment;
import lk.payhere.androidsdk.PHConfigs;
import lk.payhere.androidsdk.PHConstants;
import lk.payhere.androidsdk.PHMainActivity;
import lk.payhere.androidsdk.PHResponse;
import lk.payhere.androidsdk.model.InitRequest;
import lk.payhere.androidsdk.model.StatusResponse;

public class AddProjectFragment extends Fragment {

    private static final String TAG = AddProjectFragment.class.getSimpleName();
    private FragmentAddProjectBinding binding;

    private AddProjectVM addProjectVM;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = FragmentAddProjectBinding.inflate(getLayoutInflater());
        addProjectVM = new ViewModelProvider(this, ViewModelProvider.Factory.from(AddProjectVM.initializer)).get(AddProjectVM.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding.btnClose.setOnClickListener(v -> {
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView, new ProjectListFragment()).commit();
        });

        binding.btnChooseImages.setOnClickListener(v -> {
            String[] mimeType = {"image/*"};
            activityResultLauncher.launch(mimeType);
        });

        addProjectVM.getAddProjectFS().observe(getActivity(), new Observer<AddProjectFS>() {
            @Override
            public void onChanged(AddProjectFS addProjectFS) {
                if (addProjectFS == null) {
                    return;
                }
                binding.btnAddProject.setEnabled(addProjectFS.isValid());

                if (addProjectFS.getNameError() != null) {
                    binding.etProjectName.setError(addProjectFS.getNameError());
                }

                if (addProjectFS.getDescriptionError() != null) {
                    binding.etDescription.setError(addProjectFS.getDescriptionError());
                }

                if (addProjectFS.getCategoryError() != null) {
                    binding.etCategory.setError(addProjectFS.getCategoryError());
                }

                if (addProjectFS.getSkillsError() != null) {
                    binding.etSkills.setError(addProjectFS.getSkillsError());
                }

                if (addProjectFS.getBudgetError() != null) {
                    binding.etBudget.setError(addProjectFS.getBudgetError());
                }

                if (addProjectFS.getDurationError() != null) {
                    binding.etDuration.setError(addProjectFS.getDurationError());
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
                addProjectVM.addProjectDataChanged(
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

        binding.btnAddProject.setOnClickListener(v -> {
            User user = addProjectVM.getMe().getValue();

            InitRequest req = new InitRequest();
            req.setMerchantId(getString(R.string.merchantId));
            req.setCurrency(getString(R.string.currency));
            req.setAmount(Double.parseDouble(getString(R.string.project_price)));
            req.setOrderId(String.valueOf(System.currentTimeMillis()));
            req.setItemsDescription("New Project");
            req.getCustomer().setFirstName(user.getFirstName());
            req.getCustomer().setLastName(user.getLastName());
            req.getCustomer().setEmail(user.getEmail());

            if (user.getAddress() != null) {
                req.getCustomer().setPhone(user.getAddress().getMobileNumber());
                req.getCustomer().getAddress().setAddress(user.getAddress().getAddressLine1() + ", " + user.getAddress().getAddressLine2() != null ? user.getAddress().getAddressLine2() : "");
                req.getCustomer().getAddress().setCity(user.getAddress().getCity());
                req.getCustomer().getAddress().setCountry("Sri Lanka");
            }

            Intent intent = new Intent(getContext(), PHMainActivity.class);
            intent.putExtra(PHConstants.INTENT_EXTRA_DATA, req);
            PHConfigs.setBaseUrl(PHConfigs.SANDBOX_URL);
            payHereLauncher.launch(intent);
        });

        return binding.getRoot();
    }

    private ActivityResultLauncher<String[]> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.OpenMultipleDocuments(), new ActivityResultCallback<List<Uri>>() {
        @Override
        public void onActivityResult(List<Uri> o) {
            if (o.size() > 0) {
                binding.rvProjectImages.setAdapter(new AddProductRvImagesAdapter(o));
            }
        }
    });

    private final ActivityResultLauncher<Intent> payHereLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult o) {
            if (o.getData() != null && o.getData().hasExtra(PHConstants.INTENT_EXTRA_RESULT)) {
                PHResponse<StatusResponse> response = (PHResponse<StatusResponse>) o.getData().getSerializableExtra(PHConstants.INTENT_EXTRA_RESULT);
                if (o.getResultCode() == Activity.RESULT_OK) {
                    if (response != null && response.isSuccess()) {
                        AddProductRvImagesAdapter adapter = (AddProductRvImagesAdapter) binding.rvProjectImages.getAdapter();
                        if (adapter == null || adapter.getUriList().size() < 1) {
                            Toast.makeText(getContext(), "Please select at least one project image", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        ProjectDto projectDto = new ProjectDto(binding.etProjectName.getText().toString(),
                                binding.etDescription.getText().toString(),
                                binding.etCategory.getText().toString(),
                                binding.etSkills.getText().toString(),
                                Double.parseDouble(binding.etBudget.getText().toString()),
                                binding.etDuration.getText().toString(),
                                null);

                        addProjectVM.addProject(projectDto, adapter.getUriList(), () -> {
                            Intent intent = new Intent(getContext(), MainActivity.class);
                            intent.putExtra("menuItem", "account");
                            startActivity(intent);
                        });
                    } else {
                        Log.d(TAG, response.toString());
                        Toast.makeText(getContext(), response.getData().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else if (o.getResultCode() == Activity.RESULT_CANCELED) {
                    if (response != null) {
                        Log.i(TAG, response.toString());
                        Toast.makeText(getContext(), response.toString(), Toast.LENGTH_SHORT).show();
                    } else {
                        Log.i(TAG, "User canceled the request");
                        Toast.makeText(getContext(), "User canceled the request", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    });
}