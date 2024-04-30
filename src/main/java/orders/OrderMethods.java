package orders;

import io.qameta.allure.Step;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class OrderMethods {

    private static final String BASE_URL = "https://stellarburgers.nomoreparties.site";
    public static final String ORDER_PATH = "/api/orders";

    @Step("Создать заказ авторизованному пользователю")
    public Response createAuthorized(Order order, String accessToken) {
        return given()
                .baseUri(BASE_URL)
                .header("Authorization", accessToken)
                .header("Content-type", "application/json")
                .body(order)
                .when()
                .post(ORDER_PATH);
    }

    @Step("Создать заказ неавторизованному пользователю")
    public Response createUnauthorized(Order order) {
        return given()
                .baseUri(BASE_URL)
                .header("Content-Type", "application/json")
                .body(order)
                .when()
                .post(ORDER_PATH);
    }

    @Step("Получить заказ пользователя с авторизацией")
    public Response getByUserAuthorized(String accessToken) {
        return given()
                .baseUri(BASE_URL)
                .header("Authorization", accessToken)
                .when()
                .get(ORDER_PATH);
    }

    @Step("Получить заказ пользователя без авторизации")
    public Response getByUserUnauthorized() {
        return given()
                .baseUri(BASE_URL)
                .when()
                .get(ORDER_PATH);
    }
}
