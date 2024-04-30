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

public class UserAuthLoginPostTests {

    private User user;
    private UserMethods userMethods;
    String accessToken;
    String secondAccessToken;

    @Before
    public void createTestData() {
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
        userMethods = new UserMethods();
        user = UserGenerator.random();

        Response createResponse = userMethods.create(user);
        accessToken = createResponse.path("accessToken");
    }

    @After
    public void cleanUp() {
        userMethods.delete(accessToken);

        if (secondAccessToken != null) {
            userMethods.delete(secondAccessToken);
        }
        RestAssured.reset();
    }

    @Test
    @Description("Проверка возможности залогиниться под существующим пользователем: " +
            "В теле запроса логина передаются все обязательные поля. " +
            "Проверяется код ответа. " +
            "Проверяется тело ответа.")
    public void userCanBeLogin() {
        Response loginResponse = userMethods.login(user);

        loginResponse.then().assertThat()
                .body("success", equalTo(true))
                .body("user.email", equalTo(user.getEmail()))
                .body("user.name", equalTo(user.getName()))
                .body("accessToken", notNullValue())
                .body("refreshToken", notNullValue())
                .and()
                .statusCode(200);
    }

    @Test
    @Description("Проверка невозможности залогиниться под существующим пользователем без email: " +
            "В теле запроса логина передаются password и name. " +
            "Проверяется код ответа. " +
            "Проверяется тело ответа.")
    public void userWithEmailNullCantBeLogin() {
        user.setEmail(null);
        Response loginResponse = userMethods.login(user);

        loginResponse.then().assertThat()
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"))
                .and()
                .statusCode(401);
    }

    @Test
    @Description("Проверка невозможности залогиниться под существующим пользователем без password: " +
            "В теле запроса логина передаются email и name. " +
            "Проверяется код ответа. " +
            "Проверяется тело ответа.")
    public void userWithPasswordNullCantBeLogin() {
        user.setPassword(null);
        Response loginResponse = userMethods.login(user);

        loginResponse.then().assertThat()
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"))
                .and()
                .statusCode(401);
    }

    @Test
    @Description("Проверка невозможности залогиниться под существующим пользователем с другим email: " +
            "Создатется еще один пользователь. " +
            "В теле запроса логина передаются password и name одного пользователя, email - другого. " +
            "Проверяется код ответа. " +
            "Проверяется тело ответа.")
    public void userWithAnotherEmailCantBeLogin() {
        User secondUser = UserGenerator.random();
        Response createSecondResponse = userMethods.create(secondUser);
        secondAccessToken = createSecondResponse.path("accessToken");

        user.setEmail(secondUser.getEmail());
        Response loginResponse = userMethods.login(user);

        loginResponse.then().assertThat()
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"))
                .and()
                .statusCode(401);
    }

    @Test
    @Description("Проверка невозможности залогиниться под существующим пользователем с другим password: " +
            "Создатется еще один пользователь. " +
            "В теле запроса логина передаются email и name одного пользователя, password - другого. " +
            "Проверяется код ответа. " +
            "Проверяется тело ответа.")
    public void userWithAnotherPasswordCantBeLogin() {
        User secondUser = UserGenerator.random();
        Response createSecondResponse = userMethods.create(secondUser);
        secondAccessToken = createSecondResponse.path("accessToken");

        user.setPassword(secondUser.getPassword());
        Response loginResponse = userMethods.login(user);

        loginResponse.then().assertThat()
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"))
                .and()
                .statusCode(401);
    }

}
