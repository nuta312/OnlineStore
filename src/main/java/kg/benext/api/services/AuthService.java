package kg.benext.api.services;

import java.net.URI;
import java.net.http.HttpClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.qameta.allure.Step;
import kg.benext.api.model.request.AuthRequest;

import java.net.http.HttpResponse;

public class AuthService {

    private static final String FIREBASE_URL = "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword";
    private static final String API_KEY = "AIzaSyBPP_MzdxmjW_5Gt-xJqkPl1sJMTo-znCo";

    @Step("Get customer token")
    public static String getToken(String email, String password) {
        try {
            String url = "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=" + API_KEY;
            String body = AuthRequest.builder()
                    .email(email)
                    .password(password)
                    .returnSecureToken(true)
                    .build()
                    .toJson();

            HttpClient client = HttpClient.newHttpClient();
            java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(java.net.http.HttpRequest.BodyPublishers.ofString(body))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            ObjectMapper mapper = new ObjectMapper();
            return mapper.readTree(response.body()).get("idToken").asText();

        } catch (Exception e) {
            throw new RuntimeException("Auth failed", e);
        }
    }

    @Step("Get customer id")
    public static String getCustomerId(String email, String password) {
        try {
            String url = "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=" + API_KEY;
            String body = AuthRequest.builder()
                    .email(email)
                    .password(password)
                    .returnSecureToken(true)
                    .build()
                    .toJson();

            HttpClient client = HttpClient.newHttpClient();
            java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(java.net.http.HttpRequest.BodyPublishers.ofString(body))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            ObjectMapper mapper = new ObjectMapper();
            // localId — это ID пользователя в Firebase (он же customerId)
            return mapper.readTree(response.body()).get("localId").asText();

        } catch (Exception e) {
            throw new RuntimeException("Auth failed", e);
        }
    }
}