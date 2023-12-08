package lk.cmh.app.ceylonmarkethub.data.model.wishlist;

import java.util.Date;
import java.util.List;

public class Wishlist {

    private Long id;
    private List<WishListItem> wishlistItems;
    private Date created_at;

    public Wishlist() {
    }

    public Wishlist(Long id, List<WishListItem> wishListItems, Date created_at) {
        this.id = id;
        this.wishlistItems = wishListItems;
        this.created_at = created_at;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<WishListItem> getWishListItems() {
        return wishlistItems;
    }

    public void setWishListItems(List<WishListItem> wishListItems) {
        this.wishlistItems = wishListItems;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }

    @Override
    public String toString() {
        return "Wishlist{" +
                "id=" + id +
                ", wishListItems=" + wishlistItems +
                ", created_at=" + created_at +
                '}';
    }
}
