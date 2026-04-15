package kg.benext.api.model.request;

import kg.benext.api.model.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiscountRequest extends BaseModel {
    private Integer id;
    private String productName;
    private String description;
    private Integer amount;
}