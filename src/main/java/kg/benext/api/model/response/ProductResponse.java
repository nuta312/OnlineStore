package kg.benext.api.model.response;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {

    private UUID id;
    private String name;
    private String description;
    private String imageFile;
    private Long price;
    private List<UUID> categoryIds;
    private List<TranslationResponse> translations;
    private Double averageRating;
    private Integer reviewCount;
    private UUID brandId;
    private String brandName;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TranslationResponse {
        private String languageCode;
        private String name;
        private String description;
    }
}