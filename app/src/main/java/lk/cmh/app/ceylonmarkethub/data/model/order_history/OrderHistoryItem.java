package lk.cmh.app.ceylonmarkethub.data.model.order_history;

import lk.cmh.app.ceylonmarkethub.data.model.product.Product;
import lk.cmh.app.ceylonmarkethub.data.model.product.ProductColors;
import lk.cmh.app.ceylonmarkethub.data.model.product.ProductSize;

public class OrderHistoryItem {
    private Long id;

    private Product product;

    private double price;

    private int quantity;

    private ProductSize productSize;

    private ProductColors productColor;

    private OrderStatus status;

    public OrderHistoryItem() {
    }

    public OrderHistoryItem(Long id, Product product, double price, int quantity, ProductSize productSize, ProductColors productColor, OrderStatus orderStatus) {
        this.id = id;
        this.product = product;
        this.price = price;
        this.quantity = quantity;
        this.productSize = productSize;
        this.productColor = productColor;
        this.status = orderStatus;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public ProductSize getProductSize() {
        return productSize;
    }

    public void setProductSize(ProductSize productSize) {
        this.productSize = productSize;
    }

    public ProductColors getProductColor() {
        return productColor;
    }

    public void setProductColor(ProductColors productColor) {
        this.productColor = productColor;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "OrderHistoryItem{" +
                "id=" + id +
                ", product=" + product +
                ", price=" + price +
                ", quantity=" + quantity +
                ", productSize=" + productSize +
                ", productColor=" + productColor +
                ", status=" + status +
                '}';
    }
}
