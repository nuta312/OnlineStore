package kg.benext.api.model.response;

import lombok.Data;
import java.util.List;

@Data
public class BrandListResponse {
    private List<BrandResponse> brands;
}