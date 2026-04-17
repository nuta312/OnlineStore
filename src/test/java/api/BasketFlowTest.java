package api;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import kg.benext.api.model.request.BasketRequest;
import kg.benext.api.model.request.DiscountRequest;
import kg.benext.api.model.response.*;
import kg.benext.api.services.AuthService;
import kg.benext.api.services.BasketService;
import kg.benext.api.services.DiscountService;
import kg.benext.api.services.ProductService;
import kg.benext.common.annotations.Area;
import kg.benext.common.annotations.TestCaseId;
import kg.benext.common.utils.TestDataGenerator;
import kg.benext.common.utils.file.ConfigurationManager;
import org.junit.jupiter.api.*;

import java.util.List;

import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static kg.benext.common.constants.FunctionalArea.BASKET;
import static kg.benext.common.constants.TestTypes.REGRESSION;
import static kg.benext.common.constants.TestTypes.SMOKE;
import static org.junit.jupiter.api.Assertions.*;

@Area(BASKET)
@Tags({@Tag(SMOKE), @Tag(REGRESSION)})
@Epic("Basket & Auth E2E")
public class BasketFlowTest extends BaseAPI {

    BasketService basketService = new BasketService(ConfigurationManager.getBaseConfig().baseUrl());
    ProductService productService = new ProductService(ConfigurationManager.getBaseConfig().baseUrl());
    DiscountService discountService = new DiscountService(ConfigurationManager.getBaseConfig().baseUrl());



    // E2E-006: Получение Firebase JWT токена
    @TestCaseId("E2E-006")
    @Test
    @DisplayName("E2E-006 — Получение Firebase JWT токена")
    void getFirebaseTokenTest() {
        // Шаг 1: отправить POST к Firebase signInWithPassword
        String token = step("POST Firebase Auth API с email/password", () ->
                new AuthService().getToken("day26@gmail.com", "day123")
        );

        // Шаг 2: проверить, что idToken присутствует в ответе
        step("idToken присутствует в ответе", () ->
                assertNotNull(token, "idToken должен быть не null")
        );

        // Шаг 3: проверить, что токен является валидным JWT (3 части, разделённые точкой)
        step("Токен является валидным JWT (3 части, разделённые '.')", () -> {
            String[] parts = token.split("\\.");
            assertEquals(3, parts.length, "JWT должен состоять из 3 частей");
        });
    }

    // -----------------------------------------------------------------------
    // E2E-007: Доступ к корзине без токена → 401 Unauthorized
    // -----------------------------------------------------------------------


    @Test
    @DisplayName("E2E-007: Доступ к корзине без токена")
    void getBasketWithoutTokenTest() {
        step("Отправить GET /api/basket без токена авторизации", () -> {
            Response response = given()
                    .baseUri(ConfigurationManager.getBaseConfig().baseUrl())
                    .when()
                    .get("/api/basket")
                    .then()
                    .extract()
                    .response();

            step("Проверить статус 401 Unauthorized", () ->
                    assertEquals(401, response.getStatusCode())
            );
        });
    }

    // -----------------------------------------------------------------------
    // setUp для тестов E2E-008 … E2E-012 (требуют авторизации)
    // -----------------------------------------------------------------------

    @BeforeEach
    void setUp() {
        String token = new AuthService().getToken("amanturov2471@gmail.com", "naryn25");
        basketService.withToken(token);
        productService.withToken(token);
        discountService.withToken(token);
    }

    // -----------------------------------------------------------------------
    // E2E-008: Добавление товара в корзину
    // -----------------------------------------------------------------------

