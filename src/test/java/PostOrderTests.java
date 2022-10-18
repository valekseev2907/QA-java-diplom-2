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
import static org.junit.Assert.*;

public class PostOrderTests {
    User user;
    UserClient userClient;
    Ingredients ingredients;
    OrderClient orderClient;

    @Before
    public void setup() {
        user = User.getRandomUserData();
        ingredients = Ingredients.getRandomBurger();
        userClient = new UserClient();
        orderClient = new OrderClient();
    }

    @Test
    @DisplayName("Попытка созданиязаказа для зарегистрированного пользователя должна завершиться успешно")
    public void attemptToCreateOrderByAuthorizedUserShouldSucceed() {
        Response registrationResponse = userClient.create(user)
                .spec(Specification.responseSpecOK200())
                .extract().as(Response.class);
        assertTrue("В параметре ожитается значение - true",registrationResponse.isSuccess());

        ValidatableResponse orderResponse = orderClient.createOrder(registrationResponse.getAccessToken(), ingredients)
                .spec(Specification.responseSpecOK200());
        int orderNumber = orderResponse.extract().path("order.number");
        boolean isSucceed = orderResponse.extract().path("success");
        assertTrue("В параметре ожитается значение - true", isSucceed);
        assertNotEquals(0, orderNumber);
    }

    @Test
    @DisplayName("Попытка создания заказа для незарегистрированного пользователя должна завершиться успешно")
    public void attemptToCreateOrderByUnauthorizedUserShouldSucceed() {
        ValidatableResponse orderResponse = orderClient.createOrder("", ingredients)
                .spec(Specification.responseSpecOK200());
        int orderNumber = orderResponse.extract().path("order.number");
        boolean isSucceed = orderResponse.extract().path("success");
        assertTrue("В параметре ожитается значение - true", isSucceed);
        assertNotEquals(0, orderNumber);
    }

    @Test
    @DisplayName("Попытка создания заказа без списка ингредиентов должна завершиться ошибкой")
    public void attemptToCreateOrderWithoutIngredientsShouldReturnError() {
        Response registrationResponse = userClient.create(user)
                .spec(Specification.responseSpecOK200())
                .extract().as(Response.class);
        assertTrue("В параметре ожитается значение - true",registrationResponse.isSuccess());

        ValidatableResponse orderResponse = orderClient.createOrder(registrationResponse.getAccessToken(), Ingredients.getNullIngredients())
                .spec(Specification.responseSpecBadRequest400());
        String message = orderResponse.extract().path("message");
        boolean isSucceed = orderResponse.extract().path("success");
        assertFalse("В параметре ожитается значение - false", isSucceed);
        assertEquals("Ingredient ids must be provided", message);
    }

    @Test
    @DisplayName("Попытка создания заказа с некорректными ингредиентами должна завершиться ошибкой")
    public void attemptToCreateOrderWithIncorrectIngredientsShouldReturnError() {
        Response registrationResponse = userClient.create(user)
                .spec(Specification.responseSpecOK200())
                .extract().as(Response.class);
        assertTrue("В параметре ожитается значение - true",registrationResponse.isSuccess());

        orderClient.createOrder(registrationResponse.getAccessToken(), Ingredients.getIncorrectIngredients())
                .spec(Specification.responseSpecInternalServerError500());
    }
}
