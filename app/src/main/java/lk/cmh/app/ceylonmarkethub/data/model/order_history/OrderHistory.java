package lk.cmh.app.ceylonmarkethub.data.model.order_history;

import java.sql.Timestamp;
import java.util.List;

public class OrderHistory {
    private Long id;

    private double total;

    private List<OrderHistoryItem> orderHistoryItems;

    private Timestamp purchased_at;

    public OrderHistory() {
    }

    public OrderHistory(Long id, double total, List<OrderHistoryItem> orderHistoryItems, Timestamp purchased_at) {
        this.id = id;
        this.total = total;
        this.orderHistoryItems = orderHistoryItems;
        this.purchased_at = purchased_at;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public List<OrderHistoryItem> getOrderHistoryItems() {
        return orderHistoryItems;
    }

    public void setOrderHistoryItems(List<OrderHistoryItem> orderHistoryItems) {
        this.orderHistoryItems = orderHistoryItems;
    }

    public Timestamp getPurchased_at() {
        return purchased_at;
    }

    public void setPurchased_at(Timestamp purchased_at) {
        this.purchased_at = purchased_at;
    }

    @Override
    public String toString() {
        return "OrderHistory{" +
                "id=" + id +
                ", total=" + total +
                ", orderHistoryItems=" + orderHistoryItems +
                ", purchased_at=" + purchased_at +
                '}';
    }
}
