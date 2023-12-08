package lk.cmh.app.ceylonmarkethub.data.model.auth.register;

import lk.cmh.app.ceylonmarkethub.data.model.ErrorResponse;

public class RegisterResult {

    private RegisterResult() {
    }

    public static final class Success extends RegisterResult {
        private String data;

        public Success(String data) {
            this.data = data;
        }

        public String getData() {
            return this.data;
        }
    }

    public final static class Error extends RegisterResult {
        private ErrorResponse error;

        public Error(ErrorResponse error) {
            this.error = error;
        }

        public ErrorResponse getError() {
            return this.error;
        }
    }

}
