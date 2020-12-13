package ooo.sansk.sansbot.module.movielines;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Objects;

public class Movie {
    private final String title;
    private final List<Line> lines;

    @JsonCreator
    public Movie(@JsonProperty("title") String title, @JsonProperty("lines") List<Line> lines) {
        this.title = title;
        this.lines = lines;
    }

    public String getTitle() {
        return title;
    }

    public List<Line> getLines() {
        return lines;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Movie movie = (Movie) o;
        return title.equals(movie.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title);
    }
}
