package lk.cmh.app.ceylonmarkethub.data.model.projects;

public class ProjectImage {
    private Long id;

    private String url;

    public ProjectImage() {
    }

    public ProjectImage(Long id, String url) {
        this.id = id;
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

    @Override
    public String toString() {
        return "ProjectImage{" +
                "id=" + id +
                ", url='" + url + '\'' +
                '}';
    }
}
