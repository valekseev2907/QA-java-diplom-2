import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import order.Ingredients;
import order.OrderClient;
import org.junit.Before;
import org.junit.Test;
import specifications.Specification;
import user.User;
import user.UserClient;
import user.UserCredentials;

import java.util.Collections;

import static org.junit.Assert.*;

public class GetUserOrdersTests {
    User user;
    Ingredients ingredients;
    UserClient userClient;
    OrderClient orderClient;
    Specification spec;

    @Before
    public void setup() {
        user = User.getRandomUserData();
        ingredients = Ingredients.getRandomBurger();
        userClient = new UserClient();
        orderClient = new OrderClient();
        spec = new Specification();
    }

    @Test
    @DisplayName("Попытка получения списка заказов авторизованного пользователя должна завершиться успешно")
    public void attemptToGetOrderListOfAuthorizedUserShouldSucceed() {
        ValidatableResponse registrationResponse = userClient.create(user);
        UserCredentials creds = UserCredentials.from(user);
        userClient.login(creds);
        String token = spec.getTokenFrom(registrationResponse);
        orderClient.createOrder(token, ingredients);
        ValidatableResponse ordersResponse = orderClient.userOrdersList(token);

        assertEquals("status code is not valid", 200, spec.getStatusCodeFrom(ordersResponse));
        assertTrue("expected value - true", spec.isSucceedStatementFrom(ordersResponse));
        assertNotEquals(Collections.emptyList(), spec.getUserOrdersFrom(ordersResponse));
    }

    @Test
    @DisplayName("Попытка получения списка заказов неавторизованного пользователя должна завершиться ошибкой")
    public void attemptToGetOrderListOfUnauthorizedUserShouldReturnError() {
        ValidatableResponse ordersResponse = orderClient.userOrdersList("");
        assertEquals("status code is not valid", 401, spec.getStatusCodeFrom(ordersResponse));
        assertFalse("expected value - false", spec.isSucceedStatementFrom(ordersResponse));
        assertEquals("You should be authorised", spec.getMessageFrom(ordersResponse));
    }
}
