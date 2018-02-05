package ooo.sansk.sansbot.voice.command;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;
import nl.imine.vaccine.annotation.AfterCreate;
import nl.imine.vaccine.annotation.Component;
import nl.imine.vaccine.annotation.Property;
import ooo.sansk.sansbot.options.PersistentProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

@Component
public class VoiceCommandHandler {

    private static final Logger logger = LoggerFactory.getLogger(VoiceCommandHandler.class);
    private static final String CONFIG_COMMAND_REPLY_CHANNEL = "command.channel.output";

    private final JDA jda;
    private final Set<VoiceCommand> voiceCommands;
    private final String commandPrefix;
    private PersistentProperties applicationOptions;
    private TextChannel defaultOutputChannel;

    public VoiceCommandHandler(JDA jda, PersistentProperties applicationOptions, @Property("sansbot.voice.command.prefix") String commandPrefix) {
        this.voiceCommands = new HashSet<>();
        this.jda = jda;
        this.commandPrefix = commandPrefix;
        this.applicationOptions = applicationOptions;
        if (applicationOptions.containsKey(CONFIG_COMMAND_REPLY_CHANNEL)) {
            this.defaultOutputChannel = jda.getTextChannelById(String.valueOf(applicationOptions.get(CONFIG_COMMAND_REPLY_CHANNEL)));
            if (!defaultOutputChannel.canTalk()) {
                logger.error("Bot is not allowed to talk in set output channel ({})", defaultOutputChannel.getName());
            }
        } else {
            TextChannel defaultOutputChannel = jda.getTextChannels().get(0);
            logger.warn("Default bot command output channel not set, defaulting to first available channel (#{})", defaultOutputChannel.getName());
            this.defaultOutputChannel = defaultOutputChannel;
        }
    }

    public void onVoiceCommand(Member member, String command) {
        if (command.startsWith(commandPrefix)) {
            String[] split = command.split(commandPrefix + " ", 2);
            if(split.length > 1) {
                String input = split[1];
                for (VoiceCommand voiceCommand : voiceCommands) {
                    if (voiceCommand.getTriggers().stream().anyMatch(trigger -> input.split(" ")[0].equalsIgnoreCase(trigger))) {
                        voiceCommand.handle(member);
                        return;
                    }
                }
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

    public boolean registerCommand(VoiceCommand voiceCommand) {
        return voiceCommands.add(voiceCommand);
    }

}
