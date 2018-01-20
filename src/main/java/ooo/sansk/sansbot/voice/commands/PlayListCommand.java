package ooo.sansk.sansbot.voice.commands;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import nl.imine.vaccine.annotation.AfterCreate;
import nl.imine.vaccine.annotation.Component;
import ooo.sansk.sansbot.command.Command;
import ooo.sansk.sansbot.command.CommandHandler;
import ooo.sansk.sansbot.voice.VoiceHandler;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Component
public class PlayListCommand extends Command {

    private final VoiceHandler voiceHandler;

    public PlayListCommand(CommandHandler commandHandler, VoiceHandler voiceHandler) {
        super(commandHandler);
        this.voiceHandler = voiceHandler;
    }

    @Override
    public List<String> getTriggers() {
        return Arrays.asList("playlist", "tracks", "commingup", "KomtErNogWatLeuksAan");
    }

    @Override
    public void handle(MessageReceivedEvent messageReceivedEvent) {
        deleteMessageIfPossible(messageReceivedEvent.getMessage());
        if(!voiceHandler.getQueue().isEmpty()) {
            EmbedBuilder embedBuilder = new EmbedBuilder().setTitle(":cd: Playlist");
            voiceHandler.getQueue().stream()
                    .limit(25)
                    .map(AudioTrack::getInfo)
                    .forEach(track -> embedBuilder.addField(track.title + " | " + track.length, track.author + " (" + track.uri + ")", false));
            reply(messageReceivedEvent.getChannel(), new MessageBuilder(String.format("Hier %s, dit zijn nummers die er nog aan zullen komen!", messageReceivedEvent.getAuthor().getAsMention())).setEmbed(embedBuilder.build()).build());
        } else {
            reply(messageReceivedEvent.getChannel(), String.format("Sorry %s, maar er staat nog niks op de lijst. Misschien kan je zelf wat toevoegen!", messageReceivedEvent.getAuthor().getAsMention()));
        }
    }

    public String getReadableTimeString(long millis) {
        Date resultDate = new Date(millis);
        return new SimpleDateFormat("HH:mm:ss").format(resultDate);
    }
}
