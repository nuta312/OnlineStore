package kg.benext.api.model.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import java.util.List;
import java.util.UUID;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BasketResponse {
    private String userName;
    private List<BasketItem> items;
    private Double totalPrice;
    private Double subtotal;
    private Double totalDiscount;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class BasketItem {
        private Integer quantity;
        private String color;
        private Double price;
        private Double basePrice;
        private UUID productId;
        private String productName;
        private String imageFile;
    }
}