package ooo.sansk.sansbot.voice;

import edu.cmu.sphinx.api.Configuration;
import nl.imine.vaccine.annotation.Component;
import nl.imine.vaccine.annotation.Property;
import nl.imine.vaccine.annotation.Provided;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class SphinxConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(SphinxConfiguration.class);

    private final String acousticModelPath;
    private final String dictionaryPath;
    private final String languageModelPath;

    public SphinxConfiguration(@Property("sphinx.path.acousticModel") String acousticModelPath, @Property("sphinx.path.dictionary") String dictionaryPath, @Property("sphinx.path.languageModel") String languageModelPath) {
        this.acousticModelPath = acousticModelPath;
        this.dictionaryPath = dictionaryPath;
        this.languageModelPath = languageModelPath;
    }

    @Provided
    public Configuration recognizerConfiguration() {
        Configuration configuration = new Configuration();
        configuration.setAcousticModelPath(acousticModelPath);
        configuration.setDictionaryPath(dictionaryPath);
        configuration.setLanguageModelPath(languageModelPath);
        return configuration;
    }
}
