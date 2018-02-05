package ooo.sansk.sansbot.voice;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.audio.AudioReceiveHandler;
import net.dv8tion.jda.core.audio.CombinedAudio;
import net.dv8tion.jda.core.audio.UserAudio;
import net.dv8tion.jda.core.audio.hooks.ConnectionStatus;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.VoiceChannel;
import nl.imine.vaccine.annotation.AfterCreate;
import nl.imine.vaccine.annotation.Component;
import nl.imine.vaccine.annotation.Property;
import ooo.sansk.sansbot.music.VoiceHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Component
public class RecognitionAudioReceiveHandler implements AudioReceiveHandler {

    public static final double VOLUME = 1.0;
    private static final Logger logger = LoggerFactory.getLogger(RecognitionAudioReceiveHandler.class);

    private final JDA jda;
    private final VoiceChannelRecognitionManager voiceChannelRecognitionManager;
    private final VoiceHandler voiceHandler;
    private final boolean active;

    public RecognitionAudioReceiveHandler(JDA jda, VoiceChannelRecognitionManager voiceChannelRecognitionManager, VoiceHandler voiceHandler, @Property("sansbot.voice.active") String activeString) {
        this.jda = jda;
        this.voiceChannelRecognitionManager = voiceChannelRecognitionManager;
        this.voiceHandler = voiceHandler;
        this.active = Boolean.valueOf(activeString);
    }

    @AfterCreate
    public void afterCreation() {
        Guild guild = jda.getGuilds().get(0);
        if (guild.getAudioManager().getConnectionStatus().equals(ConnectionStatus.NOT_CONNECTED)) {
            for (VoiceChannel voiceChannel : guild.getVoiceChannels()) {
                guild.getAudioManager().openAudioConnection(voiceChannel);
            }
        }
        logger.info("Setting handler for guild: {}", guild.getName());
        voiceHandler.setSendingHandler(guild);
        if(active) {
            guild.getAudioManager().setReceivingHandler(this);
        } else {
            logger.info("Voice recognition disabled, not registering audio receiver");
        }
    }

    @Override
    public boolean canReceiveUser() {
        return true;
    }

    @Override
    public void handleUserAudio(UserAudio userAudio) {
        voiceChannelRecognitionManager.handleAudio(userAudio.getUser(), convertAudio(userAudio.getAudioData(VOLUME)));
    }

    @Override
    public boolean canReceiveCombined() {
        return false;
    }

    @Override
    public void handleCombinedAudio(CombinedAudio combinedAudio) {
        throw new UnsupportedOperationException();
    }

    private byte[] convertAudio(byte[] original) {
        AudioFormat originalFormat = new AudioFormat(48000.0f, 16, 2, true, true);
        AudioFormat targetFormat = new AudioFormat(16000.0f, 16, 1, true, false);

        AudioInputStream originAudioInputStream = new AudioInputStream(new ByteArrayInputStream(original), originalFormat, original.length / originalFormat.getFrameSize());
        AudioInputStream targetAudioInputStream = AudioSystem.getAudioInputStream(targetFormat, originAudioInputStream);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        try {
            targetAudioInputStream.transferTo(byteArrayOutputStream);
        } catch (IOException e) {
            logger.error("Exception while converting audio to new format ({}: {})", e.getClass().getSimpleName(), e.getMessage());
        }

        return byteArrayOutputStream.toByteArray();

    }
}
