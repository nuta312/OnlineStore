package kg.benext.api.model.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import java.util.List;
import java.util.UUID;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderResponse {
    private String id;
    private String customerId;
    private String userName;
    private String orderName;
    private Address shippingAddress;
    private Address billingAddress;
    private Payment payment;
    private Integer status;
    private List<OrderItem> items;
    private Double totalPrice;
    private String createdAt;
    private Integer deliveryMethod;
    private Double deliveryCost;
    private String locale;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Address {
        private String firstName;
        private String lastName;
        private String emailAddress;
        private String addressLine;
        private String country;
        private String state;
        private String zipCode;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Payment {
        private String cardName;
        private String cardNumber;
        private String expiration;
        private String cvv;
        private Integer paymentMethod;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class OrderItem {
        private UUID orderId;
        private UUID productId;
        private Integer quantity;
        private Double price;
        private String productName;
        private String imageFile;
    }
}