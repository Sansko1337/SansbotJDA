package ooo.sansk.sansbot.module.web;

import nl.imine.vaccine.annotation.AfterCreate;
import nl.imine.vaccine.annotation.Component;
import nl.imine.vaccine.annotation.Property;
import ooo.sansk.sansbot.module.web.login.LoginController;
import ooo.sansk.sansbot.module.web.session.SessionFilter;
import ooo.sansk.sansbot.module.web.util.Controller;
import ooo.sansk.sansbot.module.web.util.Mapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;
import spark.Spark;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Component
public class SparkConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(SparkConfiguration.class);

    private final int port;
    private final SessionFilter sessionFilter;
    private final List<Controller> controllerList;

    public SparkConfiguration(@Property("web.port") String port,
                              SessionFilter sessionFilter,
                              LoginController loginController,
                              TrackScheduleController trackScheduleController) {
        this.port = Integer.parseInt(port);
        this.sessionFilter = sessionFilter;
        controllerList = new ArrayList<>();
        controllerList.add(loginController);
        controllerList.add(trackScheduleController);
    }

    @AfterCreate
    public void configureSpark() {
        Spark.staticFileLocation("/public");
        Spark.port(port);
        Spark.before(sessionFilter);
        Spark.before((request, response) -> response.header("Access-Control-Allow-Origin", "*"));
        Spark.exception(Exception.class, this::handleException);
        controllerList.forEach(this::registerMappings);
    }


    public void handleException(Exception e, Request request, Response response) {
        if(logger.isErrorEnabled()) {
            logger.error("Error handling request from '{}'. Caused by ({}: {})", request.session().id(), e.getClass().getSimpleName(), e.getMessage());
        }
        response.body("Internal Server Error");
        Spark.halt(500);
    }

    public void registerMappings(Controller controller) {
        String controllerBaseLocation = ensureStringHasLeadingSlashStripTrailingSlash(controller.getClass().getAnnotation(Mapping.class).location());
        Stream.of(controller.getClass().getMethods())
                .filter(method -> method.isAnnotationPresent(Mapping.class))
                .forEach(method -> registerMapping(controller, method, controllerBaseLocation));
    }

    private void registerMapping(Controller controller, Method method, String controllerBaseLocation) {
        Mapping mapping = method.getAnnotation(Mapping.class);
        String mappingLocation = controllerBaseLocation + ensureStringHasLeadingSlashStripTrailingSlash(mapping.location());
        logger.info("Registering mapping [{}] {}", mapping.type(), mappingLocation);
        mapping.type().registerMapping(mappingLocation, (request, response) ->
                method.invoke(controller, request, response)
        );
    }

    private String ensureStringHasLeadingSlashStripTrailingSlash(String string) {
        if (string.isEmpty()) {
            return string;
        }
        return ensureStringHasLeadingSlash(ensureStringHasNoTrailingSlash(string));
    }

    private String ensureStringHasLeadingSlash(String string) {
        if (!string.startsWith("/")) {
            return "/" + string;
        }
        return string;
    }

    private String ensureStringHasNoTrailingSlash(String string) {
        if (string.endsWith("/")) {
            return string.substring(0, string.length() - 1);
        }
        return string;
    }
}
