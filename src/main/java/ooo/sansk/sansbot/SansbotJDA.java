package ooo.sansk.sansbot;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import nl.imine.vaccine.Vaccine;
import nl.imine.vaccine.annotation.AfterCreate;
import nl.imine.vaccine.annotation.Component;
import nl.imine.vaccine.annotation.Property;
import nl.imine.vaccine.annotation.Provided;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.Scanner;

@Component
public class SansbotJDA {

    private static final Logger logger = LoggerFactory.getLogger(SansbotJDA.class);

    private final String botToken;

    public static void main(String[] args) {
        Properties mergedProperties = new Properties();
        try (InputStream inputStream = Files.newInputStream(Paths.get("application.properties"))) {
            Properties internalProperties = new Properties();
            internalProperties.load(ClassLoader.getSystemResource("default.properties").openStream());
            mergedProperties.putAll(internalProperties);
            Properties externalProperties = new Properties();
            externalProperties.load(inputStream);
            mergedProperties.putAll(externalProperties);
        } catch (NoSuchFileException e) {
            createPropertiesFile();
            logger.error("No application.properties file found. Creating new file. Please provide your Bot token in the applications.properties");
            System.exit(0);
        } catch (IOException e) {
            logger.error("Unable to load properties. Exiting. ({}: {})", e.getClass().getSimpleName(), e.getMessage());
            System.exit(1);
        }
        Vaccine vaccine = new Vaccine();
        vaccine.inject(mergedProperties, "ooo.sansk.sansbot");

        listenForStopCommand();
    }

    private static void createPropertiesFile() {
        try {
            Path path = Paths.get("application.properties");
            Files.createFile(path);
            Files.write(path, "sansbot.token=".getBytes());
        } catch (IOException e) {
            logger.error("Could not create properties file. Exiting. ({}: {})", e.getClass().getSimpleName(), e.getMessage());
            System.exit(1);
        }
    }

    public SansbotJDA(@Property("sansbot.token") String botToken) {
        this.botToken = botToken;
    }

    @AfterCreate
    public void onPostConstruct() {
        logger.info("Loading Sansbot");
    }

    @Provided
    public JDA jda() {
        try {
            return new JDABuilder(AccountType.BOT).setToken(botToken).buildBlocking();
        } catch (InterruptedException | LoginException e) {
            System.out.println("Failed to create JDA");
            System.exit(1);
            return null;
        }
    }

    private static void listenForStopCommand() {
        Scanner scanner = new Scanner(System.in);
        while(scanner.hasNext()) {
            if(scanner.next().equals("stop")) {
                System.exit(0);
            }
        }
    }
}
