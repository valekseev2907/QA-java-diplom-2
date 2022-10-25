package user;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import specifications.Specification;


public class UserClient extends Specification {

    private final String ROOT = "/auth/user";
    private final String CREATE = "/auth/register";
    private final String LOGIN = "/auth/login";

    @Step("Создание пользователя")
    public ValidatableResponse create(User user) {
        return requestSpec()
                .body(user)
                .when()
                .post(CREATE)
                .then().log().all();
    }

    @Step("Изменение данных авторизованного пользователя")
    public ValidatableResponse patchWithAuth(UserCredentials creds, String token) {
        return requestSpec()
                .header("Authorization", token)
                .body(creds)
                .when()
                .patch(ROOT)
                .then().log().all();
    }

    @Step("Изменение данных неавторизованного пользователя")
    public ValidatableResponse patchWithoutAuth(UserCredentials creds) {
        return requestSpec()
                .body(creds)
                .when()
                .patch(ROOT)
                .then().log().all();
    }

    @Step("Авторизация пользователя")
    public ValidatableResponse login(UserCredentials creds) {
        return requestSpec()
                .body(creds)
                .when()
                .post(LOGIN)
                .then().log().all();
    }
}
