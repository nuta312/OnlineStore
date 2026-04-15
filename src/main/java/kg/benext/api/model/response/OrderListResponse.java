package kg.benext.api.model.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderListResponse {
    private List<OrderResponse> orders;
    private Integer totalItemCount;
    private Integer pageCount;
    private Integer pageNumber;
    private Integer pageSize;
}