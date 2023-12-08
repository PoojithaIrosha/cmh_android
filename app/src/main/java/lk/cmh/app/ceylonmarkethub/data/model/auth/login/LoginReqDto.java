package lk.cmh.app.ceylonmarkethub.data.model.auth.login;

public class LoginReqDto {

    private String email;
    private String password;

    public LoginReqDto() {
    }

    public LoginReqDto(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
