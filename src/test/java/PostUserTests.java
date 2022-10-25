import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import specifications.Specification;
import user.User;
import user.UserClient;
import user.UserCredentials;

import static org.junit.Assert.*;

public class PostUserTests {

    User user;
    UserClient userClient;
    Specification spec;

    @Before
    public void setup() {
    user = User.getRandomUserData();
    userClient = new UserClient();
    spec = new Specification();
    }

    @Test
    @DisplayName("Попытка создать уникального пользователя должна завершиться успешно")
    public void attemptToCreateUniqueUserShouldSucceed() {
        ValidatableResponse response = userClient.create(user);

        assertEquals("status code is not valid", 200, spec.getStatusCodeFrom(response));
        assertTrue("expected value - true", spec.isSucceedStatementFrom(response));
    }

    @Test
    @DisplayName("Попытка создать пользователя, который уже зарегистрирован должна завершиться ошибкой")
    public void attemptToCreateUserWithExistedLoginShouldReturnError() {
        userClient.create(user);
        User existedUser = User.getDataFrom(user);
        ValidatableResponse response = userClient.create(existedUser);

        assertEquals("status code is not valid", 403, spec.getStatusCodeFrom(response));
        assertFalse("expected value - false", spec.isSucceedStatementFrom(response));
        assertEquals("User already exists", spec.getMessageFrom(response));
    }

    @Test
    @DisplayName("Попытка создать пользователя и не заполнить одно из обязательных полей должна завершиться ошибкой")
    public void attemptToCreateUserWithoutMandatoryFieldShouldReturnError() {
        user.setEmail(null);
        ValidatableResponse response = userClient.create(user);

        assertEquals("status code is not valid", 403, spec.getStatusCodeFrom(response));
        assertFalse("expected value - false", spec.isSucceedStatementFrom(response));
        assertEquals("Email, password and name are required fields", spec.getMessageFrom(response));
    }

    @Test
    @DisplayName("Попытка авторизации под существующим пользователем должна завершиться успешно")
    public void attemptToAuthorizeAsExistedUserShouldSucceed() {
        userClient.create(user);
        UserCredentials creds = UserCredentials.from(user);
        ValidatableResponse response = userClient.login(creds);

        assertEquals("status code is not valid", 200, spec.getStatusCodeFrom(response));
        assertTrue("expected value - true", spec.isSucceedStatementFrom(response));
    }

    @Test
    @DisplayName("Попытка авторизации с неверным логином и паролем должна завершиться ошибкой")
    public void attemptToAuthorizeWithInvalidLoginAndPasswordShouldReturnError() {
        userClient.create(user);
        UserCredentials creds = UserCredentials.from(user);
        creds.setEmail(RandomStringUtils.randomAlphanumeric(10));
        creds.setPassword(RandomStringUtils.randomAlphanumeric(10));
        ValidatableResponse response = userClient.login(creds);

        assertEquals("status code is not valid", 401, spec.getStatusCodeFrom(response));
        assertFalse("expected value - false", spec.isSucceedStatementFrom(response));
        assertEquals("email or password are incorrect", spec.getMessageFrom(response));
    }
}
