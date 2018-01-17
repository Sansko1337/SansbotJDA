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
public class PlayingCommand implements Command {

    private final CommandHandler commandHandler;
    private final VoiceHandler voiceHandler;

    public PlayingCommand(CommandHandler commandHandler, VoiceHandler voiceHandler) {
        this.commandHandler = commandHandler;
        this.voiceHandler = voiceHandler;
    }

    @AfterCreate
    public void afterCreation() {
        commandHandler.registerCommand(this);
    }


    @Override
    public List<String> getTriggers() {
        return Arrays.asList("playing", "currenttrack", "song", "HoeHeetDitPlaatje");
    }

    @Override
    public void handle(MessageReceivedEvent messageReceivedEvent) {
        messageReceivedEvent.getMessage().delete().queue();
        AudioTrack track = voiceHandler.getCurrentTrack();
        if(track == null) {
            messageReceivedEvent.getChannel().sendMessage(String.format("%s, volgensmij zie je ze vliegen want ik speel niks af hoor... :confused:", messageReceivedEvent.getAuthor().getAsMention())).queue();
        } else {
            messageReceivedEvent.getChannel().sendMessage(String.format("%s, als ik mij niet vergis is dit... deze! :musical_score: \n%s", messageReceivedEvent.getAuthor().getAsMention(), track.getInfo().uri)).queue();
        }
    }
}
