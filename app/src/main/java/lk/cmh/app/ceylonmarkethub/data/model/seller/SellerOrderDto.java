package lk.cmh.app.ceylonmarkethub.data.model.seller;

import java.sql.Timestamp;
import java.util.List;

public class SellerOrderDto {
    long id;
    double total;
    List<SellerOrderItemDto> orderItems;
    Timestamp purchased_at;

    public SellerOrderDto() {
    }

    public SellerOrderDto(long id, double total, List<SellerOrderItemDto> orderItems, Timestamp purchased_at) {
        this.id = id;
        this.total = total;
        this.orderItems = orderItems;
        this.purchased_at = purchased_at;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public List<SellerOrderItemDto> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<SellerOrderItemDto> orderItems) {
        this.orderItems = orderItems;
    }

    public Timestamp getPurchased_at() {
        return purchased_at;
    }

    public void setPurchased_at(Timestamp purchased_at) {
        this.purchased_at = purchased_at;
    }

    @Override
    public String toString() {
        return "SellerOrderDto{" +
                "id=" + id +
                ", total=" + total +
                ", orderItems=" + orderItems +
                ", purchased_at=" + purchased_at +
                '}';
    }
}
