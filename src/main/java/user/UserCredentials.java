package user;

import com.github.javafaker.Faker;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UserCredentials {
    private String email;
    private String password;
    private String name;

    static Faker data = new Faker();

    private static String fakeEmail = data.internet().emailAddress();
    private static String fakePassword = data.internet().password(6, 20);
    private static String fakeName = data.funnyName().name();

    public UserCredentials(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }

    public static UserCredentials from (User user) {
        return new UserCredentials(user.getEmail(), user.getPassword(), user.getName());
    }

    public static void patchEmailFrom (UserCredentials creds) {
        creds.setEmail(fakeEmail);
    }
    public static void patchPasswordFrom (UserCredentials creds) {
        creds.setPassword(fakePassword);
    }
    public static void patchNmaeFrom (UserCredentials creds) {
        creds.setName(fakeName);
    }
}
