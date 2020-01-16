package ooo.sansk.sansbot.module.music.commands;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import nl.imine.vaccine.annotation.Component;
import ooo.sansk.sansbot.command.ChatCommand;
import ooo.sansk.sansbot.command.ChatCommandHandler;
import ooo.sansk.sansbot.module.music.TrackListManager;

import java.util.Arrays;
import java.util.List;

@Component
public class PlayingChatCommand extends ChatCommand {

    private final TrackListManager trackListManager;

    public PlayingChatCommand(ChatCommandHandler chatCommandHandler, TrackListManager trackListManager) {
        super(chatCommandHandler);
        this.trackListManager = trackListManager;
    }

    @Override
    public List<String> getTriggers() {
        return Arrays.asList("playing", "currenttrack", "song", "HoeHeetDitPlaatje");
    }

    @Override
    public void handle(MessageReceivedEvent messageReceivedEvent) {
        deleteMessageIfPossible(messageReceivedEvent.getMessage());
        AudioTrack track = trackListManager.getCurrentTrack();
        if(track == null) {
            reply(messageReceivedEvent.getChannel(), String.format("%s, volgensmij zie je ze vliegen want ik speel niks af hoor... :confused:", messageReceivedEvent.getAuthor().getAsMention()));
        } else {
            reply(messageReceivedEvent.getChannel(), String.format("%s, als ik mij niet vergis is dit... deze! :musical_score: %n%s", messageReceivedEvent.getAuthor().getAsMention(), track.getInfo().uri));
        }
    }
}
