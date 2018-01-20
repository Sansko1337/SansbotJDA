package ooo.sansk.sansbot.voice.commands;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import nl.imine.vaccine.annotation.Component;
import ooo.sansk.sansbot.command.CommandHandler;
import ooo.sansk.sansbot.voice.VoiceHandler;

import java.util.Arrays;
import java.util.List;

@Component
public class SkipTrackCommand extends AbstractMusicCommand {

    private final VoiceHandler voiceHandler;

    public SkipTrackCommand(CommandHandler commandHandler, VoiceHandler voiceHandler) {
        super(commandHandler);
        this.voiceHandler = voiceHandler;
    }

    @Override
    public List<String> getTriggers() {
        return Arrays.asList("skip", "next", "WatEenPokkeHerrie", "HebJeNogIetsBeters");
    }

    @Override
    public void handle(MessageReceivedEvent messageReceivedEvent) {
        deleteMessageIfPossible(messageReceivedEvent.getMessage());
        if (isInSameChannel(messageReceivedEvent.getMember(), messageReceivedEvent.getGuild())) {
            commandHandler.getDefaultOutputChannel().sendMessage(String.format("Ik kon hier wel van genieten, %s alleen niet. ¯\\_(ツ)_/¯ Ander plaatje dan maar?", messageReceivedEvent.getAuthor().getAsMention())).queue();
            voiceHandler.skip();
        } else {
            reply(messageReceivedEvent.getTextChannel(), String.format("Well %s, jij hebt hier toch geen last van. Laat ze lekker luisteren wat ze willen als je het toch niet kan horen... :rolling_eyes:", messageReceivedEvent.getAuthor().getAsMention()));
        }
    }
}
