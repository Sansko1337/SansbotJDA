package ooo.sansk.sansbot.module.web.session;

import java.time.ZonedDateTime;

public class SessionInformation {

    private final String ip;
    private final ZonedDateTime expirationTime;

    public SessionInformation(String ip, ZonedDateTime expirationTime) {
        this.ip = ip;
        this.expirationTime = expirationTime;
    }

    public String getIp() {
        return ip;
    }

    public ZonedDateTime getExpirationTime() {
        return expirationTime;
    }

    public boolean isNonExpired() {
        return ZonedDateTime.now().isBefore(expirationTime);
    }

    public boolean isSameIp(String clientIp) {
        return ip.equals(clientIp);
    }
}
