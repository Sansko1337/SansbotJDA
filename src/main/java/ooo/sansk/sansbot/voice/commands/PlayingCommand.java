package ooo.sansk.sansbot.voice.commands;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import nl.imine.vaccine.annotation.AfterCreate;
import nl.imine.vaccine.annotation.Component;
import ooo.sansk.sansbot.command.Command;
import ooo.sansk.sansbot.command.CommandHandler;
import ooo.sansk.sansbot.voice.VoiceHandler;

import java.util.Arrays;
import java.util.List;

@Component
public class PlayingCommand extends Command {

    private final VoiceHandler voiceHandler;

    public PlayingCommand(CommandHandler commandHandler, VoiceHandler voiceHandler) {
        super(commandHandler);
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
            reply(messageReceivedEvent.getMessage(), String.format("%s, volgensmij zie je ze vliegen want ik speel niks af hoor... :confused:", messageReceivedEvent.getAuthor().getAsMention()));
        } else {
            reply(messageReceivedEvent.getMessage(), String.format("%s, als ik mij niet vergis is dit... deze! :musical_score: \n%s", messageReceivedEvent.getAuthor().getAsMention(), track.getInfo().uri));
        }
    }
}
