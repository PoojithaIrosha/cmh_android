package lk.cmh.app.ceylonmarkethub.data.model.projects;

import java.util.Date;
import java.util.List;

import lk.cmh.app.ceylonmarkethub.data.model.user.Seller;

public class Project {
    private Long id;

    private String name;

    private String description;

    private String category;

    private String skills;

    private Double budget;

    private String duration;
    private List<ProjectImage> projectImages;

    private Seller seller;

    private boolean enabled;

    private Date createdAt;
    private Date updatedAt;

    public Project() {
    }

    public Project(Long id, String name, String description, String category, String skills, Double budget, String duration, List<ProjectImage> projectImages, Seller seller, boolean enabled, Date createdAt, Date updatedAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.category = category;
        this.skills = skills;
        this.budget = budget;
        this.duration = duration;
        this.projectImages = projectImages;
        this.seller = seller;
        this.enabled = enabled;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSkills() {
        return skills;
    }

    public void setSkills(String skills) {
        this.skills = skills;
    }

    public Double getBudget() {
        return budget;
    }

    public void setBudget(Double budget) {
        this.budget = budget;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public List<ProjectImage> getProjectImages() {
        return projectImages;
    }

    public void setProjectImages(List<ProjectImage> projectImages) {
        this.projectImages = projectImages;
    }

    public Seller getSeller() {
        return seller;
    }

    public void setSeller(Seller seller) {
        this.seller = seller;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "Project{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", category='" + category + '\'' +
                ", skills='" + skills + '\'' +
                ", budget=" + budget +
                ", duration='" + duration + '\'' +
                ", projectImages=" + projectImages +
                ", seller=" + seller +
                ", enabled=" + enabled +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
