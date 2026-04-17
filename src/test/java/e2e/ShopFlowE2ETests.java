package e2e;

import com.github.javafaker.Faker;
import kg.benext.api.model.request.BasketRequest;
import kg.benext.api.model.request.CheckoutRequest;
import kg.benext.api.model.request.FavoriteRequest;
import kg.benext.api.model.request.ProductRequest;
import kg.benext.api.model.response.*;
import kg.benext.api.services.AuthService;
import kg.benext.api.services.BasketService;
import kg.benext.api.services.FavoriteService;
import kg.benext.api.services.ProductService;
import kg.benext.common.utils.TestDataGenerator;
import kg.benext.common.utils.file.ConfigurationManager;
import kg.benext.db.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.bson.Document;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.*;

public class ShopFlowE2ETests {
    String baseUrl = ConfigurationManager.getBaseConfig().baseUrl();
    ProductService productService = new ProductService(ConfigurationManager.getBaseConfig().baseUrl());
    FavoriteService favoriteService = new FavoriteService(baseUrl);
    BasketService basketService = new BasketService(ConfigurationManager.getBaseConfig().baseUrl());
    Faker faker = new Faker();
    String token;
    OrderRepository orderRepository = new OrderRepository();
    Random random = new Random();

    String customerId;
    String randomValidIdProduct = faker.options().option("ffc91150-2f5f-474e-bcd5-c84d0842bb46",
            "ffa9f951-28ac-4989-8e40-3c82289e9047", "fea670f7-b5af-4bd0-897a-d67984f97ddc");

