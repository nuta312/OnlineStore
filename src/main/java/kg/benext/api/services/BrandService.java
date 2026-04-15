package kg.benext.api.services;

import io.qameta.allure.Step;
import kg.benext.api.HttpRequest;
import kg.benext.api.model.response.BrandListResponse;
import kg.benext.common.constants.Endpoints;

public class BrandService extends HttpRequest {

    public BrandService(String url) {
        super(url);
    }

    @Step("Get all brands")
    public BrandListResponse getBrands() {
        return super.get(Endpoints.BRANDS)
                .as(BrandListResponse.class);
    }
}