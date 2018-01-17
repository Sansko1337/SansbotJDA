package ooo.sansk.sansbot.command;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import nl.imine.vaccine.annotation.AfterCreate;
import nl.imine.vaccine.annotation.Component;
import ooo.sansk.sansbot.voice.VoiceHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

@Component
public class SnoepCommand implements Command {

    private static final Logger logger = LoggerFactory.getLogger(SnoepCommand.class);

    private final JDA jda;
    private final VoiceHandler voiceHandler;
    private final CommandHandler commandHandler;

    public SnoepCommand(JDA jda, VoiceHandler voiceHandler, CommandHandler commandHandler) {
        this.jda = jda;
        this.voiceHandler = voiceHandler;
        this.commandHandler = commandHandler;
    }

    @AfterCreate
    public void afterCreation() {
        commandHandler.registerCommand(this);
    }

    @Override
    public List<String> getTriggers() {
        return Arrays.asList("snoep", "gratissnoep");
    }

    @Override
    public void handle(MessageReceivedEvent messageReceivedEvent) {
        messageReceivedEvent.getMessage().delete().queue();
        voiceHandler.getAudioPlayerManager().loadItem("https://www.youtube.com/watch?v=LB5PWnM6AlA", new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                logger.info("Located track \"{}\". Starting playback", track.getInfo().title);
                logger.info("Playback successful: {} ",voiceHandler.play(track));
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                logger.info("Loaded playlist: {}", playlist.getName());
            }

            @Override
            public void noMatches() {
                logger.info("No matches found for");
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                logger.info("Loading track failed ({}: {})", exception.getClass().getSimpleName(), exception.getMessage());
            }
        });
    }
}
