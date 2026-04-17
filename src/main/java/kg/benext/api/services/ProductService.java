package kg.benext.api.services;

import io.qameta.allure.Step;
import kg.benext.api.HttpRequest;
import kg.benext.api.model.request.ProductRequest;
import kg.benext.api.model.request.UpdateProductRequest;
import kg.benext.api.model.response.*;
import kg.benext.common.constants.Endpoints;
import kg.benext.api.model.request.ReviewRequest;
import kg.benext.api.model.response.CreateReviewResponse;
import kg.benext.api.model.response.ReviewListResponse;

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

    @Step("Get product by id raw {id}")
    public void getProductByIdRaw(String id) {
        super.get(String.format(Endpoints.PRODUCTS, id));
        // Не десериализуем — просто делаем запрос
    }

    @Step("Get products")
    public ProductListResponse getProducts() {
        return super.get(Endpoints.PRODUCTS_LIST)
                .as(ProductListResponse.class);
    }

    @Step("Get reviews by product id {productId}")
    public void getReviews(String productId) {
        super.get(String.format(Endpoints.REVIEWS, productId));
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

    @Step("Delete review {reviewId}")
    public SuccessResponse deleteReview(String reviewId) {
        return super.delete(String.format(Endpoints.REVIEWS, reviewId))
                .as(SuccessResponse.class);
    }

    @Step("Get reviews for product {productId}")
    public ReviewListResponse getProductReviews(String productId) {
        return super.get(String.format(Endpoints.PRODUCT_REVIEWS, productId))
                .as(ReviewListResponse.class);
    }

    @Step("Create review for product {productId}")
    public CreateReviewResponse createReview(String productId, ReviewRequest request) {
        return super.post(String.format(Endpoints.PRODUCT_REVIEWS, productId), request.toJson())
                .as(CreateReviewResponse.class);
    }
}
