package lk.cmh.app.ceylonmarkethub.data.model.product;

import java.util.List;

import lk.cmh.app.ceylonmarkethub.data.model.review.Review;
import lk.cmh.app.ceylonmarkethub.data.model.category.Category;
import lk.cmh.app.ceylonmarkethub.data.model.user.Seller;

public class Product {
    private Long id;

    private String name;

    private String description;

    private Double price;

    private String productCondition;

    private int quantity;

    private List<ProductImage> productImages;

    private Category category;

    private Seller seller;

    private List<Review> reviews;

    private List<ProductColors> productColors;

    private List<ProductSize> productSizes;
    private boolean deleted;

    public Product() {

    }

    public Product(Long id, String name, String description, Double price, String productCondition, int quantity, List<ProductImage> productImages, Category category) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.productCondition = productCondition;
        this.quantity = quantity;
        this.productImages = productImages;
        this.category = category;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getProductCondition() {
        return productCondition;
    }

    public void setProductCondition(String productCondition) {
        this.productCondition = productCondition;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public List<ProductImage> getProductImages() {
        return productImages;
    }

    public void setProductImages(List<ProductImage> productImages) {
        this.productImages = productImages;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Seller getSeller() {
        return seller;
    }

    public void setSeller(Seller seller) {
        this.seller = seller;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    public List<ProductColors> getProductColors() {
        return productColors;
    }

    public void setProductColors(List<ProductColors> productColors) {
        this.productColors = productColors;
    }

    public List<ProductSize> getProductSizes() {
        return productSizes;
    }

    public void setProductSizes(List<ProductSize> productSizes) {
        this.productSizes = productSizes;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", productCondition='" + productCondition + '\'' +
                ", quantity=" + quantity +
                ", productImages=" + productImages +
                ", category=" + category +
                ", seller=" + seller +
                ", reviews=" + reviews +
                ", productColors=" + productColors +
                ", productSizes=" + productSizes +
                ", isDeleted=" + deleted +
                '}';
    }
}
