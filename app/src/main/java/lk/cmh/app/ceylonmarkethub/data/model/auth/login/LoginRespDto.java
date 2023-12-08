package lk.cmh.app.ceylonmarkethub.data.model.auth.login;

import java.util.Date;

public class LoginRespDto {
    private String email;
    private String accessToken;
    private String refreshToken;
    private String firebaseToken;
    private Date expiresIn;

    private String role;

    public LoginRespDto() {
    }

    public LoginRespDto(String email, String accessToken, String refreshToken, String firebaseToken, Date expiresIn, String role) {
        this.email = email;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.firebaseToken = firebaseToken;
        this.expiresIn = expiresIn;
        this.role = role;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getFirebaseToken() {
        return firebaseToken;
    }

    public void setFirebaseToken(String firebaseToken) {
        this.firebaseToken = firebaseToken;
    }

    public Date getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Date expiresIn) {
        this.expiresIn = expiresIn;
    }


    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "LoginRespDto{" +
                "email='" + email + '\'' +
                ", accessToken='" + accessToken + '\'' +
                ", refreshToken='" + refreshToken + '\'' +
                ", firebaseToken='" + firebaseToken + '\'' +
                ", expiresIn=" + expiresIn +
                ", role='" + role + '\'' +
                '}';
    }
}
