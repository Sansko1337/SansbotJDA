package ooo.sansk.sansbot.command;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.EventListener;
import nl.imine.vaccine.annotation.AfterCreate;
import nl.imine.vaccine.annotation.Component;
import nl.imine.vaccine.annotation.Property;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

@Component
public class CommandHandler implements EventListener {

    private static final Logger logger = LoggerFactory.getLogger(CommandHandler.class);

    private final JDA jda;
    private final Set<Command> commands;
    private final String commandPrefix;


    public CommandHandler(JDA jda, @Property("sansbot.command.prefix") String commandPrefix) {
        this.commands = new HashSet<>();
        this.jda = jda;
        this.commandPrefix = commandPrefix;
    }

    @AfterCreate
    public void postConstruct() {
        logger.info("Registering {}", this.getClass().getSimpleName());
        jda.addEventListener(this);
    }

    @Override
    public void onEvent(Event event) {
        if (event instanceof MessageReceivedEvent) {
            MessageReceivedEvent messageReceivedEvent = (MessageReceivedEvent) event;
            String contentRaw = messageReceivedEvent.getMessage().getContentRaw();
            if (contentRaw.startsWith(commandPrefix)) {
                String input = contentRaw.substring(1);
                for (Command command : commands) {
                    if (command.getTriggers().stream().anyMatch(trigger -> input.split(" ")[0].equalsIgnoreCase(trigger))) {
                        command.handle(messageReceivedEvent);
                        return;
                    }
                }
                messageReceivedEvent.getChannel()
                        .sendMessage(String.format("%s, uuhhh... Nee ik denk niet dat ik begrijp wat je van mij wilt...",
                                messageReceivedEvent.getAuthor().getAsMention()))
                        .submit();
                messageReceivedEvent.getMessage().delete().submit();
                logger.info("Member ({}) attempted to run unknown command: {}",
                        messageReceivedEvent.getAuthor().getName(),
                        contentRaw
                );
            }
        }
    }

    public boolean registerCommand(Command command) {
        return commands.add(command);
    }

}
