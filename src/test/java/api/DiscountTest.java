package api;

import kg.benext.api.model.request.DiscountRequest;
import kg.benext.api.model.response.DiscountDeleteResponse;
import kg.benext.api.model.response.DiscountResponse;
import kg.benext.api.services.DiscountService;
import kg.benext.common.utils.TestDataGenerator;
import kg.benext.common.utils.file.ConfigurationManager;
import org.junit.jupiter.api.Test;

import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.*;

public class DiscountTest extends BaseAPI {

    DiscountService discountService = new DiscountService(ConfigurationManager.getBaseConfig().baseUrl());

    @Test
    void createAndGetDiscountTest() {
        String productName = TestDataGenerator.randomString(8);
        String description = TestDataGenerator.randomString(12);
        int amount = new java.util.Random().nextInt(5, 50);

        DiscountRequest request = DiscountRequest.builder()
                .productName(productName)
                .description(description)
                .amount(amount)
                .build();

        DiscountResponse created = discountService.createDiscount(request);

        step("Создание: статус 200", () ->
                assertEquals(200, discountService.getResponse().getStatusCode())
        );
        step("productName совпадает", () ->
                assertEquals(productName, created.getProductName())
        );

        // Получаем по имени
        DiscountResponse fetched = discountService.getDiscountByProductName(productName);

        step("Получение: статус 200", () ->
                assertEquals(200, discountService.getResponse().getStatusCode())
        );
        step("productName совпадает", () ->
                assertEquals(productName, fetched.getProductName())
        );
    }

    @Test
    void updateDiscountTest() {
        String productName = TestDataGenerator.randomString(8);
        discountService.createDiscount(DiscountRequest.builder()
                .productName(productName)
                .description(TestDataGenerator.randomString(10))
                .amount(10)
                .build());

        int newAmount = new java.util.Random().nextInt(50, 90);
        DiscountResponse updated = discountService.updateDiscount(DiscountRequest.builder()
                .productName(productName)
                .description(TestDataGenerator.randomString(10))
                .amount(newAmount)
                .build());

        step("Статус 200", () ->
                assertEquals(200, discountService.getResponse().getStatusCode())
        );
        step("amount обновлён", () ->
                assertEquals(newAmount, updated.getAmount())
        );
    }

    @Test
    void deleteDiscountTest() {
        String productName = TestDataGenerator.randomString(8);
        discountService.createDiscount(DiscountRequest.builder()
                .productName(productName)
                .description(TestDataGenerator.randomString(10))
                .amount(15)
                .build());

        DiscountDeleteResponse response = discountService.deleteDiscount(productName);

        step("Статус 200", () ->
                assertEquals(200, discountService.getResponse().getStatusCode())
        );
        step("success = true", () ->
                assertTrue(response.getSuccess())
        );
    }
}