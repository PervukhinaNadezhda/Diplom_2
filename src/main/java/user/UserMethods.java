package user;

import io.qameta.allure.Step;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class UserMethods {

    private static final String BASE_URL = "https://stellarburgers.nomoreparties.site";
    private static final String USER_REGISTER_PATH = "/api/auth/register";
    private static final String USER_LOGIN_PATH = "/api/auth/login";
    private static final String USER_UPDATE_PATH = "/api/auth/user";

    @Step("Создать пользователя")
    public Response create(User user) {
        Response response = given()
                .baseUri(BASE_URL)
                .header("Content-type", "application/json")
                .body(user)
                .when()
                .post(USER_REGISTER_PATH)
                .then()
                .extract().response();
        return response;
    }

    @Step("Удалить пользователя")
    public Response delete(String accessToken) {
        return given()
                .baseUri(BASE_URL)
                .header("Authorization", accessToken)
                .when()
                .delete(USER_UPDATE_PATH);
    }

    @Step("Логин пользователя")
    public Response login(User user) {
        return given()
                .baseUri(BASE_URL)
                .header("Content-type", "application/json")
                .body(user)
                .when()
                .post(USER_LOGIN_PATH);
    }

    @Step("Измененить данные авторизованному пользователю")
    public Response updateAuthorized(User user, String accessToken) {
        return given()
                .baseUri(BASE_URL)
                .header("Authorization", accessToken)
                .header("Content-type", "application/json")
                .body(user)
                .when()
                .patch(USER_UPDATE_PATH);
    }

    @Step("Измененить данные неавторизованному пользователю")
    public Response updateUnauthorized(User user) {
        return given()
                .baseUri(BASE_URL)
                .header("Content-type", "application/json")
                .body(user)
                .when()
                .patch(USER_UPDATE_PATH);
    }


}
