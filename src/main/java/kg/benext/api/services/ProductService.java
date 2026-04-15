package kg.benext.api.services;

import io.qameta.allure.Step;
import kg.benext.api.HttpRequest;
import kg.benext.api.model.request.ProductRequest;
import kg.benext.api.model.request.UpdateProductRequest;
import kg.benext.api.model.response.*;
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

    @Step("Update product")
    public SuccessResponse updateProduct(UpdateProductRequest request) {
        return super.put(Endpoints.PRODUCTS_UPDATE, request.toJson())
                .as(SuccessResponse.class);
    }

    @Step("Delete product {id}")
    public void deleteProduct(String id) {
        super.delete(String.format(Endpoints.PRODUCTS, id));
    }

    @Step("Get products by category {categoryId}")
    public ProductListResponse getProductsByCategory(String categoryId) {
        return super.get(String.format(Endpoints.PRODUCTS_BY_CATEGORY, categoryId))
                .as(ProductListResponse.class);
    }

    @Step("Delete review {id}")
    public void deleteReview(String id) {
        super.delete(String.format(Endpoints.REVIEWS, id));
    }
}
