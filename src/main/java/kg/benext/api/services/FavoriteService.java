package kg.benext.api.services;

import io.qameta.allure.Step;
import kg.benext.api.HttpRequest;
import kg.benext.api.model.request.FavoriteRequest;
import kg.benext.api.model.response.FavoriteListResponse;
import kg.benext.api.model.response.SuccessResponse;
import kg.benext.common.constants.Endpoints;

public class FavoriteService extends HttpRequest {

    public FavoriteService(String url) {
        super(url);
    }

    @Step("Get favorites")
    public FavoriteListResponse getFavorites() {
        return super.get(Endpoints.FAVORITES)
                .as(FavoriteListResponse.class);
    }

    @Step("Add to favorites")
    public SuccessResponse addToFavorites(FavoriteRequest request) {
        return super.post(Endpoints.FAVORITES, request.toJson())
                .as(SuccessResponse.class);
    }

    @Step("Remove from favorites {productId}")
    public SuccessResponse removeFromFavorites(String productId) {
        return super.delete(String.format(Endpoints.FAVORITES_DELETE, productId))
                .as(SuccessResponse.class);
    }
}