package ooo.sansk.sansbot.listener;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.EventListener;
import nl.imine.vaccine.annotation.AfterCreate;
import nl.imine.vaccine.annotation.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Component
public class MessageListener implements EventListener {

    private static final Logger logger = LoggerFactory.getLogger(MessageListener.class);

    private final JDA jda;

    public MessageListener(JDA jda) {
        this.jda = jda;
    }

    @AfterCreate
    public void postConstruct() {
        logger.info("Registering {}", this.getClass().getSimpleName());
        jda.addEventListener(this);
    }

    @Override
    public void onEvent(Event event) {
        if(event instanceof MessageReceivedEvent) {
            MessageReceivedEvent messageReceivedEvent = (MessageReceivedEvent) event;
            logger.info("{} sent: \"{}\"", messageReceivedEvent.getAuthor().getName(), messageReceivedEvent.getMessage().getContentDisplay());
            if(((MessageReceivedEvent) event).getMessage().getContentStripped().equals("Zeg kan jij een koprol voor ons doen?")) {
                ((MessageReceivedEvent) event).getChannel().sendMessage("Wat denk je dat ik ben, een verdraaide acrobaat?!").submit();
            }
        }
    }

}
