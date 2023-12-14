package lk.cmh.app.ceylonmarkethub.ui.fragment.seller.products.update_product;

import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import lk.cmh.app.ceylonmarkethub.R;
import lk.cmh.app.ceylonmarkethub.data.adapter.rv.AddProductRvImagesAdapter;
import lk.cmh.app.ceylonmarkethub.data.model.category.Category;
import lk.cmh.app.ceylonmarkethub.data.model.product.Product;
import lk.cmh.app.ceylonmarkethub.data.model.product.ProductColors;
import lk.cmh.app.ceylonmarkethub.data.model.product.ProductDto;
import lk.cmh.app.ceylonmarkethub.data.model.product.ProductImage;
import lk.cmh.app.ceylonmarkethub.data.model.product.ProductSize;
import lk.cmh.app.ceylonmarkethub.data.util.FragmentUtil;
import lk.cmh.app.ceylonmarkethub.databinding.FragmentUpdateProductBinding;
import lk.cmh.app.ceylonmarkethub.ui.fragment.seller.products.product_list.ProductListFragment;

public class UpdateProductFragment extends Fragment {
    private static final String TAG = UpdateProductFragment.class.getSimpleName();
    private FragmentUpdateProductBinding binding;
    private UpdateProductVM updateProductVM;
    private FirebaseStorage storage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = FragmentUpdateProductBinding.inflate(getLayoutInflater());
        updateProductVM = new ViewModelProvider(this, ViewModelProvider.Factory.from(UpdateProductVM.initializer)).get(UpdateProductVM.class);
        storage = FirebaseStorage.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Bundle arguments = getArguments();
        long id = arguments.getLong("pid");

        updateProductVM.loadProduct(id);

        updateProductVM.getProduct().observe(getActivity(), new Observer<Product>() {
            @Override
            public void onChanged(Product product) {
                binding.etProductTitle.setText(product.getName());
                binding.etDescription.setText(product.getDescription());
                binding.etPrice.setText(String.valueOf(product.getPrice()));
                binding.etQty.setText(String.valueOf(product.getQuantity()));
                binding.spinnerCategory.setSelection(Integer.parseInt(String.valueOf(product.getCategory().getId())));
                binding.etCondition.setText(product.getProductCondition());
                binding.etSizes.setText(product.getProductSizes().stream().map(ProductSize::getName).collect(Collectors.joining(", ")));
                binding.etColors.setText(product.getProductColors().stream().map(ProductColors::getName).collect(Collectors.joining(", ")));

                ArrayAdapter<String> adapter = (ArrayAdapter<String>) binding.spinnerCategory.getAdapter();
                binding.spinnerCategory.setSelection(adapter.getPosition(product.getCategory().getName()));

                updateProductVM.getUpdateProductFormState().setValue(null);

                binding.etProductTitle.setError(null);
                binding.etDescription.setError(null);
                binding.etPrice.setError(null);
                binding.etQty.setError(null);
                binding.etCondition.setError(null);
                binding.etSizes.setError(null);
                binding.etColors.setError(null);

                for (ProductImage productImage : product.getProductImages()) {
                    storage.getReference("products/" + productImage.getUrl()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            updateProductVM.getUpdateProductFormState().setValue(null);
                            AddProductRvImagesAdapter adapter = (AddProductRvImagesAdapter) binding.rvProductImages.getAdapter();
                            if (adapter == null) {
                                binding.rvProductImages.setAdapter(new AddProductRvImagesAdapter(Arrays.asList(uri)));
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


        updateProductVM.getCategoryList().observe(getActivity(), new Observer<List<Category>>() {
            @Override
            public void onChanged(List<Category> categories) {
                List<String> list = categories.stream().map(Category::getName).collect(Collectors.toList());
                list.add(0, "Select");
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, list);
                arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                binding.spinnerCategory.setAdapter(arrayAdapter);
            }
        });

        updateProductVM.getUpdateProductFormState().observe(getActivity(), new Observer<UpdateProductFormState>() {
            @Override
            public void onChanged(UpdateProductFormState addProductFormState) {
                if (addProductFormState == null) {
                    return;
                }
                binding.btnUpdateProduct.setEnabled(addProductFormState.isValid());

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
                updateProductVM.updateProductDataChanged(
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

        binding.btnUpdateProduct.setOnClickListener(v -> {
            if (binding.spinnerCategory.getSelectedItemPosition() == 0) {
                Toast.makeText(getContext(), "Select a category", Toast.LENGTH_SHORT).show();
                return;
            }

            AddProductRvImagesAdapter adapter = (AddProductRvImagesAdapter) binding.rvProductImages.getAdapter();
            if (adapter == null || adapter.getUriList().size() < 1) {
                Toast.makeText(getContext(), "Please select at least one product image", Toast.LENGTH_SHORT).show();
                return;
            }

            String colors = null;
            if (!binding.etColors.getText().toString().isEmpty()) {
                colors = binding.etColors.getText().toString();
            }

            String sizes = null;
            if (!binding.etSizes.getText().toString().isEmpty()) {
                sizes = binding.etSizes.getText().toString();
            }

            ProductDto productDto = new ProductDto(binding.etProductTitle.getText().toString(),
                    binding.etDescription.getText().toString(), Double.parseDouble(binding.etPrice.getText().toString()), binding.etCondition.getText().toString(), Integer.parseInt(binding.etQty.getText().toString()), binding.spinnerCategory.getSelectedItem().toString().toString(), null, colors, sizes);

            updateProductVM.updateProduct(id, productDto, adapter.getUriList(), () -> {
                getActivity().finish();
            });

        });

        return binding.getRoot();
    }

    private ActivityResultLauncher<String[]> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.OpenMultipleDocuments(), new ActivityResultCallback<List<Uri>>() {
        @Override
        public void onActivityResult(List<Uri> o) {
            updateProductVM.getIsImageChanged().setValue(true);
            binding.rvProductImages.setAdapter(new AddProductRvImagesAdapter(o));
        }
    });
}