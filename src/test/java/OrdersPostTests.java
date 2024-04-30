import helper.GenerateRandom;
import ingredients.IngredientsMethods;
import io.qameta.allure.Description;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.response.Response;
import orders.Order;
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

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.IsEqual.equalTo;

public class OrdersPostTests {

    private User user;
    private UserMethods userMethods;
    String accessToken;
    private IngredientsMethods ingredientsMethods;
    private OrderMethods orderMethods;
    List<String> orderIngredientsHashesList;


    @Before
    public void createTestData() {
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
        userMethods = new UserMethods();
        user = UserGenerator.random();
        ingredientsMethods = new IngredientsMethods();
        orderMethods = new OrderMethods();
    }

    @After
    public void reset() {
        if (accessToken != null) {
            userMethods.delete(accessToken);
        }
        RestAssured.reset();
    }

    @Test
    @Description("Проверка возможности создания заказа с ингредиентами для авторизированного пользователя: " +
            "Формируется список ингридиентов для заказа. " +
            "Создатется пользователь. " +
            "В запросе создания заказа передаются список ингредиентов и токен авторизации. " +
            "Проверяется код ответа. " +
            "Проверяется тело ответа.")
    public void orderWithIngredientsWithAuthorizationCanBeCreated() {
        Response createUserResponse = userMethods.create(user);
        accessToken = createUserResponse.path("accessToken");

        getOrderIngredientsHashesList();
        Order order = new Order(orderIngredientsHashesList);

        Response createOrderResponse = orderMethods.createAuthorized(order, accessToken);

        createOrderResponse.then().assertThat()
                .body("success", equalTo(true))
                .body("name", notNullValue())
                .body("order.ingredients._id", hasItem(orderIngredientsHashesList.get(0)))
                .body("order.ingredients._id", hasItem(orderIngredientsHashesList.get(1)))
                .body("order.owner.email", equalTo(user.getEmail()))
                .and()
                .statusCode(200);
    }

    @Test
    @Description("Проверка возможности создания заказа с ингредиентами для неавторизированного пользователя: " +
            "Формируется список ингридиентов для заказа. " +
            "В теле запроса создания заказа передается список ингредиентов. " +
            "Проверяется код ответа. " +
            "Проверяется тело ответа.")
    public void orderWithIngredientsNoAuthorizationCanBeCreated() {
        getOrderIngredientsHashesList();
        Order order = new Order(orderIngredientsHashesList);

        Response createOrderResponse = orderMethods.createUnauthorized(order);

        createOrderResponse.then().assertThat()
                .body("success", equalTo(true))
                .body("name", notNullValue())
                .body("order.number", notNullValue())
                .and()
                .statusCode(200);
    }

    @Test
    @Description("Проверка невозможности создания заказа без ингредиентов для авторизированного пользователя: " +
            "Создатется пользователь. " +
            "В запросе создания заказа передается пустой список ингредиентов и токен авторизации. " +
            "Проверяется код ответа. " +
            "Проверяется тело ответа.")
    public void orderNoIngredientsWithAuthorizationCantBeCreated() {
        Response createUserResponse = userMethods.create(user);
        accessToken = createUserResponse.path("accessToken");

        Order order = new Order(orderIngredientsHashesList);

        Response createOrderResponse = orderMethods.createAuthorized(order, accessToken);

        createOrderResponse.then().assertThat()
                .body("success", equalTo(false))
                .body("message", equalTo("Ingredient ids must be provided"))
                .and()
                .statusCode(400);
    }

    @Test
    @Description("Проверка невозможности создания заказа без ингредиентов для неавторизированного пользователя: " +
            "В запросе создания заказа передается пустой список ингредиентов. " +
            "Проверяется код ответа. " +
            "Проверяется тело ответа.")
    public void orderNoIngredientsNoAuthorizationCantBeCreated() {
        Order order = new Order(orderIngredientsHashesList);

        Response createOrderResponse = orderMethods.createUnauthorized(order);

        createOrderResponse.then().assertThat()
                .body("success", equalTo(false))
                .body("message", equalTo("Ingredient ids must be provided"))
                .and()
                .statusCode(400);
    }

    @Test
    @Description("Проверка невозможности создания заказа с невалидными значениями хэшей ингредиентов: " +
            "В теле запроса создания заказа передается список ингредиентов с невалидными значениями хэшей. " +
            "Проверяется код ответа. ")
    public void orderWithInvalidIngredientsHashCantBeCreated() {
        orderIngredientsHashesList = List.of("InvalidIngredientsHash_1", "InvalidIngredientsHash_2");
        Order order = new Order(orderIngredientsHashesList);

        Response createOrderResponse = orderMethods.createUnauthorized(order);

        createOrderResponse.then().assertThat().statusCode(500);
    }

    @Test
    @Description("Проверка невозможности создания заказа с несуществующими ингредиентами: " +
            "В теле запроса создания заказа передается список ингредиентов с хэшами, которых нет в БД. " +
            "Проверяется код ответа. " +
            "Проверяется тело ответа.")
    public void orderWithNotExistIngredientsHashCantBeCreated() {
        orderIngredientsHashesList = List.of("66c0c3a71d1f82007bdaaa6d");
        Order order = new Order(orderIngredientsHashesList);

        Response createOrderResponse = orderMethods.createUnauthorized(order);

        createOrderResponse.then().assertThat()
                .body("success", equalTo(false))
                .body("message", equalTo("One or more ids provided are incorrect"))
                .and()
                .statusCode(400);
    }

    public void getOrderIngredientsHashesList() {
        List<String> allIngredientsHashesList = ingredientsMethods.getHashes();
        orderIngredientsHashesList = List.of(
                allIngredientsHashesList.get(GenerateRandom.rndInt(allIngredientsHashesList.size())),
                allIngredientsHashesList.get(GenerateRandom.rndInt(allIngredientsHashesList.size())));
    }

}
