package specifications;

import config.Config;
import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;

import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class Specification {
    public static RequestSpecification requestSpec() {
        return given().log().all()
                .baseUri(Config.BASE_URL)
                .contentType(ContentType.JSON);
    }

    @Step("Получение кода ответа")
    public int getStatusCodeFrom(ValidatableResponse response) {
        return response
                .extract()
                .statusCode();
    }

    @Step("Получение статуса запроса")
    public boolean isSucceedStatementFrom(ValidatableResponse response) {
        return response
                .extract()
                .path("success");
    }

    @Step("Получение сообщения об ошибке")
    public String getMessageFrom(ValidatableResponse response) {
        return response
                .extract()
                .path("message");
    }

    @Step("Получение токена")
    public String getTokenFrom(ValidatableResponse response) {
        return response
                .extract().path("accessToken");
    }

    @Step("Получение номера заказа")
    public int getOrderNumberFrom(ValidatableResponse response) {
        return response
                .extract().path("order.number");
    }

    @Step("Получение списка заказов")
    public List<Map<String, Object>> getUserOrdersFrom(ValidatableResponse response) {
        return response
                .extract().path("orders");
    }
}
