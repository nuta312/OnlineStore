package kg.benext.common.utils.data;

import com.github.javafaker.Faker;
import kg.benext.api.model.request.OrderRequest;
import kg.benext.common.model.User;

import com.github.javafaker.Faker;
import kg.benext.common.model.User;

public class RandomData {
    private static final Faker faker = new Faker();

    public static User defaultUser() {
        return User.builder()
                .fullName("John Doe")
                .email("JohnDoe@example.com")
                .password("123456")
                .build();
    }


    public static User randomUser() {
        return User.builder()
                .fullName(faker.name().fullName())
                .email("test" + System.currentTimeMillis() + "@example.com")
                .password("123456")
                .build();
    }

    public static User userWithoutEmail() {
        return User.builder()
                .fullName(faker.name().fullName())
                .email("")
                .password("123456")
                .build();
    }
}
//    public static OrderRequest randomOrder(){
//        OrderRequest orderRequest = new OrderRequest();
//        orderRequest.setId(faker.options().option(
//                "1b33caca-3679-4bd6-b403-32f8122e9d46","60db7604-a05f-4688-891c-fc8963c80559",
//        "26316d8a-8982-4019-92e2-acebeaf7f8fa", "ff2be9cb-af73-4663-b395-f236021c9408",
//        "5173009f-23be-4e6e-8bd4-118962217746", "1b0b06ed-22ae-49e6-ada0-e4793945c7ad",
//        "4a09717c-8bc0-4c2d-95c7-e71029353a4c", "1ee7d012-5545-4584-9cf8-411c4e8a5086",
//        "54353ddb-ec70-44dc-893e-b71f1aae1530", "83e51fbc-a194-4fa8-9bfc-e0da8edabb29"
//        ));
//        orderRequest.setCustomerId("");
//    }


