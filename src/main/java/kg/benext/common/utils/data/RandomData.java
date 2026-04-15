package kg.benext.common.utils.data;

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