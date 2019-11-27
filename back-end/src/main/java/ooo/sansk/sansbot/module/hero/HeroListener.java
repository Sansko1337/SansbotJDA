package ooo.sansk.sansbot.module.hero;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.EventListener;
import nl.imine.vaccine.annotation.AfterCreate;
import nl.imine.vaccine.annotation.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ThreadLocalRandom;

@Component
public class HeroListener implements EventListener {

    private static final String[] lines = {
            "De stad is in gevaar! Red ons '%s' man!",
            "\"Is het een vogel?!\"\n\"Is het een vliegtuig?\"\n\"Nee! Het is '%s' man!\"",
            "Waar zouden we toch zijn zonder onze lokale held '%s' man"
    };

    private static final Logger logger = LoggerFactory.getLogger(HeroListener.class);
    private static final char NON_BREAKING_SPACE = '\u200C';

    private final JDA jda;

    public HeroListener(JDA jda) {
        this.jda = jda;
    }

    @AfterCreate
    public void postConstruct() {
        logger.info("Registering {}", this.getClass().getSimpleName());
        jda.addEventListener(this);
    }

    @Override
    public void onEvent(Event event) {
        if (!(event instanceof MessageReceivedEvent)) {
            return;
        }
        if(((MessageReceivedEvent) event).getAuthor().getIdLong() == jda.getSelfUser().getIdLong()) {
            return;
        }
        String content = ((MessageReceivedEvent) event).getMessage().getContentStripped();
        String heroName = findHeroName(content);
        if(heroName == null) {
            return;
        }
        heroName = cleanHeroName(heroName);
        String sentence = lines[ThreadLocalRandom.current().nextInt(lines.length)];
        ((MessageReceivedEvent) event).getChannel().sendMessage(String.format(sentence, heroName)).submit();
    }

    public String findHeroName(String text) {
        if (!text.toLowerCase().contains("man")) {
            return null;
        }
        int manIndex = text.toLowerCase().lastIndexOf("man");
        int startIndex = findLastStartOfSentence(text, manIndex);

        String heroName = text.substring(startIndex, manIndex);
        if (heroName.isBlank()) {
            return null;
        }
        heroName = heroName.stripTrailing();
        heroName = heroName.stripLeading();
        return heroName;
    }

    private String cleanHeroName(String heroName) {
        String ret = heroName;
        ret = ret.replace("@", "@" + NON_BREAKING_SPACE);
        ret = ret.replace("http", "http" + NON_BREAKING_SPACE);
        return ret;
    }

    private int findLastStartOfSentence(String content, int manIndex) {
        int start = 0;
        start = Math.max(start, content.lastIndexOf(". ", manIndex));
        start = Math.max(start, content.lastIndexOf("! ", manIndex));
        start = Math.max(start, content.lastIndexOf("? ", manIndex));
        //Found start of sentence, remove 2 chars from string as they'll contain a sentence terminator ( . | ! | ? )
        if (start != 0) {
            start = start + 2;
        }
        return start;
    }
}
