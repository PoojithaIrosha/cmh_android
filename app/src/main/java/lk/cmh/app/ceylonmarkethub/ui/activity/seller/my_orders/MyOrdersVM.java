package lk.cmh.app.ceylonmarkethub.ui.activity.seller.my_orders;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.viewmodel.CreationExtras;
import androidx.lifecycle.viewmodel.ViewModelInitializer;

import java.util.List;

import kotlin.jvm.functions.Function1;
import lk.cmh.app.ceylonmarkethub.CMH;
import lk.cmh.app.ceylonmarkethub.data.model.seller.SellerOrderDto;
import lk.cmh.app.ceylonmarkethub.data.repository.SellerRepository;
import lk.cmh.app.ceylonmarkethub.data.util.RetrofitUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyOrdersVM extends ViewModel {

    private MutableLiveData<List<SellerOrderDto>> sellerOrders = new MutableLiveData<>();
    private SellerRepository sellerRepository;

    public MyOrdersVM(SellerRepository sellerRepository) {
        this.sellerRepository = sellerRepository;
        loadSellersOrders();
    }

    public static final ViewModelInitializer<MyOrdersVM> initializer = new ViewModelInitializer<>(MyOrdersVM.class, new Function1<CreationExtras, MyOrdersVM>() {
        @Override
        public MyOrdersVM invoke(CreationExtras creationExtras) {
            SellerRepository sellerRepository = RetrofitUtil.getInstance().getRepository(SellerRepository.class, CMH.getInstance().getSharedPreferences("auth", Context.MODE_PRIVATE));
            return new MyOrdersVM(sellerRepository);
        }
    });

    public void loadSellersOrders() {
        sellerRepository.getSellerOrders().enqueue(new Callback<List<SellerOrderDto>>() {
            @Override
            public void onResponse(Call<List<SellerOrderDto>> call, Response<List<SellerOrderDto>> response) {
                sellerOrders.setValue(response.body());
            }

            @Override
            public void onFailure(Call<List<SellerOrderDto>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public MutableLiveData<List<SellerOrderDto>> getSellerOrders() {
        return sellerOrders;
    }
}
