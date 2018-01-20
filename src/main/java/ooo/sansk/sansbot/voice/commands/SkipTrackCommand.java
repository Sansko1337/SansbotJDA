package ooo.sansk.sansbot.voice.commands;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import nl.imine.vaccine.annotation.AfterCreate;
import nl.imine.vaccine.annotation.Component;
import ooo.sansk.sansbot.command.Command;
import ooo.sansk.sansbot.command.CommandHandler;
import ooo.sansk.sansbot.voice.VoiceHandler;

import java.util.Arrays;
import java.util.List;

@Component
public class SkipTrackCommand extends Command {

    private final VoiceHandler voiceHandler;

    public SkipTrackCommand(CommandHandler commandHandler, VoiceHandler voiceHandler) {
        super(commandHandler);
        this.voiceHandler = voiceHandler;
    }

    @Override
    public List<String> getTriggers() {
        return Arrays.asList("skip", "next", "WatEenPokkeHerrie", "HebJeNogIetsBeters");
    }

    @Override
    public void handle(MessageReceivedEvent messageReceivedEvent) {
        deleteMessageIfPossible(messageReceivedEvent.getMessage());
        reply(messageReceivedEvent.getMessage(), String.format("Ik kon hier wel van genieten, %s alleen niet. ¯\\_(ツ)_/¯ Ander plaatje dan maar?", messageReceivedEvent.getAuthor().getAsMention()));
        voiceHandler.skip();
    }
}
