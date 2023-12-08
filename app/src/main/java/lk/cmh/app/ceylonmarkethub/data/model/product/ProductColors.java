package lk.cmh.app.ceylonmarkethub.data.model.product;

public class ProductColors {
    private Long id;

    private String name;

    public ProductColors() {
    }

    public ProductColors(Long id, String name) {
        this.id = id;
        this.name = name;
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

    @Override
    public String toString() {
        return "ProductColors{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}

