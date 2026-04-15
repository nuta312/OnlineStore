package kg.benext.common.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class User {
        private String fullName;
        private String email;
        private String password;
}