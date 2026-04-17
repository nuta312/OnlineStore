package api;

import kg.benext.api.model.request.ReviewRequest;
import kg.benext.api.model.response.CreateReviewResponse;
import kg.benext.api.model.response.ReviewListResponse;
import kg.benext.api.model.response.SuccessResponse;
import kg.benext.api.services.AuthService;
import kg.benext.api.services.ProductService;
import kg.benext.common.utils.TestDataGenerator;
import kg.benext.common.utils.file.ConfigurationManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.*;

public class ReviewTest extends BaseAPI {

    ProductService productService = new ProductService(ConfigurationManager.getBaseConfig().baseUrl());
    // Существующий продукт
    String productId = "ffc91150-2f5f-474e-bcd5-c84d0842bb46";

    @BeforeEach
    void setUp() {
        String token = new AuthService().getToken("amanturov2471@gmail.com", "naryn25");
        productService.withToken(token);
    }

    @Test
    void getProductReviewsTest() {
        ReviewListResponse response = productService.getProductReviews(productId);

        step("Статус 200", () ->
                assertEquals(200, productService.getResponse().getStatusCode())
        );
        step("Список отзывов не null", () ->
                assertNotNull(response.getReviews())
        );
    }

    @Test
    void createAndDeleteReviewTest() {
        // Создаём отзыв
        ReviewRequest request = ReviewRequest.builder()
                .userId(TestDataGenerator.randomString(8))
                .userName(TestDataGenerator.randomString(6))
                .rating(new java.util.Random().nextInt(1, 6)) // от 1 до 5
                .comment(TestDataGenerator.randomString(20))
                .build();

        CreateReviewResponse created = productService.createReview(productId, request);

        step("Создание: статус 201", () ->
                assertEquals(201, productService.getResponse().getStatusCode())
        );
        step("ID отзыва не null", () ->
                assertNotNull(created.getId())
        );

        // Удаляем отзыв
        SuccessResponse deleted = productService.deleteReview(created.getId().toString());

        step("Удаление: статус 200", () ->
                assertEquals(200, productService.getResponse().getStatusCode())
        );
        step("isSuccess = true", () ->
                assertTrue(deleted.getIsSuccess())
        );
    }
}