package api;

import kg.benext.api.model.request.BasketRequest;
import kg.benext.api.model.request.CheckoutRequest;
import kg.benext.api.model.response.*;
import kg.benext.api.services.AuthService;
import kg.benext.api.services.BasketService;
import kg.benext.common.utils.file.ConfigurationManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.*;

public class BasketTest extends BaseAPI {

    BasketService basketService = new BasketService(ConfigurationManager.getBaseConfig().baseUrl());
    String token;

    @BeforeEach
    void setUp() {
        token = AuthService.getToken("amanturov2471@gmail.com", "naryn25");
        basketService.withToken(token);
    }

    @Test
    void getBasketTest() {
        BasketResponse response = basketService.getBasket();

        step("Статус 200", () ->
                assertEquals(200, basketService.getResponse().getStatusCode())
        );
        step("Список items не null", () ->
                assertNotNull(response.getItems())
        );
    }

    @Test
    void storeBasketTest() {
        BasketRequest request = BasketRequest.builder()
                .items(List.of(
                        BasketRequest.BasketItem.builder()
                                .productId(UUID.fromString("ffc91150-2f5f-474e-bcd5-c84d0842bb46"))
                                .productName("Nike Yoga Luxe Top")
                                .quantity(1)
                                .price(70.0)
                                .basePrice(70.0)
                                .color("Black")
                                .imageFile("https://cdn.dummyjson.com/products/images/womens-dresses/1.png")
                                .build()
                ))
                .build();

        StoreBasketResponse response = basketService.storeBasket(request);

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
        step("Список методов доставки не пустой", () ->
                assertFalse(response.isEmpty())
        );
        step("У каждого метода есть name и cost", () -> {
            for (DeliveryMethodResponse method : response) {
                assertNotNull(method.getName());
                assertNotNull(method.getCost());
            }
        });
    }

    @Test
    void checkoutBasketTest() {
        BasketRequest basketRequest = BasketRequest.builder()
                .items(List.of(
                        BasketRequest.BasketItem.builder()
                                .productId(UUID.fromString("ffc91150-2f5f-474e-bcd5-c84d0842bb46"))
                                .productName("Nike Yoga Luxe Top")
                                .quantity(1)
                                .price(70.0)
                                .basePrice(70.0)
                                .color("Black")
                                .imageFile("https://cdn.dummyjson.com/products/images/womens-dresses/1.png")
                                .build()
                ))
                .build();
        basketService.storeBasket(basketRequest);

        CheckoutRequest request = CheckoutRequest.builder()
                .basketCheckoutDto(CheckoutRequest.BasketCheckoutDto.builder()
                        .firstName("Sanzhar")
                        .lastName("Amanturov")
                        .emailAddress("amanturov2471@gmail.com")
                        .addressLine("manas 123")
                        .country("Kyrgyzstan")
                        .state("Бишкек")
                        .zipCode("720000")
                        .cardName("Sanzhar Amanturov")
                        .cardNumber("4242424242424242")
                        .expiration("12/33")
                        .cvv("123")
                        .paymentMethod(1)
                        .deliveryMethod("Pickup")
                        .deliveryCost(0.0)
                        .build())
                .build();

        SuccessResponse response = basketService.checkout(request);

        step("Статус 200", () ->
                assertEquals(200, basketService.getResponse().getStatusCode())
        );
        step("isSuccess = true", () ->
                assertTrue(response.getIsSuccess())
        );
    }

    @Test
    void deleteBasketTest() {
        SuccessResponse response = basketService.deleteBasket();

        step("Статус 200", () ->
                assertEquals(200, basketService.getResponse().getStatusCode())
        );
        step("isSuccess = true", () ->
                assertTrue(response.getIsSuccess())
        );
    }
}