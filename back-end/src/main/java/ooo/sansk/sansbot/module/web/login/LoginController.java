package ooo.sansk.sansbot.module.web.login;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.imine.vaccine.annotation.Component;
import ooo.sansk.sansbot.module.web.util.Controller;
import ooo.sansk.sansbot.module.web.util.Mapping;
import ooo.sansk.sansbot.module.web.util.MappingType;
import spark.Request;
import spark.Response;

import java.util.Optional;

@Component
@Mapping(location = LoginController.BASE_URL)
public class LoginController implements Controller {

    public static final String BASE_URL = "login";

    private final LoginService loginService;
    private final ObjectMapper objectMapper;

    public LoginController(LoginService loginService, ObjectMapper objectMapper) {
        this.loginService = loginService;
        this.objectMapper = objectMapper;
    }

    @Mapping(type = MappingType.POST)
    public String onLogin(Request request, Response response) throws Exception {
        Optional<WebUserDetails> oWebUserDetails = loginService.attemptLogin(request.body());
        if(oWebUserDetails.isPresent()) {
            response.body(objectMapper.writeValueAsString(oWebUserDetails.get()));
        } else {
            response.status(401);
            response.body("Invalid Token");
        }
        return response.body();
    }
}
