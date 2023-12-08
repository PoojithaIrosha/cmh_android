package lk.cmh.app.ceylonmarkethub.ui.activity.shipping_address;

import javax.annotation.Nullable;

public class ShippingFormState {

    @Nullable
    private String contactNameError;

    @Nullable
    private String mobileNumberError;

    @Nullable
    private String addressLine1Error;

    @Nullable
    private String provinceError;

    @Nullable
    private String cityError;

    @Nullable
    private String zipCodeError;

    private boolean isValid;

    public ShippingFormState() {
        this.isValid = false;
        this.contactNameError = null;
        this.mobileNumberError = null;
        this.addressLine1Error = null;
        this.cityError = null;
        this.provinceError = null;
        this.zipCodeError = null;
    }

    public ShippingFormState(@Nullable String contactNameError, @Nullable String mobileNumberError, @Nullable String addressLine1Error, @Nullable String provinceError, @Nullable String cityError, @Nullable String zipCodeError) {
        this.contactNameError = contactNameError;
        this.mobileNumberError = mobileNumberError;
        this.addressLine1Error = addressLine1Error;
        this.provinceError = provinceError;
        this.cityError = cityError;
        this.zipCodeError = zipCodeError;
    }

    public ShippingFormState(boolean isValid) {
        this.isValid = isValid;
        this.contactNameError = null;
        this.mobileNumberError = null;
        this.addressLine1Error = null;
        this.cityError = null;
        this.provinceError = null;
        this.zipCodeError = null;
    }

    @Nullable
    public String getContactNameError() {
        return contactNameError;
    }

    public void setContactNameError(@Nullable String contactNameError) {
        this.contactNameError = contactNameError;
    }

    @Nullable
    public String getMobileNumberError() {
        return mobileNumberError;
    }

    public void setMobileNumberError(@Nullable String mobileNumberError) {
        this.mobileNumberError = mobileNumberError;
    }

    @Nullable
    public String getAddressLine1Error() {
        return addressLine1Error;
    }

    public void setAddressLine1Error(@Nullable String addressLine1Error) {
        this.addressLine1Error = addressLine1Error;
    }

    @Nullable
    public String getProvinceError() {
        return provinceError;
    }

    public void setProvinceError(@Nullable String provinceError) {
        this.provinceError = provinceError;
    }

    @Nullable
    public String getCityError() {
        return cityError;
    }

    public void setCityError(@Nullable String cityError) {
        this.cityError = cityError;
    }

    @Nullable
    public String getZipCodeError() {
        return zipCodeError;
    }

    public void setZipCodeError(@Nullable String zipCodeError) {
        this.zipCodeError = zipCodeError;
    }

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }

}
