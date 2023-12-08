package lk.cmh.app.ceylonmarkethub.data.model.order_history;

import java.util.List;

public class OrderHistoryDto {

    private double total;
    private List<OrderHistoryItemDto> orderItems;

    public OrderHistoryDto() {
    }

    public OrderHistoryDto(double total, List<OrderHistoryItemDto> orderItems) {
        this.total = total;
        this.orderItems = orderItems;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public List<OrderHistoryItemDto> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderHistoryItemDto> orderItems) {
        this.orderItems = orderItems;
    }

    @Override
    public String toString() {
        return "OrderHistoryDto{" +
                "total=" + total +
                ", orderItems=" + orderItems +
                '}';
    }

}
