package kg.benext.api.services;

import io.qameta.allure.Step;
import kg.benext.api.HttpRequest;
import kg.benext.api.model.response.CategoryResponse;
import kg.benext.common.constants.Endpoints;
import java.util.Arrays;
import java.util.List;

public class CategoryService extends HttpRequest {

    public CategoryService(String url) {
        super(url);
    }

    @Step("Get all categories")
    public List<CategoryResponse> getCategories() {
        return Arrays.asList(
                super.get(Endpoints.CATEGORIES)
                        .as(CategoryResponse[].class)
        );
    }
}
