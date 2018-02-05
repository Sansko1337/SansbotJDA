package ooo.sansk.sansbot.voice.command;

import net.dv8tion.jda.core.entities.Member;
import nl.imine.vaccine.annotation.Component;
import ooo.sansk.sansbot.music.VoiceHandler;

import java.util.Arrays;
import java.util.List;

@Component
public class ResumeTrackVoiceCommand extends AbstractMusicVoiceCommand {

    private final VoiceHandler voiceHandler;

    public ResumeTrackVoiceCommand(VoiceCommandHandler voiceCommandHandler, VoiceHandler voiceHandler) {
        super(voiceCommandHandler);
        this.voiceHandler = voiceHandler;
    }

    @Override
    public List<String> getTriggers() {
        return Arrays.asList("START");
    }

    @Override
    public void handle(Member member) {
        if (isInSameChannel(member, member.getGuild())) {
            if (voiceHandler.resume()) {
                voiceCommandHandler.getDefaultOutputChannel().sendMessage(String.format("En volgens %s kan het feestje weer beginnen! :tada:", member.getAsMention())).queue();
            } else {
                reply(voiceCommandHandler.getDefaultOutputChannel(), String.format("Zeg %s, ik kan niks resumen als er verder niks af te spelen valt! :angry:", member.getAsMention()));
            }
        } else {
            reply(voiceCommandHandler.getDefaultOutputChannel(), String.format("Yo %s, ik geloof niet dat jij kan weten of men alweer zin heeft in de muziek... :confused:", member.getAsMention()));
        }
    }
}
