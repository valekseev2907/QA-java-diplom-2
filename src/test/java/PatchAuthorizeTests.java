import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.Before;
import org.junit.Test;
import specifications.Specification;
import user.User;
import user.UserClient;
import user.UserCredentials;

import static org.junit.Assert.*;

public class PatchAuthorizeTests {
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
    @DisplayName("Попытка изменить адрес эл. почты авторизованного пользователя должна завершиться успешно")
    public void attemptToPatchUserEmailShouldSucceed() {
        ValidatableResponse registrationResponse = userClient.create(user);
        UserCredentials creds = UserCredentials.from(user);
        UserCredentials.patchEmailFrom(creds);
        String token = spec.getTokenFrom(registrationResponse);
        ValidatableResponse patchResponse = userClient.patchWithAuth(creds, token);

        assertEquals("status code is not valid", 200, spec.getStatusCodeFrom(patchResponse));
        assertTrue("expected value - true", spec.isSucceedStatementFrom(patchResponse));
    }

    @Test
    @DisplayName("Попытка изменить пароль авторизованного пользователя должна завершиться успешно")
    public void attemptToPatchUserPasswordShouldSucceed() {
        ValidatableResponse registrationResponse = userClient.create(user);
        UserCredentials creds = UserCredentials.from(user);
        UserCredentials.patchPasswordFrom(creds);
        String token = spec.getTokenFrom(registrationResponse);
        ValidatableResponse patchResponse  = userClient.patchWithAuth(creds, token);

        assertEquals("status code is not valid", 200, spec.getStatusCodeFrom(patchResponse));
        assertTrue("expected value - true", spec.isSucceedStatementFrom(patchResponse));
    }

    @Test
    @DisplayName("Попытка изменить name авторизованного пользователя должна завершиться успешно")
    public void attemptToPatchUserNameShouldSucceed() {
        ValidatableResponse registrationResponse = userClient.create(user);
        UserCredentials creds = UserCredentials.from(user);
        UserCredentials.patchNmaeFrom(creds);
        String token = spec.getTokenFrom(registrationResponse);
        ValidatableResponse patchResponse  = userClient.patchWithAuth(creds, token);

        assertEquals("status code is not valid", 200, spec.getStatusCodeFrom(patchResponse));
        assertTrue("expected value - true", spec.isSucceedStatementFrom(patchResponse));
    }

    @Test
    @DisplayName("Попытка изменить данные неавторизованного пользователя должна завершиться ошибкой")
    public void attemptToPatchUserDataWithoutAuthorizeShouldReturnError() {
        userClient.create(user);
        UserCredentials creds = UserCredentials.from(user);
        UserCredentials.patchNmaeFrom(creds);
        ValidatableResponse patchResponse = userClient.patchWithoutAuth(creds);

        assertEquals("status code is not valid", 401, spec.getStatusCodeFrom(patchResponse));
        assertFalse("expected value - false", spec.isSucceedStatementFrom(patchResponse));
        assertEquals("You should be authorised", spec.getMessageFrom(patchResponse));
    }
}