package api;

import kg.benext.api.model.request.DiscountRequest;
import kg.benext.api.model.response.DiscountDeleteResponse;
import kg.benext.api.model.response.DiscountResponse;
import kg.benext.api.services.DiscountService;
import kg.benext.common.utils.file.ConfigurationManager;
import org.junit.jupiter.api.Test;

import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.*;

public class DiscountTest extends BaseAPI {

    DiscountService discountService = new DiscountService(ConfigurationManager.getBaseConfig().baseUrl());

    @Test
    void createDiscountTest() {
        DiscountRequest request = DiscountRequest.builder()
                .productName("Nike Yoga Luxe Top")
                .description("Summer sale")
                .amount(10)
                .build();

        DiscountResponse response = discountService.createDiscount(request);

        step("Статус 200", () ->
                assertEquals(200, discountService.getResponse().getStatusCode())
        );
        step("productName совпадает", () ->
                assertEquals("Nike Yoga Luxe Top", response.getProductName())
        );
        step("amount совпадает", () ->
                assertEquals(10, response.getAmount())
        );
    }

    @Test
    void getDiscountByProductNameTest() {
        DiscountResponse response = discountService.getDiscountByProductName("Nike Yoga Luxe Top");

        step("Статус 200", () ->
                assertEquals(200, discountService.getResponse().getStatusCode())
        );
        step("productName не null", () ->
                assertNotNull(response.getProductName())
        );
    }

    @Test
    void updateDiscountTest() {
        DiscountRequest request = DiscountRequest.builder()
                .id(1)
                .productName("Nike Yoga Luxe Top")
                .description("Winter sale")
                .amount(20)
                .build();

        DiscountResponse response = discountService.updateDiscount(request);

        step("Статус 200", () ->
                assertEquals(200, discountService.getResponse().getStatusCode())
        );
        step("amount обновлён", () ->
                assertEquals(20, response.getAmount())
        );
    }

    @Test
    void deleteDiscountTest() {
        DiscountDeleteResponse response = discountService.deleteDiscount("Nike Yoga Luxe Top");

        step("Статус 200", () ->
                assertEquals(200, discountService.getResponse().getStatusCode())
        );
        step("success = true", () ->
                assertTrue(response.getSuccess())
        );
    }
}