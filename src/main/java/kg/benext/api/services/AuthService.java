package kg.benext.api.services;


import io.qameta.allure.Step;
import kg.benext.api.HttpRequest;
import kg.benext.api.model.request.AuthRequest;
import kg.benext.common.utils.file.ConfigurationManager;



public class AuthService extends HttpRequest {

    public AuthService() {
        super(ConfigurationManager.getBaseConfig().firebaseUrl());
    }

    @Step("Get customer token")
    public String getToken(String email, String password) {
        String body = AuthRequest.builder()
                .email(email)
                .password(password)
                .returnSecureToken(true)
                .build()
                .toJson();

        return super.post("?key=" + ConfigurationManager.getBaseConfig().firebaseApiKey(), body)
                .jsonPath().getString("idToken");
    }

    @Step("Get customer id")
    public String getCustomerId(String email, String password) {
        String body = AuthRequest.builder()
                .email(email)
                .password(password)
                .returnSecureToken(true)
                .build()
                .toJson();

        return super.post("?key=" + ConfigurationManager.getBaseConfig().firebaseApiKey(), body)
                .jsonPath().getString("localId");
    }
}