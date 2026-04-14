package api;

import kg.benext.api.model.request.OrderRequest;
import kg.benext.api.model.response.CreateOrderResponse;
import kg.benext.api.services.OrderService;
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
        OrderRequest request = OrderRequest.builder()
                .customerId("iT4MJATMOtUYui1m7GTk7kuLZHz2")
                .userName("testUser123")
                .orderName("testUser123")
                .shippingAddress(OrderRequest.AddressRequest.builder()
                        .firstName("John")
                        .lastName("Doe")
                        .emailAddress("test@test.com")
                        .addressLine("Самовывоз")
                        .country("Kyrgyzstan")
                        .state("Бишкек")
                        .zipCode("720000")
                        .build())
                .billingAddress(OrderRequest.AddressRequest.builder()
                        .firstName("John")
                        .lastName("Doe")
                        .emailAddress("test@test.com")
                        .addressLine("Самовывоз")
                        .country("Kyrgyzstan")
                        .state("Бишкек")
                        .zipCode("720000")
                        .build())
                .payment(OrderRequest.PaymentRequest.builder()
                        .cardName("John Doe")
                        .cardNumber("4242424242424242")
                        .expiration("12/33")
                        .cvv("123")
                        .paymentMethod(1)
                        .build())
                .orderItems(List.of(
                        OrderRequest.OrderItemRequest.builder()
                                .productId(UUID.fromString("ffc91150-2f5f-474e-bcd5-c84d0842bb46"))
                                .quantity(1)
                                .price(70.0)
                                .productNameSnapshot("Nike Yoga Luxe Top")
                                .build()
                ))
                .deliveryMethod("Pickup")
                .deliveryCost(0.0)
                .locale("en")
                .build();

        CreateOrderResponse response = orderService.createOrder(request);

        assertEquals(201, orderService.getResponse().getStatusCode());
        assertNotNull(response.getId());
        System.out.println("Заказ создан с ID: " + response.getId());
    }
}