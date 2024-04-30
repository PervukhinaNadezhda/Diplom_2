import io.qameta.allure.Description;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import user.User;
import user.UserGenerator;
import user.UserMethods;

import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.IsEqual.equalTo;

public class UserAuthRegisterPostTests {

    private User user;
    private UserMethods userMethods;
    String accessToken;

    @Before
    public void createTestData() {
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
        userMethods = new UserMethods();
        user = UserGenerator.random();
    }

    @After
    public void reset() {
        if (accessToken != null) {
            userMethods.delete(accessToken);
        }
        RestAssured.reset();
    }

    @Test
    @Description("Проверка возможности создания уникального пользователя: " +
            "В теле запроса регистрации передаются все обязательные поля. " +
            "Проверяется код ответа. " +
            "Проверяется тело ответа.")
    public void uniqueUserCanBeCreated() {
        Response createResponse = userMethods.create(user);
        accessToken = createResponse.path("accessToken");

        createResponse.then().assertThat()
                .body("success", equalTo(true))
                .body("user.email", equalTo(user.getEmail()))
                .body("user.name", equalTo(user.getName()))
                .body("accessToken", notNullValue())
                .body("refreshToken", notNullValue())
                .and()
                .statusCode(200);
    }

    @Test
    @Description("Проверка невозможности создания пользователя с уже зарегистрированным email: " +
            "В теле запроса регистрации передаются все обязательные поля. " +
            "В теле повторного запроса регистрации передаются все поля с уже зарегистрированным email. " +
            "Проверяется код ответа. " +
            "Проверяется тело ответа.")
    public void sameEmailUserCantBeCreated() {
        Response createResponse = userMethods.create(user);
        accessToken = createResponse.path("accessToken");

        user.setName(UserGenerator.nameRandom());
        user.setPassword(UserGenerator.passwordRandom());
        Response createSecondResponse = userMethods.create(user);

        createSecondResponse.then().assertThat()
                .body("success", equalTo(false))
                .body("message", equalTo("User already exists"))
                .and()
                .statusCode(403);
    }

    @Test
    @Description("Проверка невозможности создания пользователя без email: " +
            "В теле запроса регистрации передаются password и name. " +
            "Проверяется код ответа. " +
            "Проверяется тело ответа.")
    public void userWithEmailNullCantBeCreated() {
        user.setEmail(null);
        Response createResponse = userMethods.create(user);

        createResponse.then().assertThat()
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"))
                .and()
                .statusCode(403);
    }

    @Test
    @Description("Проверка невозможности создания пользователя без password: " +
            "В теле запроса регистрации передаются email и name. " +
            "Проверяется код ответа. " +
            "Проверяется тело ответа.")
    public void userWithPasswordNullCantBeCreated() {
        user.setPassword(null);
        Response createResponse = userMethods.create(user);

        createResponse.then().assertThat()
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"))
                .and()
                .statusCode(403);
    }

    @Test
    @Description("Проверка невозможности создания пользователя без name: " +
            "В теле запроса регистрации передаются password и email. " +
            "Проверяется код ответа. " +
            "Проверяется тело ответа.")
    public void userWithNameNullCantBeCreated() {
        user.setName(null);
        Response createResponse = userMethods.create(user);

        createResponse.then().assertThat()
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"))
                .and()
                .statusCode(403);
    }

}
