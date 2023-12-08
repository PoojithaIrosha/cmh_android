package lk.cmh.app.ceylonmarkethub.data.model.projects;

import java.util.Set;

import lk.cmh.app.ceylonmarkethub.data.model.product.ProductDto;

public class ProjectDto {
    private String name;
    private String description;
    private String category;
    private String skills;
    private Double budget;
    private String duration;

    private Set<ProjectDto.ProjectImageDto> projectImages;

    public ProjectDto() {
    }

    public ProjectDto(String name, String description, String category, String skills, Double budget, String duration, Set<ProjectImageDto> projectImages) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.skills = skills;
        this.budget = budget;
        this.duration = duration;
        this.projectImages = projectImages;
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

    public Set<ProjectImageDto> getProjectImages() {
        return projectImages;
    }

    public void setProjectImages(Set<ProjectImageDto> projectImages) {
        this.projectImages = projectImages;
    }

    @Override
    public String toString() {
        return "ProjectDto{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", category='" + category + '\'' +
                ", skills='" + skills + '\'' +
                ", budget=" + budget +
                ", duration='" + duration + '\'' +
                ", projectImages=" + projectImages +
                '}';
    }

    public static class ProjectImageDto {
        private String url;

        public ProjectImageDto() {
        }

        public ProjectImageDto(String url) {
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
            return "ProjectImageDto{" +
                    "url='" + url + '\'' +
                    '}';
        }
    }
}
