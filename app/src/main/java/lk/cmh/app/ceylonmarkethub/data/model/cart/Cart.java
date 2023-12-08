package lk.cmh.app.ceylonmarkethub.data.model.cart;

import java.util.Date;
import java.util.List;

public class Cart {
    private Long id;
    private List<CartItem> cartItems;
    private Date created_at;

    public Cart() {
    }

    public Cart(Long id, List<CartItem> cartItems, Date created_at) {
        this.id = id;
        this.cartItems = cartItems;
        this.created_at = created_at;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<CartItem> getCartItems() {
        return cartItems;
    }

    public void setCartItems(List<CartItem> cartItems) {
        this.cartItems = cartItems;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }

    @Override
    public String toString() {
        return "Cart{" +
                "id=" + id +
                ", cartItems=" + cartItems +
                ", created_at=" + created_at +
                '}';
    }
}
