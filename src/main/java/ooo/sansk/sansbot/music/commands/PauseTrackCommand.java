package ooo.sansk.sansbot.music.commands;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import nl.imine.vaccine.annotation.Component;
import ooo.sansk.sansbot.command.CommandHandler;
import ooo.sansk.sansbot.music.VoiceHandler;

import java.util.Arrays;
import java.util.List;

@Component
public class PauseTrackCommand extends AbstractMusicCommand {

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
        if(isInSameChannel(messageReceivedEvent.getMember(), messageReceivedEvent.getGuild())) {
            if (voiceHandler.pause()) {
                reply(messageReceivedEvent.getChannel(), String.format("Stop de plaat! %s heeft iets belangrijks te melden!", messageReceivedEvent.getAuthor().getAsMention()));
            } else {
                reply(messageReceivedEvent.getChannel(), String.format("Er valt helemaal niks te pauzeren, %s!", messageReceivedEvent.getAuthor().getAsMention()));
            }
        } else {
            reply(messageReceivedEvent.getChannel(), String.format("Zeg %s, we gaan niet de lol van andere verzieken als je er toch geen last van hebt... :angry:", messageReceivedEvent.getAuthor().getAsMention()));
        }
    }
}
