package lk.cmh.app.ceylonmarkethub.data.model;

import java.io.Serializable;

import lk.cmh.app.ceylonmarkethub.data.model.product.Product;

public class OrderDto implements Serializable {

    private Long productId;
    private Long size;
    private Long color;

    private Integer quantity;

    private Product product;

    public OrderDto() {
    }

    public OrderDto(Long productId, Long size, Long color, Integer quantity, Product product) {
        this.productId = productId;
        this.size = size;
        this.color = color;
        this.quantity = quantity;
        this.product = product;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public Long getColor() {
        return color;
    }

    public void setColor(Long color) {
        this.color = color;
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

    @Override
    public String toString() {
        return "OrderDto{" +
                "productId=" + productId +
                ", size=" + size +
                ", color=" + color +
                ", quantity=" + quantity +
                ", product=" + product +
                '}';
    }
}
