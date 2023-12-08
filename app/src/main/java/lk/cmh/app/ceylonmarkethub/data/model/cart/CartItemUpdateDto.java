package lk.cmh.app.ceylonmarkethub.data.model.cart;

public class CartItemUpdateDto {
    private Long cartItemId;
    private Integer quantity;

    public CartItemUpdateDto() {
    }

    public CartItemUpdateDto(Long cartItemId, Integer quantity) {
        this.cartItemId = cartItemId;
        this.quantity = quantity;
    }

    public Long getCartItemId() {
        return cartItemId;
    }

    public void setCartItemId(Long cartItemId) {
        this.cartItemId = cartItemId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "CartItemUpdateDto{" +
                "cartItemId=" + cartItemId +
                ", quantity=" + quantity +
                '}';
    }
}
