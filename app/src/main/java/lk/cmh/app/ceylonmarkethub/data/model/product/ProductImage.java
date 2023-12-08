package lk.cmh.app.ceylonmarkethub.data.model.product;

public class ProductImage {
    private Long id;

    private String url;

    public ProductImage() {

    }

    public ProductImage(Long id, String url) {
        this.id = id;
        this.url = url;
    }

    public ProductImage(String url) {
        this.url = url;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
