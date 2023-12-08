package lk.cmh.app.ceylonmarkethub.data.repository;


import java.util.List;

import lk.cmh.app.ceylonmarkethub.data.model.order_history.OrderHistory;
import lk.cmh.app.ceylonmarkethub.data.model.order_history.OrderHistoryDto;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface OrderHistoryRepository {

    @GET("orders")
    Call<List<OrderHistory>> getAllOrders();

    @GET("orders/{id}")
    Call<OrderHistory> getOrder(@Path("id") long id);

    @POST("orders")
    Call<OrderHistory> addOrder(@Body OrderHistoryDto orderHistoryDto);

}
