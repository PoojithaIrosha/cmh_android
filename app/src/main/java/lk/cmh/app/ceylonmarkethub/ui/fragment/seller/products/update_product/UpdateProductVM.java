package lk.cmh.app.ceylonmarkethub.ui.fragment.seller.products.update_product;

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
import lk.cmh.app.ceylonmarkethub.data.model.category.Category;
import lk.cmh.app.ceylonmarkethub.data.model.product.Product;
import lk.cmh.app.ceylonmarkethub.data.model.product.ProductDto;
import lk.cmh.app.ceylonmarkethub.data.repository.CategoryRepository;
import lk.cmh.app.ceylonmarkethub.data.repository.ProductRepository;
import lk.cmh.app.ceylonmarkethub.data.util.GsonUtil;
import lk.cmh.app.ceylonmarkethub.data.util.RetrofitUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateProductVM extends ViewModel {

    private static final String TAG = UpdateProductVM.class.getSimpleName();
    private MutableLiveData<UpdateProductFormState> addProductFormState = new MutableLiveData<>();
    private ProductRepository productRepository;
    private FirebaseStorage storage;
    private MutableLiveData<List<Category>> categoryList = new MutableLiveData<>();
    private CategoryRepository categoryRepository;
    private MutableLiveData<Product> product = new MutableLiveData<>();
    private MutableLiveData<Boolean> isImageChanged = new MutableLiveData<>(false);

    public UpdateProductVM(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.storage = FirebaseStorage.getInstance();
        loadCategories();
    }

    public static ViewModelInitializer<UpdateProductVM> initializer = new ViewModelInitializer<>(UpdateProductVM.class, new Function1<CreationExtras, UpdateProductVM>() {
        @Override
        public UpdateProductVM invoke(CreationExtras creationExtras) {
            SharedPreferences auth = CMH.getInstance().getSharedPreferences("auth", Context.MODE_PRIVATE);
            ProductRepository pr = RetrofitUtil.getInstance().getRepository(ProductRepository.class, auth);
            CategoryRepository cr = RetrofitUtil.getInstance().getRepository(CategoryRepository.class);
            return new UpdateProductVM(pr, cr);
        }
    });

    public void loadCategories() {
        categoryRepository.getCategories().enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                categoryList.setValue(response.body());
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public void loadProduct(Long id) {
        productRepository.getProductById(id).enqueue(new Callback<Product>() {
            @Override
            public void onResponse(Call<Product> call, Response<Product> response) {
                product.setValue(response.body());
            }

            @Override
            public void onFailure(Call<Product> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public void updateProductDataChanged(String title, String description, String price, String quantity, String condition) {
        UpdateProductFormState formState = addProductFormState.getValue();
        if (formState == null) {
            formState = new UpdateProductFormState(null, null, null, null, null);
        }

        if (title.isEmpty()) {
            formState.setTitleError("Title cannot be empty");
            addProductFormState.setValue(formState);
        } else {
            formState.setTitleError(null);
        }

        if (description.isEmpty()) {
            formState.setDescriptionError("Description cannot be empty");
            addProductFormState.setValue(formState);
        } else {
            formState.setDescriptionError(null);
        }

        if (price.isEmpty()) {
            formState.setPriceError("Price cannot be empty");
            addProductFormState.setValue(formState);
        } else {
            formState.setPriceError(null);
        }

        if (quantity.isEmpty()) {
            formState.setQuantityError("Quantity cannot be empty");
            addProductFormState.setValue(formState);
        } else {
            formState.setQuantityError(null);
        }

        if (condition.isEmpty()) {
            formState.setConditionError("Condition cannot be empty");
            addProductFormState.setValue(formState);
        } else {
            formState.setConditionError(null);
        }

        if (!title.isEmpty() && !description.isEmpty() && !price.isEmpty() && !quantity.isEmpty() && !condition.isEmpty()) {
            addProductFormState.setValue(new UpdateProductFormState(true));
        }
    }

    public MutableLiveData<UpdateProductFormState> getUpdateProductFormState() {
        return addProductFormState;
    }

    public void updateProduct(Long id, ProductDto productDto, List<Uri> imageList, Runnable runnable) {
        List<String> url = new ArrayList<>();
        if (isImageChanged.getValue()) {
            imageList.forEach(uri -> {
                String uuid = UUID.randomUUID().toString();
                url.add(uuid);
                StorageReference products = storage.getReference("products").child(uuid);
                products.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
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
            Set<ProductDto.ProductImageDto> imageDtos = url.stream().map(ProductDto.ProductImageDto::new).collect(Collectors.toSet());
            productDto.setProductImages(imageDtos);
        } else {
            productDto.setProductImages(null);
        }


        Log.i(TAG, "Product Dto: " + GsonUtil.getInstance().toJson(productDto));
        productRepository.updateProduct(id, productDto).enqueue(new Callback<Product>() {
            @Override
            public void onResponse(Call<Product> call, Response<Product> response) {
                if (response.isSuccessful()) {
                    Log.i(TAG, "Product added successfully");
                    runnable.run();
                } else {
                    try {
                        Log.i(TAG, response.errorBody().string());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void onFailure(Call<Product> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public MutableLiveData<List<Category>> getCategoryList() {
        return categoryList;
    }

    public MutableLiveData<Product> getProduct() {
        return product;
    }

    public MutableLiveData<Boolean> getIsImageChanged() {
        return isImageChanged;
    }
}
