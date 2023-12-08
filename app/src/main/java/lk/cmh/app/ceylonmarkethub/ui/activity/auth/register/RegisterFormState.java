package lk.cmh.app.ceylonmarkethub.ui.activity.auth.register;

import javax.annotation.Nullable;

public class RegisterFormState {

    @Nullable
    String firstNameError;

    private String emailError;
    @Nullable
    private String passwordError;
    private boolean isValid;

    public RegisterFormState(@Nullable String firstNameError, @Nullable String emailError, @Nullable String passwordError) {
        this.firstNameError = firstNameError;
        this.emailError = emailError;
        this.passwordError = passwordError;
    }
    public RegisterFormState(boolean isValid) {
        this.emailError = null;
        this.passwordError = null;
        this.firstNameError = null;
        this.isValid = isValid;
    }

    @Nullable
    public String getEmailError() {
        return emailError;
    }
    @Nullable
    public String getPasswordError() {
        return passwordError;
    }

    @Nullable
    public String getFirstNameError() {
        return firstNameError;
    }

    public void setFirstNameError(@Nullable String firstNameError) {
        this.firstNameError = firstNameError;
    }

    public void setEmailError(String emailError) {
        this.emailError = emailError;
    }

    public void setPasswordError(@Nullable String passwordError) {
        this.passwordError = passwordError;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }

    public boolean isValid() {
        return isValid;
    }
}
