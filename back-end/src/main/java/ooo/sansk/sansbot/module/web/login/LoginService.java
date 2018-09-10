package ooo.sansk.sansbot.module.web.login;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.User;
import nl.imine.vaccine.annotation.Component;
import ooo.sansk.sansbot.module.web.session.SessionFilter;
import ooo.sansk.sansbot.module.web.session.SessionInformation;
import spark.Request;

import java.security.SecureRandom;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class LoginService {

    private final JDA jda;
    private final List<WebToken> requestedTokens;
    private final SecureRandom secureRandom;

    public LoginService(JDA jda) {
        this.jda = jda;
        this.requestedTokens = new ArrayList<>();
        this.secureRandom = new SecureRandom();
    }

    public Optional<WebUserDetails> attemptLogin(Request request, String loginToken) {
        Optional<WebUserDetails> userDetails = requestedTokens.stream()
                .filter(webToken -> webToken.getToken().equals(loginToken))
                .filter(webToken -> webToken.getExpirationTime().isAfter(ZonedDateTime.now()))
                .map(this::createUserDetails)
                .findFirst();
        userDetails.ifPresent(webUserDetails -> request.session().attribute(SessionFilter.SESSION_INFORMATION, createSessionInformation(request.ip())));
        return userDetails;
    }

    private WebUserDetails createUserDetails(WebToken webToken) {
        User userDetails = jda.getUserById(webToken.getUserId());
        return new WebUserDetails(userDetails.getName(), userDetails.getAvatarUrl());
    }

    public WebToken createWebToken(String id) {
        requestedTokens.removeIf(webToken -> webToken.getUserId().equals(id));
        WebToken newToken = new WebToken(createLoginTokenExpirationTime(), id, generateLoginToken());
        requestedTokens.add(newToken);
        return newToken;
    }

    private String generateLoginToken() {
        return String.format("%6d", (secureRandom.nextInt(999999) + 1)).replace(' ', '0');
    }

    private ZonedDateTime createLoginTokenExpirationTime() {
        return ZonedDateTime.now().plusMinutes(15);
    }

    private SessionInformation createSessionInformation(String ip) {
        return new SessionInformation(ip, createSessionTokenExpirationTime());
    }

    private ZonedDateTime createSessionTokenExpirationTime() {
        return ZonedDateTime.now().plusHours(2);
    }
}
