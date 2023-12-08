package lk.cmh.app.ceylonmarkethub.data.model.wishlist;

import lk.cmh.app.ceylonmarkethub.data.model.product.Product;

public class WishListItem {
    private Long id;
    private Product product;

    public WishListItem() {
    }

    public WishListItem(Long id, Product product) {
        this.id = id;
        this.product = product;
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

    @Override
    public String toString() {
        return "WishListItem{" +
                "id=" + id +
                ", product=" + product +
                '}';
    }
}
