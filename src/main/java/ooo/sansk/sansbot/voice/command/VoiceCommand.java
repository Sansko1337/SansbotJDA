package ooo.sansk.sansbot.voice.command;

import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import nl.imine.vaccine.annotation.AfterCreate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public abstract class VoiceCommand {

    private static final Logger logger = LoggerFactory.getLogger(VoiceCommand.class);

    protected final VoiceCommandHandler voiceCommandHandler;

    public VoiceCommand(VoiceCommandHandler voiceCommandHandler) {
        this.voiceCommandHandler = voiceCommandHandler;
    }

    @AfterCreate
    public void afterCreation() {
        logger.trace("Registering command: {}", getClass().getSimpleName());
        voiceCommandHandler.registerCommand(this);
    }

    public abstract List<String> getTriggers();

    public abstract void handle(Member member);

    public void reply(MessageChannel original, Message reply) {
        voiceCommandHandler.getDefaultOutputChannel().sendMessage(reply).queue();
    }

    public void reply(MessageChannel original, String reply) {
        reply(original, new MessageBuilder(reply).build());
    }
}
