package kg.benext.api.services;

import io.qameta.allure.Step;
import kg.benext.api.HttpRequest;
import kg.benext.api.model.request.DiscountRequest;
import kg.benext.api.model.response.DiscountDeleteResponse;
import kg.benext.api.model.response.DiscountResponse;
import kg.benext.common.constants.Endpoints;

public class DiscountService extends HttpRequest {

    public DiscountService(String url) {
        super(url);
    }

    @Step("Get discount by product name {productName}")
    public DiscountResponse getDiscountByProductName(String productName) {
        return super.get(String.format(Endpoints.DISCOUNT_BY_NAME, productName))
                .as(DiscountResponse.class);
    }

    @Step("Create discount")
    public DiscountResponse createDiscount(DiscountRequest request) {
        return super.post(Endpoints.DISCOUNT, request.toJson())
                .as(DiscountResponse.class);
    }

    @Step("Update discount")
    public DiscountResponse updateDiscount(DiscountRequest request) {
        return super.put(Endpoints.DISCOUNT, request.toJson())
                .as(DiscountResponse.class);
    }

    @Step("Delete discount by product name {productName}")
    public DiscountDeleteResponse deleteDiscount(String productName) {
        return super.delete(String.format(Endpoints.DISCOUNT_BY_NAME, productName))
                .as(DiscountDeleteResponse.class);
    }
}