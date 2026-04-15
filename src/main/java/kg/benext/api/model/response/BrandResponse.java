package kg.benext.api.model.response;

import lombok.Data;
import java.util.UUID;

@Data
public class BrandResponse {
    private UUID id;
    private String name;
    private String description;
    private String imageFile;
}