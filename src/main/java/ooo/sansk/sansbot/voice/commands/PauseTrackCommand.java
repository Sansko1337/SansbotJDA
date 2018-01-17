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
public class PauseTrackCommand implements Command {

    private final CommandHandler commandHandler;
    private final VoiceHandler voiceHandler;

    public PauseTrackCommand(CommandHandler commandHandler, VoiceHandler voiceHandler) {
        this.commandHandler = commandHandler;
        this.voiceHandler = voiceHandler;
    }

    @AfterCreate
    public void afterCreation() {
        commandHandler.registerCommand(this);
    }


    @Override
    public List<String> getTriggers() {
        return Arrays.asList("pause", "pauze", "halt", "IkVerstaJeNiet", "AlleAandachtNaarMij");
    }

    @Override
    public void handle(MessageReceivedEvent messageReceivedEvent) {
        messageReceivedEvent.getMessage().delete().queue();
        if(voiceHandler.pasue()) {
            messageReceivedEvent.getChannel().sendMessage(String.format("Stop de plaat! %s heeft iets belangrijks te melden!", messageReceivedEvent.getAuthor().getAsMention())).queue();
        } else {
            messageReceivedEvent.getChannel().sendMessage(String.format("Er valt helemaal niks te pauzeren, %s!", messageReceivedEvent.getAuthor().getAsMention())).queue();
        }
    }
}
