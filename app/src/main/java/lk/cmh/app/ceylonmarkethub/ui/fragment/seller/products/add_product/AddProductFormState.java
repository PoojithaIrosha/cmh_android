package lk.cmh.app.ceylonmarkethub.ui.fragment.seller.products.add_product;

import javax.annotation.Nullable;

public class AddProductFormState {

    @Nullable
    private String titleError;

    @Nullable
    private String descriptionError;

    @Nullable
    private String priceError;

    @Nullable
    private String quantityError;

    @Nullable
    private String conditionError;

    private boolean isValid;

    public AddProductFormState(@Nullable String titleError, @Nullable String descriptionError, @Nullable String priceError, @Nullable String quantityError, @Nullable String conditionError) {
        this.titleError = titleError;
        this.descriptionError = descriptionError;
        this.priceError = priceError;
        this.quantityError = quantityError;
        this.conditionError = conditionError;
    }

    public AddProductFormState(boolean isValid) {
        this.titleError = null;
        this.descriptionError = null;
        this.priceError = null;
        this.quantityError = null;
        this.conditionError = null;
        this.isValid = isValid;
    }

    @Nullable
    public String getTitleError() {
        return titleError;
    }

    public void setTitleError(@Nullable String titleError) {
        this.titleError = titleError;
    }

    @Nullable
    public String getDescriptionError() {
        return descriptionError;
    }

    public void setDescriptionError(@Nullable String descriptionError) {
        this.descriptionError = descriptionError;
    }

    @Nullable
    public String getPriceError() {
        return priceError;
    }

    public void setPriceError(@Nullable String priceError) {
        this.priceError = priceError;
    }

    @Nullable
    public String getQuantityError() {
        return quantityError;
    }

    public void setQuantityError(@Nullable String quantityError) {
        this.quantityError = quantityError;
    }

    @Nullable
    public String getConditionError() {
        return conditionError;
    }

    public void setConditionError(@Nullable String conditionError) {
        this.conditionError = conditionError;
    }

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }
}
