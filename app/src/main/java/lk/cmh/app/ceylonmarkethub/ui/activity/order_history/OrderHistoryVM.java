package lk.cmh.app.ceylonmarkethub.ui.activity.order_history;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.viewmodel.CreationExtras;
import androidx.lifecycle.viewmodel.ViewModelInitializer;

import java.util.List;

import kotlin.jvm.functions.Function1;
import lk.cmh.app.ceylonmarkethub.CMH;
import lk.cmh.app.ceylonmarkethub.data.model.order_history.OrderHistory;
import lk.cmh.app.ceylonmarkethub.data.repository.OrderHistoryRepository;
import lk.cmh.app.ceylonmarkethub.data.util.RetrofitUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderHistoryVM extends ViewModel {

    private OrderHistoryRepository orderHistoryRepository;

    private MutableLiveData<List<OrderHistory>> orderHistory = new MutableLiveData<>();

    public OrderHistoryVM(OrderHistoryRepository orderHistoryRepository) {
        this.orderHistoryRepository = orderHistoryRepository;
        loadOrderHistory();
    }

    public static final ViewModelInitializer<OrderHistoryVM> initializer = new ViewModelInitializer<>(OrderHistoryVM.class, new Function1<CreationExtras, OrderHistoryVM>() {
        @Override
        public OrderHistoryVM invoke(CreationExtras creationExtras) {
            CMH instance = CMH.getInstance();
            SharedPreferences sharedPreferences = instance.getSharedPreferences("auth", Context.MODE_PRIVATE);
            OrderHistoryRepository orderHistoryRepository = RetrofitUtil.getInstance().getRepository(OrderHistoryRepository.class, sharedPreferences);
            return new OrderHistoryVM(orderHistoryRepository);
        }
    });

    public void loadOrderHistory() {
        orderHistoryRepository.getAllOrders().enqueue(new Callback<List<OrderHistory>>() {
            @Override
            public void onResponse(Call<List<OrderHistory>> call, Response<List<OrderHistory>> response) {
                orderHistory.setValue(response.body());
            }

            @Override
            public void onFailure(Call<List<OrderHistory>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public MutableLiveData<List<OrderHistory>> getOrderHistory() {
        return orderHistory;
    }
}
