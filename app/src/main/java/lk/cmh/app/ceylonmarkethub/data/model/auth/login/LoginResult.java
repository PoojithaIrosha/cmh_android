package lk.cmh.app.ceylonmarkethub.data.model.auth.login;

import lk.cmh.app.ceylonmarkethub.data.model.ErrorResponse;

public class LoginResult {

    private LoginResult() {
    }

    public static final class Success extends LoginResult {
        private LoginRespDto data;

        public Success(LoginRespDto data) {
            this.data = data;
        }

        public LoginRespDto getData() {
            return this.data;
        }
    }

    public final static class Error extends LoginResult {
        private ErrorResponse error;

        public Error(ErrorResponse error) {
            this.error = error;
        }

        public ErrorResponse getError() {
            return this.error;
        }
    }

}