    @BeforeEach
    void setUp() {
        customerId = new AuthService().getCustomerId("amanturov2471@gmail.com", "naryn25");
        token = new AuthService().getToken("amanturov2471@gmail.com", "naryn25");
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

        // Проверяем отзыв что список не пустой
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

    @Test
    void getBasketTest() {
        basketService.withToken(token);

        // Сначала добавляем товар чтобы корзина не была пустой
        ProductResponse product = productService.getProductById(randomValidIdProduct);
        int quantity = new Random().nextInt(1, 5);
        String color = TestDataGenerator.randomString(5);

        basketService.storeBasket(BasketRequest.builder()
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
                .build());

        // Получаем корзину
        BasketResponse response = basketService.getBasket();

        step("Статус 200", () ->
                assertEquals(200, basketService.getResponse().getStatusCode())
        );
        step("Items не null", () ->
                assertNotNull(response.getItems())
        );
        step("Корзина не пустая", () ->
                assertFalse(response.getItems().isEmpty())
        );
        step("totalPrice не null", () ->
                assertNotNull(response.getTotalPrice())
        );
        step("totalPrice больше 0", () ->
                assertTrue(response.getTotalPrice() > 0)
        );

        // Проверяем каждый товар в корзине
        step("Проверяем поля каждого товара", () -> {
            for (BasketResponse.BasketItem item : response.getItems()) {
                assertNotNull(item.getProductId(),
                        "productId не должен быть null");
                assertNotNull(item.getProductName(),
                        "productName не должен быть null");
                assertNotNull(item.getQuantity(),
                        "quantity не должен быть null");
                assertTrue(item.getQuantity() > 0,
                        "quantity должен быть больше 0");
                assertNotNull(item.getPrice(),
                        "price не должен быть null");
                assertTrue(item.getPrice() > 0,
                        "price должен быть больше 0");
                assertNotNull(item.getBasePrice(),
                        "basePrice не должен быть null");
                assertNotNull(item.getImageFile(),
                        "imageFile не должен быть null");
            }
        });

        // Проверяем наш конкретный товар
        step("Наш товар есть в корзине с правильными данными", () -> {
            BasketResponse.BasketItem item = response.getItems().stream()
                    .filter(i -> i.getProductId().equals(product.getId()))
                    .findFirst()
                    .orElseThrow(() -> new AssertionError("Наш товар не найден в корзине"));

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
        });
    }

    @Test
    void getDeliveryMethodsTest() {
        List<DeliveryMethodResponse> response = basketService.getDeliveryMethods();

        step("Статус 200", () ->
                assertEquals(200, basketService.getResponse().getStatusCode())
        );
        step("Список не пустой", () ->
                assertFalse(response.isEmpty())
        );
    }

    @Test
    void checkoutTest() {
        basketService.withToken(token);
        productService.withToken(token);

        // Подготовка: добавляем товар в корзину
        ProductResponse product = productService.getProductById(randomValidIdProduct);

        basketService.storeBasket(BasketRequest.builder()
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
                .build());

        // Проверяем что корзина не пустая перед checkout
        BasketResponse basketBefore = basketService.getBasket();
        step("Корзина не пустая перед checkout", () ->
                assertFalse(basketBefore.getItems().isEmpty(),
                        "Корзина должна содержать товары перед оформлением")
        );

        //  Оформляем заказ
        CheckoutRequest checkoutRequest = CheckoutRequest.builder()
                .basketCheckoutDto(CheckoutRequest.BasketCheckoutDto.builder()
                        .firstName(TestDataGenerator.randomString(6))
                        .lastName(TestDataGenerator.randomString(6))
                        .emailAddress(TestDataGenerator.randomEmail())
                        .addressLine("ул. " + TestDataGenerator.randomString(6))
                        .country("Kyrgyzstan")
                        .state("Бишкек")
                        .zipCode(TestDataGenerator.randomDigits(6))
                        .cardName(TestDataGenerator.randomString(8))
                        .cardNumber(TestDataGenerator.randomDigits(16))
                        .expiration("12/33")
                        .cvv(TestDataGenerator.randomDigits(3))
                        .paymentMethod(1)
                        .deliveryMethod("Pickup")
                        .deliveryCost(0.0)
                        .build())
                .build();

        SuccessResponse checkoutResponse = basketService.checkout(checkoutRequest);

        step("Статус 200", () ->
                assertEquals(200, basketService.getResponse().getStatusCode(),
                        "Checkout должен вернуть 200")
        );
        step("isSuccess = true", () ->
                assertTrue(checkoutResponse.getIsSuccess(),
                        "isSuccess должен быть true")
        );
        // Проверяем что заказ появился в MongoDB
        step("Заказ появился в базе данных", () -> {
            Document order = orderRepository.waitForLatestOrderByCustomerId(
                    customerId,
                    product.getId().toString()
            );

            assertNotNull(order,
                    "Заказ должен появиться в MongoDB");

            // Проверяем customerId
            assertEquals(customerId,
                    order.get("customerId", Document.class).getString("value"),
                    "customerId должен совпадать");

            // Проверяем что в заказе есть товары
            assertFalse(((List<?>) order.get("orderItems")).isEmpty(),
                    "Заказ должен содержать товары");

            // Проверяем что товар в заказе совпадает с тем что добавляли
            step("Товар в заказе совпадает с добавленным", () -> {
                List<Document> orderItems = (List<Document>) order.get("orderItems");

                // Смотрим что реально в orderItems
                System.out.println("Наш productId: " + product.getId());
                for (Document item : orderItems) {
                    System.out.println("orderItem: " + item.toJson());
                }

                boolean found = orderItems.stream()
                        .anyMatch(item -> {
                            Object productIdObj = item.get("productId", Document.class).get("value");
                            if (productIdObj instanceof org.bson.types.Binary binary) {
                                UUID uuid = uuidFromBinary(binary);
                                System.out.println("Binary UUID: " + uuid);
                                return uuid.equals(product.getId());
                            }
                            System.out.println("String value: " + productIdObj);
                            return productIdObj.toString().equals(product.getId().toString());
                        });
                assertTrue(found, "Наш товар должен быть в заказе");
            });

            // Проверяем статус заказа
            assertNotNull(order.getString("status"),
                    "Статус заказа не должен быть null");

            // Проверяем метод доставки
            assertEquals("Pickup", order.getString("deliveryMethod"),
                    "Метод доставки должен совпадать");

            // Проверяем дату создания
            assertNotNull(order.get("createdAt"),
                    "Дата создания заказа не должна быть null");
        });

        // Проверяем что корзина очистилась после checkout
        BasketResponse basketAfter = basketService.getBasket();
        step("Корзина очищена после checkout", () ->
                assertTrue(basketAfter.getItems().isEmpty(),
                        "Корзина должна быть пустой после оформления заказа")
        );
    }

    @Test
    void deleteBasketTest() {
        productService.withToken(token);
        basketService.withToken(token);

        // Добавляем товар в корзину
        ProductListResponse products = productService.getProducts();
        ProductResponse randomProduct = products.getProducts()
                .get(random.nextInt(products.getProducts().size()));

        BasketRequest build = BasketRequest.builder()
                .items(List.of(
                        BasketRequest.BasketItem.builder()
                                .productId(randomProduct.getId())
                                .productName(randomProduct.getName())
                                .quantity(random.nextInt(1, 5))
                                .price(randomProduct.getPrice().doubleValue())
                                .basePrice(randomProduct.getPrice().doubleValue())
                                .color(TestDataGenerator.randomString(5))
                                .imageFile(randomProduct.getImageFile())
                                .build()
                ))
                .build();
        basketService.storeBasket(build);

        // Проверяем что корзина НЕ пустая ПЕРЕД удалением
        BasketResponse basketBefore = basketService.getBasket();
        assertFalse(basketBefore.getItems().isEmpty(),
                "Корзина должна быть непустой перед удалением");

        // Удаляем корзину
        SuccessResponse response = basketService.deleteBasket();

        step("Статус 200", () ->
                assertEquals(200, basketService.getResponse().getStatusCode())
        );
        step("isSuccess = true", () ->
                assertTrue(response.getIsSuccess())
        );

        // Проверяем что корзина пустая ПОСЛЕ удаления
        BasketResponse basketAfter = basketService.getBasket();
        assertTrue(basketAfter.getItems().isEmpty(),
                "Корзина должна быть пустой после удаления");
    }

    private UUID uuidFromBinary(org.bson.types.Binary binary) {
        byte[] bytes = binary.getData();
        long msb = 0, lsb = 0;
        for (int i = 0; i < 8; i++) msb = (msb << 8) | (bytes[i] & 0xff);
        for (int i = 8; i < 16; i++) lsb = (lsb << 8) | (bytes[i] & 0xff);
        return new UUID(msb, lsb);
    }
}
