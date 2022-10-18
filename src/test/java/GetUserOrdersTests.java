import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import jdk.jfr.Description;
import order.Ingredients;
import order.OrderClient;
import org.junit.Before;
import org.junit.Test;
import specifications.Response;
import specifications.Specification;
import user.User;
import user.UserClient;
import user.UserCredentials;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class GetUserOrdersTests {
    User user;
    Ingredients ingredients;
    UserClient userClient;
    OrderClient orderClient;

    @Before
    public void setup() {
        user = User.getRandomUserData();
        ingredients = Ingredients.getRandomBurger();
        userClient = new UserClient();
        orderClient = new OrderClient();
    }

    @Test
    @DisplayName("Попытка получения списка заказов авторизованного пользователя должна завершиться успешно")
    public void attemptToGetOrderListOfAuthorizedUserShouldSucceed() {
        Response registrationResponse = userClient.create(user)
                .spec(Specification.responseSpecOK200())
                .extract().as(Response.class);
        assertTrue("В параметре ожитается значение - true",registrationResponse.isSuccess());

        UserCredentials creds = UserCredentials.from(user);
        Response authorizeResponse = userClient.login(creds)
                .spec(Specification.responseSpecOK200())
                .extract().as(Response.class);
        assertTrue("В параметре ожитается значение - true", authorizeResponse.isSuccess());

        ValidatableResponse createOrderResponse = orderClient.createOrder(registrationResponse.getAccessToken(), ingredients)
                .spec(Specification.responseSpecOK200());
        int orderNumber = createOrderResponse.extract().path("order.number");
        boolean isOrderCreated = createOrderResponse.extract().path("success");
        assertTrue("В параметре ожитается значение - true", isOrderCreated);
        assertNotEquals(0, orderNumber);

        ValidatableResponse ordersResponse = orderClient.userOrdersList(authorizeResponse.getAccessToken())
                .spec(Specification.responseSpecOK200());
        List<Map<String, Object>> getUserOrders = ordersResponse.extract().path("orders");
        boolean isOrdersRequestSucceed = ordersResponse.extract().path("success");
        assertTrue("В параметре ожитается значение - true", isOrdersRequestSucceed);
        assertNotEquals(Collections.emptyList(), getUserOrders);
    }

    @Test
    @DisplayName("Попытка получения списка заказов неавторизованного пользователя должна завершиться ошибкой")
    public void attemptToGetOrderListOfUnauthorizedUserShouldReturnError() {
        ValidatableResponse ordersResponse = orderClient.userOrdersList("")
                .spec(Specification.responseSpecUnauthorized401());
        boolean isSucceed = ordersResponse.extract().path("success");
        String message = ordersResponse.extract().path("message");
        assertFalse("В параметре ожитается значение - false", isSucceed);
        assertEquals("You should be authorised", message);
    }
}
