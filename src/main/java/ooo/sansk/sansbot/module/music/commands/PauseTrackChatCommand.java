package ooo.sansk.sansbot.module.music.commands;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import nl.imine.vaccine.annotation.Component;
import ooo.sansk.sansbot.command.ChatCommandHandler;
import ooo.sansk.sansbot.module.music.TrackListManager;

import java.util.Arrays;
import java.util.List;

@Component
public class PauseTrackChatCommand extends AbstractMusicChatCommand {

    private final TrackListManager trackListManager;

    public PauseTrackChatCommand(ChatCommandHandler chatCommandHandler, TrackListManager trackListManager) {
        super(chatCommandHandler);
        this.trackListManager = trackListManager;
    }

    @Override
    public List<String> getTriggers() {
        return Arrays.asList("pause", "pauze", "halt", "IkVerstaJeNiet", "AlleAandachtNaarMij");
    }

    @Override
    public void handle(MessageReceivedEvent messageReceivedEvent) {
        deleteMessageIfPossible(messageReceivedEvent.getMessage());
        if(isInSameChannel(messageReceivedEvent.getMember(), messageReceivedEvent.getGuild())) {
            if (trackListManager.pause()) {
                reply(messageReceivedEvent.getChannel(), String.format("Stop de plaat! %s heeft iets belangrijks te melden!", messageReceivedEvent.getAuthor().getAsMention()));
            } else {
                reply(messageReceivedEvent.getChannel(), String.format("Er valt helemaal niks te pauzeren, %s!", messageReceivedEvent.getAuthor().getAsMention()));
            }
        } else {
            reply(messageReceivedEvent.getChannel(), String.format("Zeg %s, we gaan niet de lol van andere verzieken als je er toch geen last van hebt... :angry:", messageReceivedEvent.getAuthor().getAsMention()));
        }
    }
}
