package ooo.sansk.sansbot.module.movielines;

import com.fasterxml.jackson.databind.ObjectMapper;
import ooo.sansk.sansbot.SansbotJDA;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class LoadTest {

    @Test
    public void loadFiles() {
        SansbotJDA sansbotJDA = new SansbotJDA("");
        ObjectMapper objectMapper = sansbotJDA.objectMapper();

        try (Stream<Path> list = Files.list(Paths.get("src/main/resources/movies/marvel"))) {
            list.filter(file -> file.toString().endsWith("json")).map(file -> readMovie(objectMapper, file)).forEach(movie -> {
                System.out.printf("%s | %d lines%n", movie.getTitle(), movie.getLines().size());
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private Movie readMovie(ObjectMapper objectMapper, Path file) {
        try {
            return objectMapper.readValue(Files.newInputStream(file), Movie.class);
        } catch (IOException e) {
            Logger.getLogger(LoadTest.class.getSimpleName()).log(Level.WARNING, file.toAbsolutePath().toString() + " failed to load.", e);
        }
        return new Movie("Failed to load", Collections.emptyList());
    }
}
