package kg.benext.api.model.response;

import lombok.Data;
import java.util.List;
import java.util.UUID;

@Data
public class CategoryResponse {
    private UUID id;
    private String name;
    private String slug;
    private UUID parentId;
    private String path;
    private List<TranslationResponse> translations;

    @Data
    public static class TranslationResponse {
        private String languageCode;
        private String displayName;
        private String description;
    }
}