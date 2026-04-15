package kg.benext.api.services;

import io.qameta.allure.Step;
import kg.benext.api.HttpRequest;
import kg.benext.api.model.request.BasketRequest;
import kg.benext.api.model.request.CheckoutRequest;
import kg.benext.api.model.response.*;
import kg.benext.common.constants.Endpoints;

import java.util.Arrays;
import java.util.List;

public class BasketService extends HttpRequest {

    public BasketService(String url) {
        super(url);
    }

    @Step("Get basket")
    public BasketResponse getBasket() {
        return super.get(Endpoints.BASKET)
                .as(BasketResponse.class);
    }

    @Step("Store basket")
    public StoreBasketResponse storeBasket(BasketRequest request) {
        return super.post(Endpoints.BASKET, request.toJson())
                .as(StoreBasketResponse.class);
    }

    @Step("Delete basket")
    public SuccessResponse deleteBasket() {
        return super.delete(Endpoints.BASKET)
                .as(SuccessResponse.class);
    }

    @Step("Checkout basket")
    public SuccessResponse checkout(CheckoutRequest request) {
        return super.post(Endpoints.BASKET_CHECKOUT, request.toJson())
                .as(SuccessResponse.class);
    }

    @Step("Get delivery methods")
    public List<DeliveryMethodResponse> getDeliveryMethods() {
        return Arrays.asList(
                super.get(Endpoints.BASKET_DELIVERY_METHODS)
                        .as(DeliveryMethodResponse[].class)
        );
    }
}