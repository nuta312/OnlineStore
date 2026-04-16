package api;

import kg.benext.api.model.request.OrderRequest;
import kg.benext.api.model.response.CreateOrderResponse;
import kg.benext.api.model.response.OrderListResponse;
import kg.benext.api.model.response.OrderResponse;
import kg.benext.api.services.AuthService;
import kg.benext.api.services.OrderService;
import kg.benext.common.utils.TestDataGenerator;
import kg.benext.common.utils.file.ConfigurationManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.*;

public class OrderTest extends BaseAPI {

    OrderService orderService = new OrderService(ConfigurationManager.getBaseConfig().baseUrl());

    @BeforeEach
    void setUp() {
        String token = AuthService.getToken("amanturov2471@gmail.com", "naryn25");
        orderService.withToken(token);
    }

    @Test
    void createOrderTest() {
        OrderRequest request = TestDataGenerator.randomOrderRequest();
        CreateOrderResponse response = orderService.createOrder(request);

        step("Статус 201", () ->
                assertEquals(201, orderService.getResponse().getStatusCode())
        );
        step("ID не null", () ->
                assertNotNull(response.getId())
        );
        System.out.println("Заказ создан с ID: " + response.getId());
    }

    @Test
    void getOrdersTest() {
        OrderListResponse response = orderService.getOrders();

        step("Статус 200", () ->
                assertEquals(200, orderService.getResponse().getStatusCode())
        );
        step("Список заказов не null", () ->
                assertNotNull(response.getOrders())
        );
    }

    @Test
    void getOrderByIdTest() {
        CreateOrderResponse created = orderService.createOrder(TestDataGenerator.randomOrderRequest());
        assertNotNull(created.getId());

        OrderResponse order = orderService.getOrderById(created.getId().toString());

        step("Статус 200", () ->
                assertEquals(200, orderService.getResponse().getStatusCode())
        );
        step("ID совпадает", () ->
                assertEquals(created.getId().toString(), order.getId())
        );
    }

    @Test
    void updateOrderTest() {
        CreateOrderResponse created = orderService.createOrder(TestDataGenerator.randomOrderRequest());
        assertNotNull(created.getId());

        OrderRequest updateRequest = TestDataGenerator.orderRequestBuilder()
                .id(created.getId().toString())
                .status(2)
                .build();

        orderService.updateOrder(updateRequest);

        step("Статус 200", () ->
                assertEquals(200, orderService.getResponse().getStatusCode())
        );
    }

    @Test
    void deleteOrderTest() {
        CreateOrderResponse created = orderService.createOrder(TestDataGenerator.randomOrderRequest());
        assertNotNull(created.getId());

        orderService.deleteOrder(created.getId().toString());

        step("Статус 204", () ->
                assertEquals(204, orderService.getResponse().getStatusCode())
        );
    }
}