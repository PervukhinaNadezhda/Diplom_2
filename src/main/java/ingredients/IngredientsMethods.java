package ingredients;

import io.qameta.allure.Step;

import java.util.List;

import static io.restassured.RestAssured.given;

public class IngredientsMethods {


    private static final String BASE_URL = "https://stellarburgers.nomoreparties.site";
    public static final String INGREDIENTS_PATH = "/api/ingredients";

    @Step("Получить список хэшей всех ингредиентов")
    public List<String> getHashes() {
        List<String> listHash = given()
                .baseUri(BASE_URL)
                .when()
                .get(INGREDIENTS_PATH)
                .then()
                .extract().jsonPath().getList("data._id");
        return listHash;
    }

}
