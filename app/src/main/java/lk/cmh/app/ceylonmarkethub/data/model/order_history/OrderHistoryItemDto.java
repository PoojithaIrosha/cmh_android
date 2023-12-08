package lk.cmh.app.ceylonmarkethub.data.model.order_history;

public class OrderHistoryItemDto {
    private long productId;
    private int quantity;
    private long sizeId;
    private long colorId;

    public OrderHistoryItemDto() {
    }

    public OrderHistoryItemDto(long productId, int quantity, long sizeId, long colorId) {
        this.productId = productId;
        this.quantity = quantity;
        this.sizeId = sizeId;
        this.colorId = colorId;
    }

    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public long getSizeId() {
        return sizeId;
    }

    public void setSizeId(long sizeId) {
        this.sizeId = sizeId;
    }

    public long getColorId() {
        return colorId;
    }

    public void setColorId(long colorId) {
        this.colorId = colorId;
    }

    @Override
    public String toString() {
        return "OrderHistoryItemDto{" +
                "productId=" + productId +
                ", quantity=" + quantity +
                ", sizeId=" + sizeId +
                ", colorId=" + colorId +
                '}';
    }
}
