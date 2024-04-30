import helper.GenerateRandom;
import ingredients.IngredientsMethods;
import io.qameta.allure.Description;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.response.Response;
import orders.Order;
import orders.OrderGenerator;
import orders.OrderMethods;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import user.User;
import user.UserGenerator;
import user.UserMethods;

import java.util.List;

import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.IsEqual.equalTo;

public class OrdersGetTests {

    private User user;
    private UserMethods userMethods;
    String accessToken;
    private IngredientsMethods ingredientsMethods;
    private Order order;
    private OrderMethods orderMethods;


    @Before
    public void createTestData() {
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());

        userMethods = new UserMethods();
        user = UserGenerator.random();
        Response createUserResponse = userMethods.create(user);
        accessToken = createUserResponse.path("accessToken");

        ingredientsMethods = new IngredientsMethods();
        orderMethods = new OrderMethods();
        List<String> allIngredientsHashesList = ingredientsMethods.getHashes();
        order = OrderGenerator.random(allIngredientsHashesList);
        orderMethods.createAuthorized(order, accessToken);
    }

    @After
    public void reset() {
        userMethods.delete(accessToken);
        RestAssured.reset();
    }

    @Test
    @Description("Проверка возможности получения заказа пользователя для авторизированного пользователя: " +
            "В запросе получения заказа передается токен авторизации. " +
            "Проверяется код ответа. " +
            "Проверяется тело ответа.")
    public void orderWithAuthorizationCanBeGet() {
        Response getOrderResponse = orderMethods.getByUserAuthorized(accessToken);

        getOrderResponse.then().assertThat()
                .body("success", equalTo(true))
                .body("orders", notNullValue())
                .body("total", notNullValue())
                .body("totalToday", notNullValue())
                .and()
                .statusCode(200);
    }

    @Test
    @Description("Проверка невозможности получения заказа пользователя для неавторизированного пользователя: " +
            "В запросе получения заказа не передается токен авторизации. " +
            "Проверяется код ответа. " +
            "Проверяется тело ответа.")
    public void orderWithNoAuthorizationCantBeGet() {
        Response getOrderResponse = orderMethods.getByUserUnauthorized();

        getOrderResponse.then().assertThat()
                .body("success", equalTo(false))
                .body("message", CoreMatchers.equalTo("You should be authorised"))
                .and()
                .statusCode(401);
    }


}
