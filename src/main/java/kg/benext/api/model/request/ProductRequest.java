package kg.benext.api.model.request;

import kg.benext.api.model.BaseModel;
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
public class ProductRequest extends BaseModel {

    private String name;
    private String description;
    private String imageFile;
    private Long price;
    private List<UUID> categoryIds;
    private String brandName;
    private List<TranslationRequest> translations;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TranslationRequest {
        private String languageCode;
        private String name;
        private String description;
    }
}