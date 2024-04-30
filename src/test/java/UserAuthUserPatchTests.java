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

import static org.hamcrest.core.IsEqual.equalTo;

public class UserAuthUserPatchTests {

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
    @Description("Проверка обновления email для авторизированного пользователя: " +
            "В запросе обновления передается новый email и токен авторизации. " +
            "Проверяется код ответа. " +
            "Проверяется тело ответа.")
    public void emailAuthorizedUserCanBeUpdate() {
        user.setEmail(UserGenerator.emailRandom());
        Response updateResponse = userMethods.updateAuthorized(user, accessToken);

        updateResponse.then().assertThat()
                .body("success", equalTo(true))
                .body("user.email", equalTo(user.getEmail()))
                .body("user.name", equalTo(user.getName()))
                .and()
                .statusCode(200);
    }

    @Test
    @Description("Проверка обновления password для авторизированного пользователя: " +
            "В запросе обновления передается новый password и токен авторизации. " +
            "Проверяется код ответа. " +
            "Проверяется тело ответа.")
    public void passwordAuthorizedUserCanBeUpdate() {
        user.setPassword(UserGenerator.passwordRandom());
        Response updateResponse = userMethods.updateAuthorized(user, accessToken);

        updateResponse.then().assertThat()
                .body("success", equalTo(true))
                .body("user.email", equalTo(user.getEmail()))
                .body("user.name", equalTo(user.getName()))
                .and()
                .statusCode(200);
    }

    @Test
    @Description("Проверка обновления name для авторизированного пользователя: " +
            "В запросе обновления передается новый name и токен авторизации. " +
            "Проверяется код ответа. " +
            "Проверяется тело ответа.")
    public void nameAuthorizedUserCanBeUpdate() {
        user.setName(UserGenerator.nameRandom());
        Response updateResponse = userMethods.updateAuthorized(user, accessToken);

        updateResponse.then().assertThat()
                .body("success", equalTo(true))
                .body("user.email", equalTo(user.getEmail()))
                .body("user.name", equalTo(user.getName()))
                .and()
                .statusCode(200);
    }

    @Test
    @Description("Проверка отсутствия обновления email для авторизированного пользователя: " +
            "Создатется еще один пользователь. " +
            "В запросе обновления для первого пользователя передается email второго и токен авторизации. " +
            "Проверяется код ответа. " +
            "Проверяется тело ответа.")
    public void emailAuthorizedUserCantBeUpdate() {
        User secondUser = UserGenerator.random();
        Response createSecondResponse = userMethods.create(secondUser);
        secondAccessToken = createSecondResponse.path("accessToken");

        user.setEmail(secondUser.getEmail());
        Response updateResponse = userMethods.updateAuthorized(user, accessToken);

        updateResponse.then().assertThat()
                .body("success", equalTo(false))
                .body("message", equalTo("User with such email already exists"))
                .and()
                .statusCode(403);
    }

    @Test
    @Description("Проверка отсутствия обновления password для неавторизированного пользователя: " +
            "В теле запроса обновления передается новый password. " +
            "Проверяется код ответа. " +
            "Проверяется тело ответа.")
    public void passwordUnauthorizedUserCantBeUpdate() {
        user.setPassword(UserGenerator.passwordRandom());
        Response updateResponse = userMethods.updateUnauthorized(user);

        updateResponse.then().assertThat()
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"))
                .and()
                .statusCode(401);
    }

}
