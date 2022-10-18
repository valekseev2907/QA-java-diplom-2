import io.qameta.allure.junit4.DisplayName;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import specifications.Response;
import specifications.Specification;
import user.User;
import user.UserClient;
import user.UserCredentials;

import static org.junit.Assert.*;

public class PostUserTests {

    User user;
    UserClient userClient;

    @Before
    public void setup() {
    user = User.getRandomUserData();
    userClient = new UserClient();
    }

    @Test
    @DisplayName("Попытка создать уникального пользователя должна завершиться успешно")
    public void attemptToCreateUniqueUserShouldSucceed() {
        boolean isSucceed = userClient.create(user)
                .spec(Specification.responseSpecOK200())
                .extract().path("success");
        assertTrue("В параметре ожитается значение - true",isSucceed);
    }

    @Test
    @DisplayName("Попытка создать пользователя, который уже зарегистрирован должна завершиться ошибкой")
    public void attemptToCreateUserWithExistedLoginShouldReturnError() {
        boolean isSucceed = userClient.create(user)
                .spec(Specification.responseSpecOK200())
                .extract().path("success");
        assertTrue("В параметре ожитается значение - true",isSucceed);

        User existedUser = User.getDatafrom(user);
        Response response = userClient.create(existedUser)
                .spec(Specification.responseSpecForbidden403())
                .extract().as(Response.class);
        assertFalse("В параметре ожитается значение - false", response.isSuccess());
        assertEquals("User already exists", response.getMessage());
    }

    @Test
    @DisplayName("Попытка создать пользователя и не заполнить одно из обязательных полей должна завершиться ошибкой")
    public void attemptToCreateUserWithoutMandatoryFieldShouldReturnError() {
        user.setEmail(null);

        Response response = userClient.create(user)
                .spec(Specification.responseSpecForbidden403())
                .extract().as(Response.class);
        assertFalse("В параметре ожитается значение - false", response.isSuccess());
        assertEquals("Email, password and name are required fields", response.getMessage());
    }

    @Test
    @DisplayName("Попытка авторизации под существующим пользователем должна завершиться успешно")
    public void attemptToAuthorizeAsExistedUserShouldSucceed() {
        boolean isSucceed = userClient.create(user)
                .spec(Specification.responseSpecOK200())
                .extract().path("success");
        assertTrue("В параметре ожитается значение - true",isSucceed);

        UserCredentials creds = UserCredentials.from(user);
        boolean response = userClient.login(creds)
                .spec(Specification.responseSpecOK200())
                .extract().path("success");
        assertTrue("В параметре ожитается значение - true", response);
    }

    @Test
    @DisplayName("Попытка авторизации с неверным логином и паролем должна завершиться ошибкой")
    public void attemptToAuthorizeWithInvalidLoginAndPasswordShouldReturnError() {
        boolean isSucceed = userClient.create(user)
                .spec(Specification.responseSpecOK200())
                .extract().path("success");
        assertTrue("В параметре ожитается значение - true",isSucceed);

        UserCredentials creds = UserCredentials.from(user);
        creds.setEmail(RandomStringUtils.randomAlphanumeric(10));
        creds.setPassword(RandomStringUtils.randomAlphanumeric(10));

        Response response = userClient.login(creds)
                .spec(Specification.responseSpecUnauthorized401())
                .extract().as(Response.class);
        assertFalse("В параметре ожитается значение - false", response.isSuccess());
        assertEquals("email or password are incorrect", response.getMessage());
    }
}
