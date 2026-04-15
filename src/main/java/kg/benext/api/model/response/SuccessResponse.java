package kg.benext.api.model.response;

import lombok.Data;

@Data
public class SuccessResponse {
    private Boolean isSuccess;
    private Boolean alreadyExisted;
}