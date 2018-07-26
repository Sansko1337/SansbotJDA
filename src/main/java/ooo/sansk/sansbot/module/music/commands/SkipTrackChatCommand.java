package ooo.sansk.sansbot.module.music.commands;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import nl.imine.vaccine.annotation.Component;
import ooo.sansk.sansbot.command.ChatCommandHandler;
import ooo.sansk.sansbot.module.music.TrackListManager;

import java.util.Arrays;
import java.util.List;

@Component
public class SkipTrackChatCommand extends AbstractMusicChatCommand {

    private final TrackListManager trackListManager;

    public SkipTrackChatCommand(ChatCommandHandler chatCommandHandler, TrackListManager trackListManager) {
        super(chatCommandHandler);
        this.trackListManager = trackListManager;
    }

    @Override
    public List<String> getTriggers() {
        return Arrays.asList("skip", "next", "WatEenPokkeHerrie", "HebJeNogIetsBeters");
    }

    @Override
    public void handle(MessageReceivedEvent messageReceivedEvent) {
        deleteMessageIfPossible(messageReceivedEvent.getMessage());
        if (isInSameChannel(messageReceivedEvent.getMember(), messageReceivedEvent.getGuild())) {
            chatCommandHandler.getDefaultOutputChannel().sendMessage(String.format("Ik kon hier wel van genieten, %s alleen niet. ¯\\_(ツ)_/¯ Ander plaatje dan maar?", messageReceivedEvent.getAuthor().getAsMention())).queue();
            trackListManager.skip();
        } else {
            reply(messageReceivedEvent.getChannel(), String.format("Well %s, jij hebt hier toch geen last van. Laat ze lekker luisteren wat ze willen als je het toch niet kan horen... :rolling_eyes:", messageReceivedEvent.getAuthor().getAsMention()));
        }
    }
}
