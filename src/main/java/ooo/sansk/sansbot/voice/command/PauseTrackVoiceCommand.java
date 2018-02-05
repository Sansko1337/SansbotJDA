package ooo.sansk.sansbot.voice.command;

import net.dv8tion.jda.core.entities.Member;
import nl.imine.vaccine.annotation.Component;
import ooo.sansk.sansbot.music.VoiceHandler;

import java.util.Arrays;
import java.util.List;

@Component
public class PauseTrackVoiceCommand extends AbstractMusicVoiceCommand {

    private final VoiceHandler voiceHandler;

    public PauseTrackVoiceCommand(VoiceCommandHandler voiceCommandHandler, VoiceHandler voiceHandler) {
        super(voiceCommandHandler);
        this.voiceHandler = voiceHandler;
    }

    @Override
    public List<String> getTriggers() {
        return Arrays.asList("STIL", "STOP", "BAKKES");
    }

    @Override
    public void handle(Member member) {
        if(isInSameChannel(member, member.getGuild())) {
            if (voiceHandler.pause()) {
                reply(voiceCommandHandler.getDefaultOutputChannel(), String.format("Stop de plaat! %s heeft iets belangrijks te melden!", member.getAsMention()));
            } else {
                reply(voiceCommandHandler.getDefaultOutputChannel(), String.format("Er valt helemaal niks te pauzeren, %s!", member.getAsMention()));
            }
        } else {
            reply(voiceCommandHandler.getDefaultOutputChannel(), String.format("Zeg %s, we gaan niet de lol van andere verzieken als je er toch geen last van hebt... :angry:", member.getAsMention()));
        }
    }
}
