import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import order.Ingredients;
import order.OrderClient;
import org.junit.Before;
import org.junit.Test;
import specifications.Specification;
import user.User;
import user.UserClient;
import static org.junit.Assert.*;

public class PostOrderTests {
    User user;
    UserClient userClient;
    Ingredients ingredients;
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
    @DisplayName("Попытка создания заказа для зарегистрированного пользователя должна завершиться успешно")
    public void attemptToCreateOrderByAuthorizedUserShouldSucceed() {
        ValidatableResponse registrationResponse = userClient.create(user);
        String token = spec.getTokenFrom(registrationResponse);
        ValidatableResponse orderResponse = orderClient.createOrder(token, ingredients);

        assertEquals("status code is not valid", 200, spec.getStatusCodeFrom(orderResponse));
        assertTrue("expected value - true", spec.isSucceedStatementFrom(orderResponse));
        assertNotEquals(0, spec.getOrderNumberFrom(orderResponse));
    }

    @Test
    @DisplayName("Попытка создания заказа для незарегистрированного пользователя должна завершиться успешно")
    public void attemptToCreateOrderByUnauthorizedUserShouldSucceed() {
        ValidatableResponse orderResponse = orderClient.createOrder("", ingredients);

        assertEquals("status code is not valid", 200, spec.getStatusCodeFrom(orderResponse));
        assertTrue("expected value - true", spec.isSucceedStatementFrom(orderResponse));
        assertNotEquals(0, spec.getOrderNumberFrom(orderResponse));
    }

    @Test
    @DisplayName("Попытка создания заказа без списка ингредиентов должна завершиться ошибкой")
    public void attemptToCreateOrderWithoutIngredientsShouldReturnError() {
        ValidatableResponse registrationResponse = userClient.create(user);
        String token = spec.getTokenFrom(registrationResponse);
        ValidatableResponse orderResponse = orderClient.createOrder(token, Ingredients.getNullIngredients());

        assertEquals("status code is not valid", 400, spec.getStatusCodeFrom(orderResponse));
        assertFalse("expected value - false", spec.isSucceedStatementFrom(orderResponse));
        assertEquals("Ingredient ids must be provided", spec.getMessageFrom(orderResponse));
    }

    @Test
    @DisplayName("Попытка создания заказа с некорректными ингредиентами должна завершиться ошибкой")
    public void attemptToCreateOrderWithIncorrectIngredientsShouldReturnError() {
        ValidatableResponse registrationResponse = userClient.create(user);
        String token = spec.getTokenFrom(registrationResponse);
        ValidatableResponse orderResponse = orderClient.createOrder(token, Ingredients.getIncorrectIngredients());

        assertEquals("status code is not valid", 500, spec.getStatusCodeFrom(orderResponse));
    }
}
