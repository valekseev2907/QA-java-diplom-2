import io.qameta.allure.junit4.DisplayName;
import jdk.jfr.Description;
import org.junit.Before;
import org.junit.Test;
import specifications.Response;
import specifications.Specification;
import user.User;
import user.UserClient;
import user.UserCredentials;

import static org.junit.Assert.*;

public class PatchAuthorizeTests {
    User user;
    UserClient userClient;

    @Before
    public void setup() {
        user = User.getRandomUserData();
        userClient = new UserClient();
    }

    @Test
    @DisplayName("Попытка изменить адрес эл. почты авторизованного пользователя должна завершиться успешно")
    public void attemptToPatchUserEmailShouldSucceed() {
        Response response = userClient.create(user)
                .spec(Specification.responseSpecOK200())
                .extract().as(Response.class);
        assertTrue("В параметре ожитается значение - true", response.isSuccess());

        UserCredentials creds = UserCredentials.from(user);
        UserCredentials.patchEmailFrom(creds);

        boolean isSucceed = userClient.patchWithAuth(creds, response.getAccessToken())
                .spec(Specification.responseSpecOK200())
                .extract().path("success");
        assertTrue("В параметре ожитается значение - true", isSucceed);
    }

    @Test
    @DisplayName("Попытка изменить пароль авторизованного пользователя должна завершиться успешно")
    public void attemptToPatchUserPasswordShouldSucceed() {
        Response response = userClient.create(user)
                .spec(Specification.responseSpecOK200())
                .extract().as(Response.class);
        assertTrue("В параметре ожитается значение - true", response.isSuccess());

        UserCredentials creds = UserCredentials.from(user);
        UserCredentials.patchPasswordFrom(creds);

        boolean isSucceed = userClient.patchWithAuth(creds, response.getAccessToken())
                .spec(Specification.responseSpecOK200())
                .extract().path("success");
        assertTrue("В параметре ожитается значение - true", isSucceed);
    }

    @Test
    @DisplayName("Попытка изменить name авторизованного пользователя должна завершиться успешно")
    public void attemptToPatchUserNameShouldSucceed() {
        Response response = userClient.create(user)
                .spec(Specification.responseSpecOK200())
                .extract().as(Response.class);
        assertTrue("В параметре ожитается значение - true", response.isSuccess());

        UserCredentials creds = UserCredentials.from(user);
        UserCredentials.patchNmaeFrom(creds);

        boolean isSucceed = userClient.patchWithAuth(creds, response.getAccessToken())
                .spec(Specification.responseSpecOK200())
                .extract().path("success");
        assertTrue("В параметре ожитается значение - true", isSucceed);
    }

    @Test
    @DisplayName("Попытка изменить данные неавторизованного пользователя должна завершиться ошибкой")
    public void attemptToPatchUserDataWithoutAuthorizeShouldReturnError() {
        Response registrationResponse = userClient.create(user)
                .spec(Specification.responseSpecOK200())
                .extract().as(Response.class);
        assertTrue("В параметре ожитается значение - true", registrationResponse.isSuccess());

        UserCredentials creds = UserCredentials.from(user);
        UserCredentials.patchNmaeFrom(creds);

        Response patchUserDataResponse = userClient.patchWithoutAuth(creds)
                .spec(Specification.responseSpecUnauthorized401())
                .extract().as(Response.class);
        assertFalse("В параметре ожитается значение - true", patchUserDataResponse.isSuccess());
        assertEquals("You should be authorised", patchUserDataResponse.getMessage());
    }
}