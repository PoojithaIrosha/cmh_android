package lk.cmh.app.ceylonmarkethub.ui.fragment.seller.products.add_product;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import java.util.List;
import java.util.stream.Collectors;

import lk.cmh.app.ceylonmarkethub.R;
import lk.cmh.app.ceylonmarkethub.data.adapter.rv.AddProductRvImagesAdapter;
import lk.cmh.app.ceylonmarkethub.data.model.category.Category;
import lk.cmh.app.ceylonmarkethub.data.model.product.ProductDto;
import lk.cmh.app.ceylonmarkethub.data.util.FragmentUtil;
import lk.cmh.app.ceylonmarkethub.databinding.FragmentAddProductBinding;
import lk.cmh.app.ceylonmarkethub.ui.activity.main.MainActivity;
import lk.cmh.app.ceylonmarkethub.ui.fragment.seller.products.product_list.ProductListFragment;

public class AddProductFragment extends Fragment {
    private static final String TAG = AddProductFragment.class.getSimpleName();
    private FragmentAddProductBinding binding;
    private AddProductVM addProductVM;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = FragmentAddProductBinding.inflate(getLayoutInflater());
        addProductVM = new ViewModelProvider(this, ViewModelProvider.Factory.from(AddProductVM.initializer)).get(AddProductVM.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding.btnClose.setOnClickListener(v -> {
            FragmentUtil.loadFragment(new ProductListFragment(), R.id.fragmentContainerView, getActivity());
        });

        binding.btnChooseImages.setOnClickListener(v -> {
            String[] mimeType = {"image/*"};
            activityResultLauncher.launch(mimeType);
        });


        addProductVM.getCategoryList().observe(getActivity(), new Observer<List<Category>>() {
            @Override
            public void onChanged(List<Category> categories) {
                List<String> list = categories.stream().map(Category::getName).collect(Collectors.toList());
                list.add(0, "Select");
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, list);
                arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                binding.spinnerCategory.setAdapter(arrayAdapter);
            }
        });

        addProductVM.getAddProductFormState().observe(getActivity(), new Observer<AddProductFormState>() {
            @Override
            public void onChanged(AddProductFormState addProductFormState) {
                if (addProductFormState == null) {
                    return;
                }
                binding.btnAddProduct.setEnabled(addProductFormState.isValid());

                if (addProductFormState.getTitleError() != null) {
                    binding.etProductTitle.setError(addProductFormState.getTitleError());
                }

                if (addProductFormState.getDescriptionError() != null) {
                    binding.etDescription.setError(addProductFormState.getDescriptionError());
                }

                if (addProductFormState.getPriceError() != null) {
                    binding.etPrice.setError(addProductFormState.getPriceError());
                }

                if (addProductFormState.getQuantityError() != null) {
                    binding.etQty.setError(addProductFormState.getQuantityError());
                }

                if (addProductFormState.getConditionError() != null) {
                    binding.etCondition.setError(addProductFormState.getConditionError());
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
                addProductVM.addProductDataChanged(
                        binding.etProductTitle.getText().toString(),
                        binding.etDescription.getText().toString(),
                        binding.etPrice.getText().toString(),
                        binding.etQty.getText().toString(),
                        binding.etCondition.getText().toString()
                );
            }
        };
        binding.etProductTitle.addTextChangedListener(afterTextChangedListener);
        binding.etDescription.addTextChangedListener(afterTextChangedListener);
        binding.etPrice.addTextChangedListener(afterTextChangedListener);
        binding.etQty.addTextChangedListener(afterTextChangedListener);
        binding.etCondition.addTextChangedListener(afterTextChangedListener);

        binding.btnAddProduct.setOnClickListener(v -> {

            AddProductRvImagesAdapter adapter = (AddProductRvImagesAdapter) binding.rvProductImages.getAdapter();
            if (adapter == null || adapter.getUriList().size() < 1) {
                Toast.makeText(getContext(), "Please select at least one product image", Toast.LENGTH_SHORT).show();
                return;
            }


            ProductDto productDto = new ProductDto(binding.etProductTitle.getText().toString(),
                    binding.etDescription.getText().toString(), Double.parseDouble(binding.etPrice.getText().toString()), binding.etCondition.getText().toString(), Integer.parseInt(binding.etQty.getText().toString()), binding.spinnerCategory.getSelectedItem().toString().toString(), null, binding.etColors.getText().toString(), binding.etSizes.getText().toString());

            addProductVM.addProduct(productDto, adapter.getUriList(), () -> {
                Intent intent = new Intent(getContext(), MainActivity.class);
                intent.putExtra("menuItem", "account");
                startActivity(intent);
            });
        });

        return binding.getRoot();
    }

    private ActivityResultLauncher<String[]> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.OpenMultipleDocuments(), new ActivityResultCallback<List<Uri>>() {
        @Override
        public void onActivityResult(List<Uri> o) {
            if(o.size() > 0) {
                binding.rvProductImages.setAdapter(new AddProductRvImagesAdapter(o));
            }
        }
    });
}