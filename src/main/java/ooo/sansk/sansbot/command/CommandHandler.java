package ooo.sansk.sansbot.command;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.EventListener;
import nl.imine.vaccine.annotation.AfterCreate;
import nl.imine.vaccine.annotation.Component;
import nl.imine.vaccine.annotation.Property;
import ooo.sansk.sansbot.options.PersistentProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.channels.Channel;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

@Component
public class CommandHandler implements EventListener {

    private static final Logger logger = LoggerFactory.getLogger(CommandHandler.class);
    private static final String CONFIG_COMMAND_REPLY_CHANNEL  = "command.channel.output";

    private final JDA jda;
    private final Set<Command> commands;
    private final String commandPrefix;
    private PersistentProperties applicationOptions;
    private TextChannel defaultOutputChannel;

    public CommandHandler(JDA jda, PersistentProperties applicationOptions, @Property("sansbot.command.prefix") String commandPrefix) {
        this.commands = new HashSet<>();
        this.jda = jda;
        this.commandPrefix = commandPrefix;
        this.applicationOptions = applicationOptions;
        if(applicationOptions.containsKey(CONFIG_COMMAND_REPLY_CHANNEL)) {
            this.defaultOutputChannel = jda.getTextChannelById(String.valueOf(applicationOptions.get(CONFIG_COMMAND_REPLY_CHANNEL)));
            if(!defaultOutputChannel.canTalk()) {
                logger.error("Bot is not allowed to talk in set output channel ({})", defaultOutputChannel.getName());
            }
        } else {
            TextChannel defaultOutputChannel = jda.getTextChannels().get(0);
            logger.warn("Default bot command output channel not set, defaulting to first available channel (#{})", defaultOutputChannel.getName());
            this.defaultOutputChannel = defaultOutputChannel;
        }
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

    public TextChannel getDefaultOutputChannel() {
        return defaultOutputChannel;
    }

    public void setDefaultOutputChannel(TextChannel defaultOutputChannel) {
        this.defaultOutputChannel = defaultOutputChannel;
        applicationOptions.setProperty(CONFIG_COMMAND_REPLY_CHANNEL, String.valueOf(defaultOutputChannel.getIdLong()));
        applicationOptions.save();
    }

    public boolean registerCommand(Command command) {
        return commands.add(command);
    }

}
