package lk.cmh.app.ceylonmarkethub.ui.activity.orders;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.viewmodel.ViewModelInitializer;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import lk.cmh.app.ceylonmarkethub.CMH;
import lk.cmh.app.ceylonmarkethub.data.model.order_history.OrderHistory;
import lk.cmh.app.ceylonmarkethub.data.model.order_history.OrderHistoryDto;
import lk.cmh.app.ceylonmarkethub.data.model.product.Product;
import lk.cmh.app.ceylonmarkethub.data.model.user.User;
import lk.cmh.app.ceylonmarkethub.data.model.user.UserAddress;
import lk.cmh.app.ceylonmarkethub.data.repository.CartRepository;
import lk.cmh.app.ceylonmarkethub.data.repository.OrderHistoryRepository;
import lk.cmh.app.ceylonmarkethub.data.repository.ProductRepository;
import lk.cmh.app.ceylonmarkethub.data.repository.UserRepository;
import lk.cmh.app.ceylonmarkethub.data.util.RetrofitUtil;
import lk.cmh.app.ceylonmarkethub.ui.fragment.cart.CartViewModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderConfirmationVM extends ViewModel {

    private static final String TAG = OrderConfirmationVM.class.getSimpleName();
    private final MutableLiveData<List<Product>> products = new MutableLiveData<>();

    private ProductRepository productRepository;

    private OrderHistoryRepository orderHistoryRepository;
    private UserRepository userRepository;

    private MutableLiveData<UserAddress> address = new MutableLiveData<>();
    private MutableLiveData<User> user = new MutableLiveData<>();

    public OrderConfirmationVM(UserRepository userRepository, OrderHistoryRepository orderHistoryRepository) {
        productRepository = RetrofitUtil.getInstance().getRepository(ProductRepository.class);
        this.userRepository = userRepository;
        this.orderHistoryRepository = orderHistoryRepository;
        loadAddress();
        loadUser();
    }

    public static final ViewModelInitializer<OrderConfirmationVM> initializer = new ViewModelInitializer<>(OrderConfirmationVM.class, creationExtras -> {
        CMH instance = CMH.getInstance();
        SharedPreferences sharedPreferences = instance.getSharedPreferences("auth", Context.MODE_PRIVATE);
        UserRepository userRepository = RetrofitUtil.getInstance().getRepository(UserRepository.class, sharedPreferences);
        OrderHistoryRepository orderHistoryRepository = RetrofitUtil.getInstance().getRepository(OrderHistoryRepository.class, sharedPreferences);
        return new OrderConfirmationVM(userRepository, orderHistoryRepository);
    });


    public void loadProduct(long id) {
        productRepository.getProductById(id).enqueue(new Callback<Product>() {
            @Override
            public void onResponse(Call<Product> call, Response<Product> response) {
                if (products.getValue() != null && products.getValue().size() > 0) {
                    products.getValue().add(response.body());
                } else {
                    List<Product> productsList = new ArrayList<>();
                    productsList.add(response.body());
                    products.setValue(productsList);
                }
            }

            @Override
            public void onFailure(Call<Product> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

     public void loadAddress() {
         userRepository.getAddress().enqueue(new Callback<UserAddress>() {
             @Override
             public void onResponse(Call<UserAddress> call, Response<UserAddress> response) {
                 address.setValue(response.body());
             }

             @Override
             public void onFailure(Call<UserAddress> call, Throwable t) {
                 t.printStackTrace();
             }
         });
     }

     private void loadUser() {
         userRepository.getMe().enqueue(new Callback<User>() {
             @Override
             public void onResponse(Call<User> call, Response<User> response) {
                 user.setValue(response.body());
                 Log.i(TAG, user.toString());
             }

             @Override
             public void onFailure(Call<User> call, Throwable t) {
                 t.printStackTrace();
             }
         });
     }

     public void completeOrder(OrderHistoryDto orderHistoryDto, @Nullable Runnable runnable) {
        orderHistoryRepository.addOrder(orderHistoryDto).enqueue(new Callback<OrderHistory>() {
            @Override
            public void onResponse(Call<OrderHistory> call, Response<OrderHistory> response) {
                System.out.println(response);
                runnable.run();
            }

            @Override
            public void onFailure(Call<OrderHistory> call, Throwable t) {
                t.printStackTrace();
            }
        });
     }

    public MutableLiveData<UserAddress> getAddress() {
        return address;
    }

    public MutableLiveData<User> getUser() {
        return user;
    }

    public MutableLiveData<List<Product>> getProducts() {
        return products;
    }
}