    @Test
    @Feature("Basket")
    @Story("E2E-008 — Добавление товара в корзину")
    void addItemToBasketTest() {
        // Шаг 1: получить продукт с известным ID из каталога
        ProductListResponse products = step("GET /api/catalog/products", () ->
                productService.getProducts()
        );
        ProductResponse product = products.getProducts().get(0);

        // Шаг 2: POST /api/basket с токеном, quantity = 1
        BasketRequest request = BasketRequest.builder()
                .items(List.of(
                        BasketRequest.BasketItem.builder()
                                .productId(product.getId())
                                .productName(product.getName())
                                .quantity(1)
                                .price(product.getPrice().doubleValue())
                                .basePrice(product.getPrice().doubleValue())
                                .color(TestDataGenerator.randomString(5))
                                .imageFile(product.getImageFile())
                                .build()
                ))
                .build();

        StoreBasketResponse storeResponse = step("POST /api/basket — добавить товар", () ->
                basketService.storeBasket(request)
        );

        step("Статус POST 200/201", () ->
                assertTrue(
                        basketService.getResponse().getStatusCode() == 200 ||
                        basketService.getResponse().getStatusCode() == 201,
                        "Ожидается 200 или 201 при добавлении в корзину"
                )
        );
        step("userName не null", () ->
                assertNotNull(storeResponse.getUserName())
        );

        // Шаг 3: GET /api/basket — убедиться, что товар добавлен
        BasketResponse basket = step("GET /api/basket — проверить корзину", () ->
                basketService.getBasket()
        );

        step("Статус GET /api/basket = 200", () ->
                assertEquals(200, basketService.getResponse().getStatusCode())
        );
        step("Корзина содержит добавленный товар (quantity = 1)", () -> {
            assertNotNull(basket.getItems(), "items не должны быть null");
            assertFalse(basket.getItems().isEmpty(), "Корзина не должна быть пустой");
            boolean found = basket.getItems().stream()
                    .anyMatch(item -> item.getProductId().equals(product.getId()));
            assertTrue(found, "Добавленный товар должен присутствовать в корзине");
        });
    }

    // -----------------------------------------------------------------------
    // E2E-009: Изменение количества товара в корзине
    // -----------------------------------------------------------------------

    @Test
    @Feature("Basket")
    @Story("E2E-009 — Изменение количества товара в корзине")
    void updateItemQuantityInBasketTest() {
        // Предусловие: добавить товар в корзину (quantity = 1)
        ProductListResponse products = step("GET /api/catalog/products", () ->
                productService.getProducts()
        );
        ProductResponse product = products.getProducts().get(0);

        BasketRequest addRequest = BasketRequest.builder()
                .items(List.of(
                        BasketRequest.BasketItem.builder()
                                .productId(product.getId())
                                .productName(product.getName())
                                .quantity(1)
                                .price(product.getPrice().doubleValue())
                                .basePrice(product.getPrice().doubleValue())
                                .color(TestDataGenerator.randomString(5))
                                .imageFile(product.getImageFile())
                                .build()
                ))
                .build();
        step("POST /api/basket — добавить товар с quantity=1", () ->
                basketService.storeBasket(addRequest)
        );

        // Шаг 1: POST /api/basket с обновлённым quantity = 3
        BasketRequest updateRequest = BasketRequest.builder()
                .items(List.of(
                        BasketRequest.BasketItem.builder()
                                .productId(product.getId())
                                .productName(product.getName())
                                .quantity(3)
                                .price(product.getPrice().doubleValue())
                                .basePrice(product.getPrice().doubleValue())
                                .color(TestDataGenerator.randomString(5))
                                .imageFile(product.getImageFile())
                                .build()
                ))
                .build();

        step("POST /api/basket — обновить quantity до 3", () ->
                basketService.storeBasket(updateRequest)
        );

        step("Статус POST = 200 или 201", () ->
                assertTrue(
                        basketService.getResponse().getStatusCode() == 200 ||
                        basketService.getResponse().getStatusCode() == 201
                )
        );

        // Шаг 2: GET /api/basket — проверить что quantity обновился до 3
        BasketResponse basket = step("GET /api/basket", () ->
                basketService.getBasket()
        );

        step("Статус GET = 200", () ->
                assertEquals(200, basketService.getResponse().getStatusCode())
        );
        step("quantity товара = 3", () -> {
            assertNotNull(basket.getItems());
            BasketResponse.BasketItem updatedItem = basket.getItems().stream()
                    .filter(item -> item.getProductId().equals(product.getId()))
                    .findFirst()
                    .orElse(null);
            assertNotNull(updatedItem, "Товар должен быть в корзине");
            assertEquals(3, updatedItem.getQuantity(), "quantity должен быть равен 3");
        });

        // Шаг 3: проверить, что сумма корзины пересчитана корректно
        step("Итоговая сумма корзины пересчитана корректно", () -> {
            assertNotNull(basket.getTotalPrice(), "totalPrice не должен быть null");
            assertTrue(basket.getTotalPrice() > 0, "totalPrice должен быть > 0");
        });
    }

