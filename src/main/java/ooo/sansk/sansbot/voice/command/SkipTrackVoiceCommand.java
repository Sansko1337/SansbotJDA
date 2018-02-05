package ooo.sansk.sansbot.voice.command;

import net.dv8tion.jda.core.entities.Member;
import nl.imine.vaccine.annotation.Component;
import ooo.sansk.sansbot.music.VoiceHandler;

import java.util.Arrays;
import java.util.List;

@Component
public class SkipTrackVoiceCommand extends AbstractMusicVoiceCommand {

    private final VoiceHandler voiceHandler;

    public SkipTrackVoiceCommand(VoiceCommandHandler voiceCommandHandler, VoiceHandler voiceHandler) {
        super(voiceCommandHandler);
        this.voiceHandler = voiceHandler;
    }

    @Override
    public List<String> getTriggers() {
        return Arrays.asList("SKIP", "VOLGENDE");
    }

    @Override
    public void handle(Member member) {
        if (isInSameChannel(member, member.getGuild())) {
            voiceCommandHandler.getDefaultOutputChannel().sendMessage(String.format("Ik kon hier wel van genieten, %s alleen niet. ¯\\_(ツ)_/¯ Ander plaatje dan maar?", member.getAsMention())).queue();
            voiceHandler.skip();
        } else {
            reply(voiceCommandHandler.getDefaultOutputChannel(), String.format("Well %s, jij hebt hier toch geen last van. Laat ze lekker luisteren wat ze willen als je het toch niet kan horen... :rolling_eyes:", member.getAsMention()));
        }
    }
}
