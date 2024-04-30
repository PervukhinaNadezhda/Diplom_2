package user;
import com.github.javafaker.Faker;

public class UserGenerator {
    static Faker faker = new Faker();

    public static User random() {
        final String email = faker.internet().emailAddress();
        final String password = faker.internet().password();
        final String name = faker.name().name();
        return new User(email, password, name);
    }

    public static String emailRandom() {
        return faker.internet().emailAddress();
    }

    public static String passwordRandom() {
        return faker.internet().password();
    }

    public static String nameRandom() {
        return faker.name().name();
    }

}
