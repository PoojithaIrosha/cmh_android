package lk.cmh.app.ceylonmarkethub.ui.fragment.seller.products.add_product;

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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import kotlin.jvm.functions.Function1;
import lk.cmh.app.ceylonmarkethub.CMH;
import lk.cmh.app.ceylonmarkethub.data.model.category.Category;
import lk.cmh.app.ceylonmarkethub.data.model.product.Product;
import lk.cmh.app.ceylonmarkethub.data.model.product.ProductDto;
import lk.cmh.app.ceylonmarkethub.data.repository.CategoryRepository;
import lk.cmh.app.ceylonmarkethub.data.repository.ProductRepository;
import lk.cmh.app.ceylonmarkethub.data.util.RetrofitUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddProductVM extends ViewModel {

    private static final String TAG = AddProductVM.class.getSimpleName();
    private MutableLiveData<AddProductFormState> addProductFormState = new MutableLiveData<>();
    private ProductRepository productRepository;
    private FirebaseStorage storage;
    private MutableLiveData<List<Category>> categoryList = new MutableLiveData<>();
    private CategoryRepository categoryRepository;

    public AddProductVM(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.storage = FirebaseStorage.getInstance();
        loadCategories();
    }

    public static ViewModelInitializer<AddProductVM> initializer = new ViewModelInitializer<>(AddProductVM.class, new Function1<CreationExtras, AddProductVM>() {
        @Override
        public AddProductVM invoke(CreationExtras creationExtras) {
            SharedPreferences auth = CMH.getInstance().getSharedPreferences("auth", Context.MODE_PRIVATE);
            ProductRepository pr = RetrofitUtil.getInstance().getRepository(ProductRepository.class, auth);
            CategoryRepository cr = RetrofitUtil.getInstance().getRepository(CategoryRepository.class);
            return new AddProductVM(pr, cr);
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

    public void addProductDataChanged(String title, String description, String price, String quantity, String condition) {
        AddProductFormState formState = addProductFormState.getValue();
        if (formState == null) {
            formState = new AddProductFormState(null, null, null, null, null);
        }

        if (title.isEmpty()) {
            formState.setTitleError("Title cannot be empty");
            addProductFormState.setValue(formState);
        }else {
            formState.setTitleError(null);
        }

        if (description.isEmpty()) {
            formState.setDescriptionError("Description cannot be empty");
            addProductFormState.setValue(formState);
        }else {
            formState.setDescriptionError(null);
        }

        if (price.isEmpty()) {
            formState.setPriceError("Price cannot be empty");
            addProductFormState.setValue(formState);
        }else {
            formState.setPriceError(null);
        }

        if (quantity.isEmpty()) {
            formState.setQuantityError("Quantity cannot be empty");
            addProductFormState.setValue(formState);
        }else {
            formState.setQuantityError(null);
        }

        if (condition.isEmpty()) {
            formState.setConditionError("Condition cannot be empty");
            addProductFormState.setValue(formState);
        }else {
            formState.setConditionError(null);
        }

        if (!title.isEmpty() && !description.isEmpty() && !price.isEmpty() && !quantity.isEmpty() && !condition.isEmpty()) {
            addProductFormState.setValue(new AddProductFormState(true));
        }
    }

    public MutableLiveData<AddProductFormState> getAddProductFormState() {
        return addProductFormState;
    }

    public void addProduct(ProductDto productDto, List<Uri> imageList, Runnable runnable) {
        List<String> url = new ArrayList<>();

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
                    url.remove(uuid);
                }
            });
        });

        productDto.setProductImages(url.stream().map(ProductDto.ProductImageDto::new).collect(Collectors.toSet()));

        productRepository.saveProduct(productDto).enqueue(new Callback<Product>() {
            @Override
            public void onResponse(Call<Product> call, Response<Product> response) {
                Log.i(TAG, "Product added successfully");
                runnable.run();
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
}
