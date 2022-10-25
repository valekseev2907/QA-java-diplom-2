package order;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import specifications.Specification;


public class OrderClient extends Specification {

    private static final String INGRIDIENTS_PATH = "/ingredients";
    private static final String ORDER_PATH = "/orders";

    @Step("Получение списка ингредиентов")
    public static ValidatableResponse getIngredients() {
        //Запрос данных об ингредиентах
        return requestSpec()
                .when()
                .get(INGRIDIENTS_PATH)
                .then()
                .log().all();
    }

    @Step("Создание заказа")
    public ValidatableResponse createOrder (String token, Ingredients ingredients){
        return requestSpec()
                .header("Authorization", token)
                .body(ingredients)
                .when()
                .post(ORDER_PATH)
                .then().log().all();
    }

    @Step("Получение списка заказов пользователя")
    public ValidatableResponse userOrdersList (String token){
        return requestSpec()
                .header("Authorization", token)
                .when()
                .get(ORDER_PATH)
                .then().log().all();
    }
}

