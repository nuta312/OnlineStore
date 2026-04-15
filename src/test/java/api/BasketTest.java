package api;

import kg.benext.api.model.request.BasketRequest;
import kg.benext.api.model.request.CheckoutRequest;
import kg.benext.api.model.response.*;
import kg.benext.api.services.AuthService;
import kg.benext.api.services.BasketService;
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

public class BasketTest extends BaseAPI {

    BasketService basketService = new BasketService(ConfigurationManager.getBaseConfig().baseUrl());
    ProductService productService = new ProductService(ConfigurationManager.getBaseConfig().baseUrl());
    Random random = new Random();

    @BeforeEach
    void setUp() {
        String token = AuthService.getToken("amanturov2471@gmail.com", "naryn25");
        basketService.withToken(token);
        productService.withToken(token);
    }

    private BasketRequest buildRandomBasketRequest() {
        ProductListResponse products = productService.getProducts();
        ProductResponse randomProduct = products.getProducts()
                .get(random.nextInt(products.getProducts().size()));

        return BasketRequest.builder()
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
    }

    @Test
    void getBasketTest() {
        BasketResponse response = basketService.getBasket();

        step("Статус 200", () ->
                assertEquals(200, basketService.getResponse().getStatusCode())
        );
        step("Items не null", () ->
                assertNotNull(response.getItems())
        );
    }

    @Test
    void storeBasketTest() {
        StoreBasketResponse response = basketService.storeBasket(buildRandomBasketRequest());

        step("Статус 201", () ->
                assertEquals(201, basketService.getResponse().getStatusCode())
        );
        step("userName не null", () ->
                assertNotNull(response.getUserName())
        );
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
    void checkoutBasketTest() {
        basketService.storeBasket(buildRandomBasketRequest());

        CheckoutRequest.BasketCheckoutDto dto = CheckoutRequest.BasketCheckoutDto.builder()
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
                .paymentMethod(random.nextInt(1, 3))
                .deliveryMethod("Pickup")
                .deliveryCost(0.0)
                .build();

        SuccessResponse response = basketService.checkout(
                CheckoutRequest.builder().basketCheckoutDto(dto).build()
        );

        step("Статус 200", () ->
                assertEquals(200, basketService.getResponse().getStatusCode())
        );
        step("isSuccess = true", () ->
                assertTrue(response.getIsSuccess())
        );
    }

    @Test
    void deleteBasketTest() {
        basketService.storeBasket(buildRandomBasketRequest());
        SuccessResponse response = basketService.deleteBasket();

        step("Статус 200", () ->
                assertEquals(200, basketService.getResponse().getStatusCode())
        );
        step("isSuccess = true", () ->
                assertTrue(response.getIsSuccess())
        );
    }
}