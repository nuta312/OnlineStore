package api;

import kg.benext.api.model.request.OrderRequest;
import kg.benext.api.model.response.CreateOrderResponse;
import kg.benext.api.services.OrderService;
import kg.benext.common.utils.TestDataGenerator;
import kg.benext.common.utils.file.ConfigurationManager;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class OrderTest {

    OrderService orderService = new OrderService(ConfigurationManager.getBaseConfig().baseUrl());

    @Test
    void createOrderTest() {
        OrderRequest request = TestDataGenerator.orderRequestBuilder()
                .status(3)
                .deliveryMethod(2)
                .build();

        CreateOrderResponse response = orderService.createOrder(request);

        System.out.println("Status: " + orderService.getResponse().getStatusCode());
        System.out.println("Body: " + orderService.getResponse().getBody().asString());

        assertEquals(201, orderService.getResponse().getStatusCode());
        assertNotNull(response.getId());
        System.out.println("Заказ создан с ID: " + response.getId());
    }
}