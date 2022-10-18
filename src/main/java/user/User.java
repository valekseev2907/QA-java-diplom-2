package user;

import com.github.javafaker.Faker;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class User {

    private String email;
    private String password;
    private String name;

    public User(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }

    static Faker data = new Faker();

    public static User getDatafrom(User user) {
        return new User(user.getEmail(), user.getPassword(), user.getName());
    }

    public static User getRandomUserData() {
        return new User(
                data.internet().emailAddress(), data.internet().password(6, 20), data.funnyName().name()
        );
    }
}
