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

import java.util.HashSet;
import java.util.Set;

@Component
public class ChatCommandHandler implements EventListener {

    private static final Logger logger = LoggerFactory.getLogger(ChatCommandHandler.class);
    private static final String CONFIG_COMMAND_REPLY_CHANNEL  = "command.channel.output";

    private final JDA jda;
    private final Set<ChatCommand> chatCommands;
    private final String commandPrefix;
    private PersistentProperties applicationOptions;
    private TextChannel defaultOutputChannel;

    public ChatCommandHandler(JDA jda, PersistentProperties applicationOptions, @Property("sansbot.chat.command.prefix") String commandPrefix) {
        this.chatCommands = new HashSet<>();
        this.jda = jda;
        this.commandPrefix = commandPrefix;
        this.applicationOptions = applicationOptions;
        if(applicationOptions.containsKey(CONFIG_COMMAND_REPLY_CHANNEL)) {
            this.defaultOutputChannel = jda.getTextChannelById(String.valueOf(applicationOptions.get(CONFIG_COMMAND_REPLY_CHANNEL)));
            if(!defaultOutputChannel.canTalk()) {
                logger.error("Bot is not allowed to talk in set output channel ({})", defaultOutputChannel.getName());
            }
        } else {
            TextChannel fallbackOutputChannel = jda.getTextChannels().get(0);
            logger.warn("Default bot command output channel not set, defaulting to first available channel (#{})", fallbackOutputChannel.getName());
            this.defaultOutputChannel = fallbackOutputChannel;
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
                for (ChatCommand chatCommand : chatCommands) {
                    if (chatCommand.getTriggers().stream().anyMatch(input.split(" ")[0]::equalsIgnoreCase)) {
                        chatCommand.handle(messageReceivedEvent);
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

    public boolean registerCommand(ChatCommand chatCommand) {
        return chatCommands.add(chatCommand);
    }

}