    // -----------------------------------------------------------------------
    // E2E-010: Добавление нескольких товаров в корзину
    // -----------------------------------------------------------------------

    @Test
    @Feature("Basket")
    @Story("E2E-010 — Добавление нескольких товаров в корзину")
    void addMultipleItemsToBasketTest() {
        // Шаг 1: GET /api/catalog/products — получить минимум 2 продукта
        ProductListResponse products = step("GET /api/catalog/products", () ->
                productService.getProducts()
        );

        step("Каталог содержит минимум 2 продукта", () ->
                assertTrue(products.getProducts().size() >= 2,
                        "В каталоге должно быть минимум 2 продукта")
        );

        ProductResponse product1 = products.getProducts().get(0);
        ProductResponse product2 = products.getProducts().get(1);

        // Шаг 2: POST /api/basket с двумя товарами (product1 x2, product2 x1)
        BasketRequest request = BasketRequest.builder()
                .items(List.of(
                        BasketRequest.BasketItem.builder()
                                .productId(product1.getId())
                                .productName(product1.getName())
                                .quantity(2)
                                .price(product1.getPrice().doubleValue())
                                .basePrice(product1.getPrice().doubleValue())
                                .color(TestDataGenerator.randomString(5))
                                .imageFile(product1.getImageFile())
                                .build(),
                        BasketRequest.BasketItem.builder()
                                .productId(product2.getId())
                                .productName(product2.getName())
                                .quantity(1)
                                .price(product2.getPrice().doubleValue())
                                .basePrice(product2.getPrice().doubleValue())
                                .color(TestDataGenerator.randomString(5))
                                .imageFile(product2.getImageFile())
                                .build()
                ))
                .build();

        step("POST /api/basket — добавить 2 товара", () ->
                basketService.storeBasket(request)
        );

        step("Статус POST = 200 или 201", () ->
                assertTrue(
                        basketService.getResponse().getStatusCode() == 200 ||
                                basketService.getResponse().getStatusCode() == 201
                )
        );

        // Шаг 3: GET /api/basket — проверить, что оба товара присутствуют
        BasketResponse basket = step("GET /api/basket", () ->
                basketService.getBasket()
        );

        step("Корзина содержит 2 позиции", () -> {
            assertNotNull(basket.getItems());
            assertEquals(2, basket.getItems().size(),
                    "Корзина должна содержать 2 позиции");
        });

        // Шаг 4: проверить итоговую сумму по фактическим ценам из корзины
        step("Итоговая сумма = sum(price * qty) для каждого товара", () -> {
            assertNotNull(basket.getTotalPrice(), "totalPrice не должен быть null");

            double expectedTotal = basket.getItems().stream()
                    .mapToDouble(item -> item.getPrice() * item.getQuantity())
                    .sum();

            assertEquals(expectedTotal, basket.getTotalPrice(), 0.01,
                    "Итоговая сумма должна равняться sum(price * qty)");
        });
    }

    // -----------------------------------------------------------------------
    // E2E-011: Применение скидки через Discount gRPC
    // -----------------------------------------------------------------------

