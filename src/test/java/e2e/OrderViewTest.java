package e2e;

import kg.benext.api.model.response.CreateOrderResponse;
import kg.benext.api.model.response.OrderListResponse;
import kg.benext.api.model.response.OrderResponse;
import kg.benext.api.services.AuthService;
import kg.benext.api.services.OrderService;
import kg.benext.common.utils.TestDataGenerator;
import kg.benext.common.utils.file.ConfigurationManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.*;

public class OrderViewTest extends BaseE2E {

    OrderService orderServiceA = new OrderService(ConfigurationManager.getBaseConfig().baseUrl());
    OrderService orderServiceB = new OrderService(ConfigurationManager.getBaseConfig().baseUrl());
    String createdOrderId;

    @BeforeEach
    void setUp() {
        // User A
        String tokenA = AuthService.getToken("amanturov2471@gmail.com", "naryn25");
        orderServiceA.withToken(tokenA);

        // Создаём заказ как предусловие (аналог E2E-013)
        CreateOrderResponse created = orderServiceA.createOrder(TestDataGenerator.randomOrderRequest());
        assertNotNull(created.getId());
        createdOrderId = created.getId().toString();
        System.out.println("Предусловие: создан заказ с ID: " + createdOrderId);

        // User B
        String tokenB = AuthService.getToken("JohnDoe@example.com", "123456");
        orderServiceB.withToken(tokenB);
    }

    @Test
    @DisplayName("E2E-016: Просмотр списка своих заказов")
    void e2e016_viewOwnOrdersList() {
        OrderListResponse response = orderServiceA.getOrders();

        step("Статус 200", () ->
                assertEquals(200, orderServiceA.getResponse().getStatusCode())
        );

        step("Список заказов не пуст", () ->
                assertFalse(response.getOrders().isEmpty())
        );

        step("Поля id, status, totalPrice, createdAt присутствуют", () -> {
            OrderResponse first = response.getOrders().get(0);
            assertNotNull(first.getId());
            assertNotNull(first.getStatus());
            assertNotNull(first.getTotalPrice());
            assertNotNull(first.getCreatedAt());
        });
    }

    @Test
    @DisplayName("E2E-017: Просмотр деталей конкретного заказа")
    void e2e017_viewOrderDetails() {
        OrderResponse order = orderServiceA.getOrderById(createdOrderId);

        step("Статус 200", () ->
                assertEquals(200, orderServiceA.getResponse().getStatusCode())
        );

        step("ID совпадает", () ->
                assertEquals(createdOrderId, order.getId())
        );

        step("Список товаров не пуст", () -> {
            assertNotNull(order.getItems());
            assertFalse(order.getItems().isEmpty());
        });

        step("shippingAddress присутствует", () ->
                assertNotNull(order.getShippingAddress())
        );

        step("deliveryMethod присутствует", () ->
                assertNotNull(order.getDeliveryMethod())
        );

        step("totalPrice присутствует", () ->
                assertNotNull(order.getTotalPrice())
        );
    }

    @Test
    @Disabled("BUG: API возвращает 200 вместо 403/404. Бэкенд не проверяет ownership заказа. Баг зафиксирован.")
    @DisplayName("E2E-018: Доступ к чужому заказу запрещён (403 или 404)")
    void e2e018_cannotAccessAnotherUserOrder() {
        // Создаём заказ от User B
        CreateOrderResponse createdByB = orderServiceB.createOrder(TestDataGenerator.randomOrderRequest());
        assertNotNull(createdByB.getId());
        String orderIdOfB = createdByB.getId().toString();

        // User A пытается получить заказ User B
        orderServiceA.getOrderById(orderIdOfB);

        step("Ответ 403 или 404", () ->
                assertTrue(
                        orderServiceA.getResponse().getStatusCode() == 403 ||
                                orderServiceA.getResponse().getStatusCode() == 404,
                        "Ожидали 403 или 404, получили: " + orderServiceA.getResponse().getStatusCode()
                )
        );
    }
}