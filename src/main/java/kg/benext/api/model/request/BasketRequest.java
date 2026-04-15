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
public class BasketRequest extends BaseModel {
    private List<BasketItem> items;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
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