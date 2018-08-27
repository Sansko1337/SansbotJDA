package ooo.sansk.sansbot.module.music.commands;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import nl.imine.vaccine.annotation.Component;
import ooo.sansk.sansbot.command.ChatCommandHandler;
import ooo.sansk.sansbot.module.music.TrackListManager;

import java.util.Arrays;
import java.util.List;

@Component
public class MoveChannelCommand extends AbstractMusicChatCommand {

    private final TrackListManager trackListManager;

    public MoveChannelCommand(ChatCommandHandler chatCommandHandler, TrackListManager trackListManager) {
        super(chatCommandHandler);
        this.trackListManager = trackListManager;
    }

    @Override
    public List<String> getTriggers() {
        return Arrays.asList("requestbot", "movesansbot", "sansbotplz", "geefsansbot");
    }

    @Override
    public void handle(MessageReceivedEvent messageReceivedEvent) {
        deleteMessageIfPossible(messageReceivedEvent.getMessage());
        if(!messageReceivedEvent.getGuild().getSelfMember().getVoiceState().inVoiceChannel()) {
            reply(messageReceivedEvent.getChannel(), String.format("Sorry %s, ik ben even niet beschikbaar voor je feestje... :disappointed:", messageReceivedEvent.getAuthor().getAsMention()));
            return;
        }
        if(messageReceivedEvent.getGuild().getSelfMember().getVoiceState().getChannel().equals(messageReceivedEvent.getMember().getVoiceState().getChannel())) {
            reply(messageReceivedEvent.getChannel(), String.format("Uhh, volgensmij ben ik er al hoor, %s? :thinking:", messageReceivedEvent.getAuthor().getAsMention()));
            return;
        }
        if(!messageReceivedEvent.getMember().getVoiceState().inVoiceChannel()) {
            reply(messageReceivedEvent.getChannel(), String.format("Zeg %s, je zit helemaal niet in een kanaal. Hoe moet ik nu weten waar ik naartoe moet?! :confused:", messageReceivedEvent.getAuthor().getAsMention()));
            return;
        }
        if(trackListManager.getCurrentTrack() != null) {
            reply(messageReceivedEvent.getChannel(), String.format("Sorry, anderen waren eerst en die zijn nog aan het genieten van de muziek. Probeer het anders later nog eens, %s? :upside_down:", messageReceivedEvent.getAuthor().getAsMention()));
            return;
        }
        reply(messageReceivedEvent.getChannel(), String.format("Ik kom al! %s :yum:", messageReceivedEvent.getAuthor().getAsMention()));
        messageReceivedEvent.getGuild().getAudioManager().openAudioConnection(messageReceivedEvent.getMember().getVoiceState().getChannel());
    }
}
