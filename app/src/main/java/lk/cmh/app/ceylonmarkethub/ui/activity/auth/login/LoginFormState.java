package lk.cmh.app.ceylonmarkethub.ui.activity.auth.login;

import javax.annotation.Nullable;

public class LoginFormState {

    @Nullable
    private String emailError;

    @Nullable
    private String passwordError;

    private boolean isValid;

    public LoginFormState(@Nullable String emailError, @Nullable String passwordError) {
        this.emailError = emailError;
        this.passwordError = passwordError;
    }

    public LoginFormState(boolean isValid) {
        this.emailError = null;
        this.passwordError = null;
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

    public boolean isValid() {
        return isValid;
    }
}
