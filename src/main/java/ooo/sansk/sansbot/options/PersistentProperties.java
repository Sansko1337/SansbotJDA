package ooo.sansk.sansbot.options;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class PersistentProperties extends Properties {

    private static final Logger logger = LoggerFactory.getLogger(PersistentProperties.class);

    private Path persistentPropertiesPath;

    public PersistentProperties(Path persistentPropertiesPath) {
        this.persistentPropertiesPath = persistentPropertiesPath;
    }

    public void save() {
        try (OutputStream out = Files.newOutputStream(persistentPropertiesPath)){
            store(out, "");
        } catch (IOException e) {
            logger.error("Exception while writing properties to disk: ({}: {})", e.getClass().getSimpleName(), e.getMessage());
        }
    }
}
