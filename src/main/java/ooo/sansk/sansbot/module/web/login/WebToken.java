package ooo.sansk.sansbot.module.web.login;

import java.time.ZonedDateTime;

public class WebToken {

    private final ZonedDateTime expirationTime;
    private final String userId;
    private final String token;

    public WebToken(ZonedDateTime expirationTime, String userId, String token) {
        this.expirationTime = expirationTime;
        this.userId = userId;
        this.token = token;
    }

    public ZonedDateTime getExpirationTime() {
        return expirationTime;
    }

    public String getUserId() {
        return userId;
    }

    public String getToken() {
        return token;
    }
}
