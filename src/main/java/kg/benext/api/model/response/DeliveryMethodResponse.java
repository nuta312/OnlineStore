package kg.benext.api.model.response;

import lombok.Data;

@Data
public class DeliveryMethodResponse {
    private String name;
    private String title;
    private String description;
    private Double cost;
}