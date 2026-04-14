package kg.benext.api.model.request;

import kg.benext.api.model.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest extends BaseModel {

    private String customerId;
    private String userName;
    private String orderName;
    private AddressRequest shippingAddress;
    private AddressRequest billingAddress;
    private PaymentRequest payment;
    private List<OrderItemRequest> orderItems;
    private String deliveryMethod;
    private Double deliveryCost;
    private String locale;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddressRequest {
        private String firstName;
        private String lastName;
        private String emailAddress;
        private String addressLine;
        private String country;
        private String state;
        private String zipCode;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaymentRequest {
        private String cardName;
        private String cardNumber;
        private String expiration;
        private String cvv;
        private Integer paymentMethod;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemRequest {
        private UUID productId;
        private Integer quantity;
        private Double price;
        private String productNameSnapshot;
    }
}