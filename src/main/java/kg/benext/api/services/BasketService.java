package kg.benext.api.services;

import io.qameta.allure.Step;
import kg.benext.api.HttpRequest;

public class BasketService extends HttpRequest{
    public BasketService(String url) {
        super(url);
    }
}
