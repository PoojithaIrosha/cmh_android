package lk.cmh.app.ceylonmarkethub.data.repository;

import java.util.List;

import lk.cmh.app.ceylonmarkethub.data.model.order_history.OrderStatus;
import lk.cmh.app.ceylonmarkethub.data.model.seller.SellerOrderDto;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface SellerRepository {

    @GET("seller/orders")
    Call<List<SellerOrderDto>> getSellerOrders();

    @PUT("seller/orders/{id}")
    Call<Void> updateOrderStatus(@Path("id") long id, @Query("status") String status);

}