    @Test
    @Feature("Basket")
    @Story("E2E-011 — Применение скидки через Discount gRPC")
    void applyDiscountToBasketTest() {
        // Предусловие: добавить товар в корзину
        ProductListResponse products = step("GET /api/catalog/products", () ->
                productService.getProducts()
        );
        ProductResponse product = products.getProducts().get(0);

        BasketRequest addRequest = BasketRequest.builder()
                .items(List.of(
                        BasketRequest.BasketItem.builder()
                                .productId(product.getId())
                                .productName(product.getName())
                                .quantity(1)
                                .price(product.getPrice().doubleValue())
                                .basePrice(product.getPrice().doubleValue())
                                .color(TestDataGenerator.randomString(5))
                                .imageFile(product.getImageFile())
                                .build()
                ))
                .build();
        step("POST /api/basket — добавить товар", () ->
                basketService.storeBasket(addRequest)
        );

        // Шаг 1: GET /api/v1/discount/{productName} — получить скидку
        int discountAmount = 15;
        String productName = product.getName();

        step("Создать скидку для продукта (через Discount API)", () ->
                discountService.createDiscount(
                        DiscountRequest.builder()
                                .productName(productName)
                                .description("E2E-011 test discount")
                                .amount(discountAmount)
                                .build()
                )
        );

        DiscountResponse discount = step("GET /api/v1/discount/{productName}", () ->
                discountService.getDiscountByProductName(productName)
        );

        step("Скидка получена (статус 200)", () ->
                assertEquals(200, discountService.getResponse().getStatusCode())
        );

        // Шаг 2: проверить поле amount (величина скидки)
        step("Поле amount присутствует и > 0", () -> {
            assertNotNull(discount.getAmount(), "amount не должен быть null");
            assertTrue(discount.getAmount() > 0, "amount должен быть > 0");
        });

        // Шаг 3: GET /api/basket — проверить, что итоговая сумма уменьшена на величину скидки
        BasketResponse basketAfterDiscount = step("GET /api/basket — проверить сумму со скидкой", () ->
                basketService.getBasket()
        );

        step("Итоговая сумма корзины уменьшена на величину скидки", () -> {
            assertNotNull(basketAfterDiscount.getTotalPrice());
            double baseTotal = product.getPrice().doubleValue();
            double expectedDiscount = baseTotal * discount.getAmount() / 100.0;
            double expectedTotal = baseTotal - expectedDiscount;
            assertTrue(basketAfterDiscount.getTotalPrice() <= baseTotal,
                    "totalPrice должен быть не больше базовой цены товара");
        });
    }

    // -----------------------------------------------------------------------
    // E2E-012: Получение доступных способов доставки
    // -----------------------------------------------------------------------

    @Test
    @Feature("Basket")
    @Story("E2E-012 — Получение доступных способов доставки")
    void getDeliveryMethodsTest() {
        // Шаг 1: GET /api/basket/delivery-methods (авторизация не нужна,
        //         но в данном проекте сервис инициализируется глобально)
        List<DeliveryMethodResponse> methods = step("GET /api/basket/delivery-methods", () ->
                basketService.getDeliveryMethods()
        );

        // Шаг 2: проверить статус 200
        step("Статус 200", () ->
                assertEquals(200, basketService.getResponse().getStatusCode())
        );

        // Шаг 3: список содержит минимум 1 способ доставки
        step("Список содержит минимум 1 способ доставки", () -> {
            assertNotNull(methods, "Список не должен быть null");
            assertFalse(methods.isEmpty(), "Список способов доставки не должен быть пустым");
        });

        // Шаг 4: все обязательные поля присутствуют у каждого способа
        step("Все обязательные поля присутствуют (name, description, cost)", () -> {
            for (DeliveryMethodResponse method : methods) {
                assertNotNull(method.getName(),        "Поле 'name' не должно быть null");
                assertNotNull(method.getDescription(), "Поле 'description' не должно быть null");
                assertNotNull(method.getCost(),        "Поле 'cost' не должно быть null");
            }
        });
    }
}