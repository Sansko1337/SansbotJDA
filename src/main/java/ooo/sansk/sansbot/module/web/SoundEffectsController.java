package ooo.sansk.sansbot.module.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.imine.vaccine.annotation.Component;
import ooo.sansk.sansbot.module.music.TrackListManager;
import ooo.sansk.sansbot.module.web.util.Controller;
import ooo.sansk.sansbot.module.web.util.Mapping;
import ooo.sansk.sansbot.module.web.util.MappingType;
import org.json.JSONObject;
import spark.Request;
import spark.Response;

@Component
@Mapping(location = SoundEffectsController.BASE_URL)
public class SoundEffectsController implements Controller {

    public static final String BASE_URL = "/api/music";

    private final ObjectMapper objectMapper;
    private final TrackListManager trackListManager;

    public SoundEffectsController(ObjectMapper objectMapper, TrackListManager trackListManager) {
        this.objectMapper = objectMapper;
        this.trackListManager = trackListManager;
    }

    @Mapping()
    public String getCurrentQueue(Request request, Response response) throws Exception {
        response.body(objectMapper.writeValueAsString(trackListManager.getQueue()));
        return response.body();
    }

    @Mapping(type = MappingType.POST)
    private String postTrack(Request request, Response response) {
        String url = new JSONObject(request.body()).getString("url");
        trackListManager.loadTrack(url);
        response.body("Added track " + url);
        return response.body();
    }
}
