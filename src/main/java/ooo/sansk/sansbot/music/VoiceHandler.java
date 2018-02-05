package ooo.sansk.sansbot.music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEvent;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventListener;
import com.sedmelluq.discord.lavaplayer.player.event.TrackEndEvent;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.VoiceChannel;
import nl.imine.vaccine.annotation.AfterCreate;
import nl.imine.vaccine.annotation.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

@Component
public class VoiceHandler implements AudioEventListener {

    private static final Logger logger = LoggerFactory.getLogger(VoiceHandler.class);

    private final JDA jda;
    private final AudioPlayerManager audioPlayerManager;
    private final Queue<AudioTrack> queue;

    private AudioPlayer audioPlayer;

    public VoiceHandler(JDA jda) {
        this.jda = jda;
        this.audioPlayerManager = new DefaultAudioPlayerManager();
        this.queue = new LinkedBlockingQueue<>();
        AudioSourceManagers.registerLocalSource(audioPlayerManager);
        AudioSourceManagers.registerRemoteSources(audioPlayerManager);
    }

    @AfterCreate
    public void afterCreation() {
        logger.info("Guilds:");
        jda.getGuilds().forEach(guild -> logger.info("{}", guild.getName()));
        Guild guild = jda.getGuilds().get(0);
        for(VoiceChannel voiceChannel : guild.getVoiceChannels()) {
            guild.getAudioManager().openAudioConnection(voiceChannel);
        }
        audioPlayer = audioPlayerManager.createPlayer();
        audioPlayer.addListener(this);
//        setSendingHandler(guild);
    }

    public void setSendingHandler(Guild guild) {
        guild.getAudioManager().setSendingHandler(new AudioPlayerSendHandler(audioPlayer));
    }

    public AudioPlayerManager getAudioPlayerManager() {
        return audioPlayerManager;
    }

    public void queue(String track){
        audioPlayerManager.loadItem(track, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                queue(track);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {}

            @Override
            public void noMatches() {
                logger.warn("No match found for track \"{}\"", track);
            }

            @Override
            public void loadFailed(FriendlyException e) {
                logger.error("An exception occurred while loading AudioTrack ({}: {})", e.getCause().getClass().getSimpleName(), e.getMessage());
            }
        });
    }

    private void queue(AudioTrack audioTrack) {
        if(queue.isEmpty() && audioPlayer.getPlayingTrack() == null) {
            play(audioTrack);
        } else {
            queue.add(audioTrack);
        }
    }

    public boolean play(AudioTrack track) {
        logger.info("Playing audio track: {}", track.getInfo().title);
        return audioPlayer.startTrack(track, false);
    }

    public void skip() {
        audioPlayer.stopTrack();
    }

    public boolean pause() {
        if(audioPlayer.getPlayingTrack() != null) {
            audioPlayer.setPaused(true);
            return true;
        } else {
            return false;
        }
    }

    public boolean resume() {
        audioPlayer.setPaused(false);
        if(audioPlayer.getPlayingTrack() == null) {
            AudioTrack track = queue.poll();
            if(track != null) {
                play(track);
                return true;
            }
            return false;
        } else {
            return true;
        }
    }

    public AudioTrack getCurrentTrack(){
        return audioPlayer.getPlayingTrack();
    }

    public Queue<AudioTrack> getQueue() {
        return queue;
    }

    @Override
    public void onEvent(AudioEvent event) {
        if(event instanceof TrackEndEvent) {
            AudioTrack track = queue.poll();
            if(track != null) {
                play(track);
            }
        }
    }
}
