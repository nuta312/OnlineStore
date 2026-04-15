package kg.benext.api.services;

import io.qameta.allure.Step;
import kg.benext.api.HttpRequest;
import kg.benext.api.model.request.ProductRequest;
import kg.benext.api.model.response.CreateProductResponse;
import kg.benext.api.model.response.ProductListResponse;
import kg.benext.api.model.response.ProductResponse;
import kg.benext.api.model.response.ProductWrapper;
import kg.benext.common.constants.Endpoints;

public class ProductService extends HttpRequest {

    public ProductService(String url) {
        super(url);
    }

    @Step("Get product by id {}")
    public ProductResponse getProductById(String id) {
        return super.get(String.format(Endpoints.PRODUCTS, id))
                .as(ProductWrapper.class)
                .getProduct();
    }

    @Step("Get products")
    public ProductListResponse getProducts() {
        return super.get(Endpoints.PRODUCTS_LIST)
                .as(ProductListResponse.class);
    }

    @Step("Create new product {}")
    public CreateProductResponse createProduct(ProductRequest request) {

        return super.post(Endpoints.PRODUCTS_LIST, request.toJson())
                .as(CreateProductResponse.class);

    }
}
