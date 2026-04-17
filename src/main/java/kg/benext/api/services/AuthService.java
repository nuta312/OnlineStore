package kg.benext.api.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.qameta.allure.Step;
import kg.benext.api.HttpRequest;
import kg.benext.api.model.request.AuthRequest;
import kg.benext.common.utils.file.ConfigurationManager;

import java.net.URI;

public class AuthService extends kg.benext.api.HttpRequest {

    public AuthService() {
        super(ConfigurationManager.getBaseConfig().firebaseUrl());
    }

    @Step("Get customer token")
    public String getToken(String email, String password) {
        return getField(email, password, "idToken");
    }

    @Step("Get customer id")
    public String getCustomerId(String email, String password) {
        return getField(email, password, "localId");
    }

    private String getField(String email, String password, String field) {
        try {
            String url = ConfigurationManager.getBaseConfig().firebaseUrl()
                    + "?key="
                    + ConfigurationManager.getBaseConfig().firebaseApiKey();

            String body = AuthRequest.builder()
                    .email(email)
                    .password(password)
                    .returnSecureToken(true)
                    .build()
                    .toJson();

            java.net.http.HttpClient client = java.net.http.HttpClient.newHttpClient();
            java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(java.net.http.HttpRequest.BodyPublishers.ofString(body))
                    .build();

            java.net.http.HttpResponse<String> response = client.send(
                    request, java.net.http.HttpResponse.BodyHandlers.ofString()
            );

            return new ObjectMapper()
                    .readTree(response.body())
                    .get(field)
                    .asText();

        } catch (Exception e) {
            throw new RuntimeException("Auth failed", e);
        }
    }
}