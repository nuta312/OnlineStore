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
                .id("8638ffca-c020-48b7-b5fe-e3774e51714a")
                .customerId("iT4MJATMOtUYui1m7GTk7kuLZHz2")
                .userName("iT4MJATMOtUYui1m7GTk7kuLZHz2")
                .orderName("iT4MJATMOtUYui1m7GTk7kuLZHz2")
                .shippingAddress(OrderRequest.AddressRequest.builder()
                        .firstName("Sanzhar")
                        .lastName("Amanturov")
                        .emailAddress("amanturov2471@gmail.com")
                        .addressLine("manas 123")
                        .country("Kyrgyzstan")
                        .state("Бишкек")
                        .zipCode("720000")
                        .build())
                .billingAddress(OrderRequest.AddressRequest.builder()
                        .firstName("Sanzhar")
                        .lastName("Amanturov")
                        .emailAddress("amanturov2471@gmail.com")
                        .addressLine("manas 123")
                        .country("Kyrgyzstan")
                        .state("Бишкек")
                        .zipCode("720000")
                        .build())
                .payment(OrderRequest.PaymentRequest.builder()
                        .cardName("1fsdfsdf")
                        .cardNumber("1234123412341234")
                        .expiration("09/23")
                        .cvv("123")
                        .paymentMethod(1)
                        .build())
                .Items(List.of(
                        OrderRequest.OrderItemRequest.builder()
                                .productId(UUID.fromString("ffc91150-2f5f-474e-bcd5-c84d0842bb46"))
                                .quantity(1)
                                .price(80.0)
                                .productName("Nike Yoga Luxe Top")
                                .build()
                ))
                .status(3)
                .deliveryMethod(2)
                .deliveryCost(200.0)
                .locale("en")
                .build();

        CreateOrderResponse response = orderService.createOrder(request);

        System.out.println("Status: " + orderService.getResponse().getStatusCode());
        System.out.println("Body: " + orderService.getResponse().getBody().asString());

        assertEquals(201, orderService.getResponse().getStatusCode());
        assertNotNull(response.getId());
        System.out.println("Заказ создан с ID: " + response.getId());
    }
}