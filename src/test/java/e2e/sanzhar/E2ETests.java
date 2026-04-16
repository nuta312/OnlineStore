package e2e.sanzhar;

import com.github.javafaker.Faker;
import kg.benext.api.model.request.BasketRequest;
import kg.benext.api.model.request.FavoriteRequest;
import kg.benext.api.model.request.ProductRequest;
import kg.benext.api.model.response.*;
import kg.benext.api.services.AuthService;
import kg.benext.api.services.BasketService;
import kg.benext.api.services.FavoriteService;
import kg.benext.api.services.ProductService;
import kg.benext.common.utils.TestDataGenerator;
import kg.benext.common.utils.file.ConfigurationManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.*;

public class E2ETests {
    String baseUrl = ConfigurationManager.getBaseConfig().baseUrl();
    ProductService productService = new ProductService(ConfigurationManager.getBaseConfig().baseUrl());
    FavoriteService favoriteService = new FavoriteService(baseUrl);
    BasketService basketService = new BasketService(ConfigurationManager.getBaseConfig().baseUrl());
    Faker faker = new Faker();
    String token;
    String randomValidIdProduct = faker.options().option("ffc91150-2f5f-474e-bcd5-c84d0842bb46",
            "ffa9f951-28ac-4989-8e40-3c82289e9047", "fea670f7-b5af-4bd0-897a-d67984f97ddc");

    @BeforeEach
    void setUp() {
        token = AuthService.getToken("amanturov2471@gmail.com", "naryn25");
    }

    @Test
    void creatProductWithoutAvtorization(){
        // баг
        ProductRequest productRequest = TestDataGenerator.randomProductRequest();
        CreateProductResponse createProduct = productService.createProduct(productRequest);
        assertEquals(401, productService.getResponse().getStatusCode(),
                "Создание продукта без авторизации ожидаемый код 401");
    }

    @Test
    void addProductFavorites(){
        favoriteService.withToken(token);
        UUID productId = UUID.fromString(randomValidIdProduct);
        SuccessResponse addResponse = favoriteService.addToFavorites(
                FavoriteRequest.builder().productId(productId).build()
        );

        step("Статус 200", () ->
                assertEquals(200, favoriteService.getResponse().getStatusCode())
        );
        step("isSuccess не null", () ->
                assertNotNull(addResponse.getIsSuccess())
        );
        step("isSuccess = true", () ->
                assertTrue(addResponse.getIsSuccess())
        );
        // Проверяем что товар действительно появился в избранном
        step("Товар появился в избранном", () -> {
            FavoriteListResponse favorites = favoriteService.getFavorites();
            boolean found = favorites.getFavorites().stream()
                    .anyMatch(f -> f.getProductId().equals(productId));
            assertTrue(found, "Продукт должен быть в списке избранного");
        });
    }

    @Test
    void getReviews(){
        productService.withToken(token);
        ReviewListResponse response = productService.getProductReviews(randomValidIdProduct);

        step("Статус 200", () ->
                assertEquals(200, productService.getResponse().getStatusCode())
        );
        step("Список отзывов не null", () ->
                assertNotNull(response.getReviews())
        );
        step("Пагинация не null", () ->
                assertNotNull(response.getPagination())
        );
        step("pageNumber не null", () ->
                assertNotNull(response.getPagination().getPageNumber())
        );
        step("pageSize не null", () ->
                assertNotNull(response.getPagination().getPageSize())
        );
        step("totalCount не null", () ->
                assertNotNull(response.getPagination().getTotalCount())
        );

        // Проверяем каждый отзыв если список не пустой
        step("Проверяем поля каждого отзыва", () -> {
            if (!response.getReviews().isEmpty()) {
                for (ReviewListResponse.ReviewItem review : response.getReviews()) {
                    assertNotNull(review.getId(),
                            "ID отзыва не должен быть null");
                    assertNotNull(review.getProductId(),
                            "productId не должен быть null");
                    assertEquals(UUID.fromString(randomValidIdProduct), review.getProductId(),
                            "productId должен совпадать с запрошенным");
                    assertNotNull(review.getUserName(),
                            "userName не должен быть null");
                    assertNotNull(review.getRating(),
                            "rating не должен быть null");
                    assertTrue(review.getRating() >= 1 && review.getRating() <= 5,
                            "rating должен быть от 1 до 5");
                    assertNotNull(review.getComment(),
                            "comment не должен быть null");
                    assertNotNull(review.getCreatedAt(),
                            "createdAt не должен быть null");
                }
            }
        });
        }

    @Test
    void addBasket() {
        basketService.withToken(token);
        productService.withToken(token);

        // Получаем существующий продукт
        ProductResponse product = productService.getProductById(randomValidIdProduct);
        int quantity = new Random().nextInt(1, 5);
        String color = TestDataGenerator.randomString(5);

        // Добавляем продукт в корзину
        BasketRequest basketRequest = BasketRequest.builder()
                .items(List.of(
                        BasketRequest.BasketItem.builder()
                                .productId(product.getId())
                                .productName(product.getName())
                                .quantity(quantity)
                                .price(product.getPrice().doubleValue())
                                .basePrice(product.getPrice().doubleValue())
                                .color(color)
                                .imageFile(product.getImageFile())
                                .build()
                ))
                .build();

        StoreBasketResponse storeResponse = basketService.storeBasket(basketRequest);

        step("Сохранение: статус 201", () ->
                assertEquals(201, basketService.getResponse().getStatusCode())
        );
        step("userName не null", () ->
                assertNotNull(storeResponse.getUserName())
        );

        // Получаем корзину и проверяем что данные совпадают
        BasketResponse basketResponse = basketService.getBasket();

        step("Получение корзины: статус 200", () ->
                assertEquals(200, basketService.getResponse().getStatusCode())
        );
        step("Список товаров не null", () ->
                assertNotNull(basketResponse.getItems())
        );
        step("Список товаров не пустой", () ->
                assertFalse(basketResponse.getItems().isEmpty())
        );

        // Находим наш товар в корзине
        step("Проверяем данные товара в корзине", () -> {
            BasketResponse.BasketItem item = basketResponse.getItems().stream()
                    .filter(i -> i.getProductId().equals(product.getId()))
                    .findFirst()
                    .orElseThrow(() -> new AssertionError("Товар не найден в корзине"));

            assertEquals(product.getId(), item.getProductId(),
                    "productId должен совпадать");
            assertEquals(product.getName(), item.getProductName(),
                    "productName должен совпадать");
            assertEquals(quantity, item.getQuantity(),
                    "quantity должен совпадать");
            assertEquals(product.getPrice().doubleValue(), item.getPrice(),
                    "price должен совпадать");
            assertEquals(color, item.getColor(),
                    "color должен совпадать");
            assertEquals(product.getImageFile(), item.getImageFile(),
                    "imageFile должен совпадать");
        });
    }
}
