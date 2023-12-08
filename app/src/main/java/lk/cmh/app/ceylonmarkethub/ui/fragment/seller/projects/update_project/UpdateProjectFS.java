package lk.cmh.app.ceylonmarkethub.ui.fragment.seller.projects.update_project;

import javax.annotation.Nullable;

public class UpdateProjectFS {
    @Nullable
    private String nameError;
    @Nullable
    private String descriptionError;

    @Nullable
    private String categoryError;

    @Nullable
    private String skillsError;

    @Nullable
    private String budgetError;

    @Nullable
    private String durationError;

    private boolean isValid;

    public UpdateProjectFS(@Nullable String nameError, @Nullable String descriptionError, @Nullable String categoryError, @Nullable String skillsError, @Nullable String budgetError, @Nullable String durationError) {
        this.nameError = nameError;
        this.descriptionError = descriptionError;
        this.categoryError = categoryError;
        this.skillsError = skillsError;
        this.budgetError = budgetError;
        this.durationError = durationError;
    }

    public UpdateProjectFS(boolean isValid) {
        this.nameError = null;
        this.descriptionError = null;
        this.categoryError = null;
        this.skillsError = null;
        this.budgetError = null;
        this.durationError = null;
        this.isValid = isValid;
    }

    @Nullable
    public String getNameError() {
        return nameError;
    }

    public void setNameError(@Nullable String nameError) {
        this.nameError = nameError;
    }

    @Nullable
    public String getDescriptionError() {
        return descriptionError;
    }

    public void setDescriptionError(@Nullable String descriptionError) {
        this.descriptionError = descriptionError;
    }

    @Nullable
    public String getCategoryError() {
        return categoryError;
    }

    public void setCategoryError(@Nullable String categoryError) {
        this.categoryError = categoryError;
    }

    @Nullable
    public String getSkillsError() {
        return skillsError;
    }

    public void setSkillsError(@Nullable String skillsError) {
        this.skillsError = skillsError;
    }

    @Nullable
    public String getBudgetError() {
        return budgetError;
    }

    public void setBudgetError(@Nullable String budgetError) {
        this.budgetError = budgetError;
    }

    @Nullable
    public String getDurationError() {
        return durationError;
    }

    public void setDurationError(@Nullable String durationError) {
        this.durationError = durationError;
    }

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }
}
