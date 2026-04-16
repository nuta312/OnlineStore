package kg.benext.api.model.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import java.util.List;
import java.util.UUID;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReviewListResponse {
    private List<ReviewItem> reviews;
    private Pagination pagination;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ReviewItem {
        private UUID id;
        private UUID productId;
        private String userId;
        private String userName;
        private Integer rating;
        private String comment;
        private String createdAt;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Pagination {
        private Integer pageNumber;
        private Integer pageSize;
        private Integer totalCount;
        private Integer totalPages;
    }
}