package ooo.sansk.sansbot.options;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Properties;

public class PersistentProperties extends Properties {

    private static final Logger logger = LoggerFactory.getLogger(PersistentProperties.class);

    private final transient Path persistentPropertiesPath;

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

    @Override
    public synchronized boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        PersistentProperties that = (PersistentProperties) o;
        return persistentPropertiesPath.equals(that.persistentPropertiesPath);
    }

    @Override
    public synchronized int hashCode() {
        return Objects.hash(super.hashCode(), persistentPropertiesPath);
    }
}
