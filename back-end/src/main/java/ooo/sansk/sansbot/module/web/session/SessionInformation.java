package ooo.sansk.sansbot.module.web.session;

import java.time.Clock;
import java.time.ZonedDateTime;

public class SessionInformation {

    private final String ip;
    private final ZonedDateTime expirationTime;
    private final Clock clock;

    public SessionInformation(String ip, ZonedDateTime expirationTime, Clock clock) {
        this.ip = ip;
        this.expirationTime = expirationTime;
        this.clock = clock;
    }

    public String getIp() {
        return ip;
    }

    public ZonedDateTime getExpirationTime() {
        return expirationTime;
    }

    public boolean isNonExpired() {
        return ZonedDateTime.now(clock).isBefore(expirationTime);
    }

    public boolean isSameIp(String clientIp) {
        return ip.equals(clientIp);
    }
}
