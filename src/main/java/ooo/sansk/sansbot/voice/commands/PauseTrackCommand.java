package ooo.sansk.sansbot.voice.commands;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import nl.imine.vaccine.annotation.AfterCreate;
import nl.imine.vaccine.annotation.Component;
import ooo.sansk.sansbot.command.Command;
import ooo.sansk.sansbot.command.CommandHandler;
import ooo.sansk.sansbot.voice.VoiceHandler;

import java.util.Arrays;
import java.util.List;

@Component
public class PauseTrackCommand extends Command {

    private final VoiceHandler voiceHandler;

    public PauseTrackCommand(CommandHandler commandHandler, VoiceHandler voiceHandler) {
        super(commandHandler);
        this.voiceHandler = voiceHandler;
    }

    @Override
    public List<String> getTriggers() {
        return Arrays.asList("pause", "pauze", "halt", "IkVerstaJeNiet", "AlleAandachtNaarMij");
    }

    @Override
    public void handle(MessageReceivedEvent messageReceivedEvent) {
        deleteMessageIfPossible(messageReceivedEvent.getMessage());
        if(voiceHandler.pause()) {
            reply(messageReceivedEvent.getMessage(), String.format("Stop de plaat! %s heeft iets belangrijks te melden!", messageReceivedEvent.getAuthor().getAsMention()));
        } else {
            reply(messageReceivedEvent.getMessage(), String.format("Er valt helemaal niks te pauzeren, %s!", messageReceivedEvent.getAuthor().getAsMention()));
        }
    }
}
