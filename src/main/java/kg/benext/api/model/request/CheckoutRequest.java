package kg.benext.api.model.request;

import kg.benext.api.model.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckoutRequest extends BaseModel {
    private BasketCheckoutDto basketCheckoutDto;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BasketCheckoutDto {
        private String firstName;
        private String lastName;
        private String emailAddress;
        private String addressLine;
        private String country;
        private String state;
        private String zipCode;
        private String cardName;
        private String cardNumber;
        private String expiration;
        private String cvv;
        private Integer paymentMethod;
        private String deliveryMethod;
        private Double deliveryCost;
    }
}