package lk.cmh.app.ceylonmarkethub.data.model.cart;

import javax.annotation.Nullable;

import lk.cmh.app.ceylonmarkethub.data.model.product.Product;
import lk.cmh.app.ceylonmarkethub.data.model.product.ProductColors;
import lk.cmh.app.ceylonmarkethub.data.model.product.ProductSize;

public class CartItem {

    private Long id;
    private Product product;
    private Integer quantity;
    private Double price;

    @Nullable
    private ProductColors productColor;

    @Nullable
    private ProductSize productSize;

    public CartItem() {
    }

    public CartItem(Long id, Product product, Integer quantity, Double price, @Nullable ProductColors productColor, @Nullable ProductSize productSize) {
        this.id = id;
        this.product = product;
        this.quantity = quantity;
        this.price = price;
        this.productColor = productColor;
        this.productSize = productSize;
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

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    @Nullable
    public ProductColors getProductColor() {
        return productColor;
    }

    public void setProductColor(@Nullable ProductColors productColor) {
        this.productColor = productColor;
    }

    @Nullable
    public ProductSize getProductSize() {
        return productSize;
    }

    public void setProductSize(@Nullable ProductSize productSize) {
        this.productSize = productSize;
    }

    @Override
    public String toString() {
        return "CartItem{" +
                "id=" + id +
                ", product=" + product +
                ", quantity=" + quantity +
                ", price=" + price +
                ", productColor=" + productColor +
                ", productSize=" + productSize +
                '}';
    }
}
