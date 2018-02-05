package ooo.sansk.sansbot.music.commands;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import nl.imine.vaccine.annotation.Component;
import ooo.sansk.sansbot.command.ChatCommand;
import ooo.sansk.sansbot.command.ChatCommandHandler;
import ooo.sansk.sansbot.music.VoiceHandler;

import java.util.Arrays;
import java.util.List;

@Component
public class PlayingChatCommand extends ChatCommand {

    private final VoiceHandler voiceHandler;

    public PlayingChatCommand(ChatCommandHandler chatCommandHandler, VoiceHandler voiceHandler) {
        super(chatCommandHandler);
        this.voiceHandler = voiceHandler;
    }

    @Override
    public List<String> getTriggers() {
        return Arrays.asList("playing", "currenttrack", "song", "HoeHeetDitPlaatje");
    }

    @Override
    public void handle(MessageReceivedEvent messageReceivedEvent) {
        deleteMessageIfPossible(messageReceivedEvent.getMessage());
        AudioTrack track = voiceHandler.getCurrentTrack();
        if(track == null) {
            reply(messageReceivedEvent.getChannel(), String.format("%s, volgensmij zie je ze vliegen want ik speel niks af hoor... :confused:", messageReceivedEvent.getAuthor().getAsMention()));
        } else {
            reply(messageReceivedEvent.getChannel(), String.format("%s, als ik mij niet vergis is dit... deze! :musical_score: \n%s", messageReceivedEvent.getAuthor().getAsMention(), track.getInfo().uri));
        }
    }
}
