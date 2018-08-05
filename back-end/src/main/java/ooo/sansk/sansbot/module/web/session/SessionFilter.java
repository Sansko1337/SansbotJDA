package ooo.sansk.sansbot.module.web.session;

import nl.imine.vaccine.annotation.Component;
import spark.*;

import java.time.ZonedDateTime;

@Component
public class SessionFilter implements Filter {

    public static final String SESSION_INFORMATION = "sessionInformation";

    @Override
    public void handle(Request request, Response response) {
        if (request.pathInfo().equals("/login"))
            return;
        Session session = request.session();
        validateSession(session, request, response);
    }

    private void validateSession(Session session, Request request, Response response) {
        Object sessionInformation = session.attribute(SESSION_INFORMATION);
        if (isSessionValid(request, sessionInformation))
            invalidateSession(session);
        else
            renewSession(session);
    }

    private void invalidateSession(Session session) {
        session.invalidate();
        Spark.halt(401);
    }

    private boolean isSessionValid(Request request, Object sessionInformation) {
        return !(sessionInformation instanceof SessionInformation) || !isSessionNotExpiredAndSameIp((SessionInformation) sessionInformation, request.ip());
    }

    private boolean isSessionNotExpiredAndSameIp(SessionInformation sessionInformation, String clientIp) {
        return sessionInformation.isNonExpired() && sessionInformation.isSameIp(clientIp);
    }

    private void renewSession(Session session) {
        SessionInformation sessionInformation = session.attribute(SESSION_INFORMATION);
        session.attribute(SESSION_INFORMATION, new SessionInformation(sessionInformation.getIp(), createSessionTokenExpirationTime()));
    }

    private ZonedDateTime createSessionTokenExpirationTime() {
        return ZonedDateTime.now().plusHours(2);
    }
}
