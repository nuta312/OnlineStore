package kg.benext.api.model.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import java.util.List;
import java.util.UUID;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class FavoriteListResponse {
    private List<FavoriteItem> favorites;
    private Pagination pagination;

    @Data
    public static class FavoriteItem {
        private UUID productId;
        private String name;
        private String imageFile;
        private Double price;
        private Double averageRating;
        private Integer reviewCount;
        private String addedAt;
    }

    @Data
    public static class Pagination {
        private Integer pageNumber;
        private Integer pageSize;
        private Integer totalCount;
        private Integer totalPages;
    }
}