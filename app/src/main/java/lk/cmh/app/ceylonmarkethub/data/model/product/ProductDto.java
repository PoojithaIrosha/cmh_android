package lk.cmh.app.ceylonmarkethub.data.model.product;

import java.util.Set;
import java.util.concurrent.locks.Condition;

public class ProductDto {
    private String name;
    private String description;
    private Double price;
    private String productCondition;
    private int quantity;
    private String category;
    private Set<ProductImageDto> productImages;
    private String colors;
    private String sizes;


    public ProductDto() {
    }

    public ProductDto(String name, String description, Double price, String productCondition, int quantity, String category, Set<ProductImageDto> productImages, String colors, String sizes) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.productCondition = productCondition;
        this.quantity = quantity;
        this.category = category;
        this.productImages = productImages;
        this.colors = colors;
        this.sizes = sizes;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Set<ProductImageDto> getProductImages() {
        return productImages;
    }

    public void setProductImages(Set<ProductImageDto> productImages) {
        this.productImages = productImages;
    }

    public String getColors() {
        return colors;
    }

    public void setColors(String colors) {
        this.colors = colors;
    }

    public String getSizes() {
        return sizes;
    }

    public void setSizes(String sizes) {
        this.sizes = sizes;
    }

    @Override
    public String toString() {
        return "ProductDto{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", productCondition='" + productCondition + '\'' +
                ", quantity=" + quantity +
                ", category='" + category + '\'' +
                ", productImages=" + productImages +
                ", colors='" + colors + '\'' +
                ", sizes='" + sizes + '\'' +
                '}';
    }

    public static class ProductImageDto {
        private String url;

        public ProductImageDto() {
        }

        public ProductImageDto(String url) {
            this.url = url;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        @Override
        public String toString() {
            return "ProductImageDto{" +
                    "url='" + url + '\'' +
                    '}';
        }
    }

}
