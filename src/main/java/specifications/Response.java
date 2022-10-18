package specifications;

import lombok.Getter;

@Getter
public class Response {
    private final boolean success;
    private final String message;
    private final String accessToken;
    private final int number;

    public Response(boolean success, String message, String accessToken, int number) {
        this.success = success;
        this.message = message;
        this.accessToken = accessToken;
        this.number = number;
    }
}