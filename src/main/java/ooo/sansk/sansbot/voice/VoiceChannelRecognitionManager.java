package ooo.sansk.sansbot.voice;

import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.StreamSpeechRecognizer;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.core.hooks.EventListener;
import nl.imine.vaccine.annotation.AfterCreate;
import nl.imine.vaccine.annotation.Component;
import nl.imine.vaccine.annotation.Property;
import ooo.sansk.sansbot.voice.command.VoiceCommand;
import ooo.sansk.sansbot.voice.command.VoiceCommandHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class VoiceChannelRecognitionManager implements EventListener {

    private static final Logger logger = LoggerFactory.getLogger(VoiceChannelRecognitionManager.class);

    private final JDA jda;
    private final VoiceCommandHandler voiceCommandHandler;
    private final Configuration configuration; //Sphinx Recognizer Configuration
    private final Map<String, RecognizerHandler> userRecognizer;
    private final boolean active;

    public VoiceChannelRecognitionManager(JDA jda, VoiceCommandHandler voiceCommandHandler, Configuration configuration, @Property("sansbot.voice.active") String activeString) {
        this.jda = jda;
        this.voiceCommandHandler = voiceCommandHandler;
        this.configuration = configuration;
        this.userRecognizer = new HashMap<>();
        this.active = Boolean.valueOf(activeString);
    }

    @AfterCreate
    public void afterCreation() {
        if(active) {
            jda.addEventListener(this);
        } else {
            logger.info("Voice recognition disabled, not registering for channel events");
        }
    }

    @Override
    public void onEvent(Event event) {
        if (event instanceof GuildVoiceJoinEvent) {
            GuildVoiceJoinEvent guildVoiceJoinEvent = (GuildVoiceJoinEvent) event;
            if (isMemberInSameVoiceChannel(guildVoiceJoinEvent.getMember())) {
                startListenToMember(guildVoiceJoinEvent.getMember());
            }
        } else if (event instanceof GuildVoiceLeaveEvent) {
            GuildVoiceLeaveEvent guildVoiceLeaveEvent = (GuildVoiceLeaveEvent) event;
            if (isMemberInSameVoiceChannel(guildVoiceLeaveEvent.getMember())) {
                stopListenToMember(guildVoiceLeaveEvent.getMember());
            }
        } else if (event instanceof GuildVoiceMoveEvent) {
            GuildVoiceMoveEvent guildVoiceMoveEvent = (GuildVoiceMoveEvent) event;
            handleMoveToChannel(guildVoiceMoveEvent);
            handleMoveAwayFromChannel(guildVoiceMoveEvent);
            handleBotMoveEvent(guildVoiceMoveEvent);
        }
    }

    private void startListenToMember(Member member) {
        if(!member.equals(member.getGuild().getSelfMember())) {
            try {
                StreamSpeechRecognizer streamSpeechRecognizer = new StreamSpeechRecognizer(configuration);
                RecognizerHandler recognizerHandler = new RecognizerHandler(member, streamSpeechRecognizer, voiceCommandHandler);
                userRecognizer.put(member.getUser().getId(), recognizerHandler);
                recognizerHandler.startListening();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void stopListenToMember(Member member) {
        RecognizerHandler recognizerHandler = userRecognizer.get(member.getUser().getId());
        if(recognizerHandler != null) {
            recognizerHandler.stopListening();
            userRecognizer.remove(member.getUser().getId());
        }
    }

    public void handleAudio(User user, byte[] audio) {
        RecognizerHandler recognizerHandler = userRecognizer.get(user.getId());
        if(recognizerHandler != null) {
            recognizerHandler.handleAudio(audio);
        }
    }

    private boolean isMemberInSameVoiceChannel(Member member) {
        return member.getVoiceState() != null && member.getVoiceState().getChannel() != null && member.getVoiceState().getChannel().equals(member.getGuild().getSelfMember().getVoiceState().getChannel());
    }

    private void handleMoveToChannel(GuildVoiceMoveEvent guildVoiceMoveEvent) {
        VoiceChannel currentBotChannel = guildVoiceMoveEvent.getGuild().getSelfMember().getVoiceState().getChannel();
        if(guildVoiceMoveEvent.getChannelJoined().equals(currentBotChannel)) {
            startListenToMember(guildVoiceMoveEvent.getMember());
        }
    }

    private void handleMoveAwayFromChannel(GuildVoiceMoveEvent guildVoiceMoveEvent) {
        VoiceChannel currentBotChannel = guildVoiceMoveEvent.getGuild().getSelfMember().getVoiceState().getChannel();
        if(guildVoiceMoveEvent.getChannelLeft().equals(currentBotChannel)) {
            stopListenToMember(guildVoiceMoveEvent.getMember());
        }
    }

    private void handleBotMoveEvent(GuildVoiceMoveEvent guildVoiceMoveEvent) {
        if(guildVoiceMoveEvent.getMember().equals(guildVoiceMoveEvent.getGuild().getSelfMember())) {
            guildVoiceMoveEvent.getChannelLeft().getMembers().forEach(this::stopListenToMember);
            guildVoiceMoveEvent.getChannelJoined().getMembers().forEach(this::startListenToMember);
        }
    }
}
