package ooo.sansk.sansbot.module.movielines;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Line {
    private final String text;
    private final String time;

    @JsonCreator
    public Line(@JsonProperty("sub") String text, @JsonProperty("time") String time) {
        this.text = text;
        this.time = time;
    }

    public String getText() {
        return text;
    }

    public String getTime() {
        return time;
    }
}
