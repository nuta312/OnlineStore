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
public class UpdateProductRequest extends BaseModel {
    private UUID id;
    private String name;
    private String description;
    private String imageFile;
    private Long price;
    private List<UUID> categoryIds;
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