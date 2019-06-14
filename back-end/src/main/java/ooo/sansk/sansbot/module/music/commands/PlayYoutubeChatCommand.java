package ooo.sansk.sansbot.module.music.commands;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import nl.imine.vaccine.annotation.Component;
import ooo.sansk.sansbot.command.ChatCommandHandler;
import ooo.sansk.sansbot.module.music.TrackListManager;

import java.util.Arrays;
import java.util.List;

@Component
public class PlayYoutubeChatCommand extends AbstractMusicChatCommand {

    private final TrackListManager trackListManager;

    public PlayYoutubeChatCommand(ChatCommandHandler chatCommandHandler, TrackListManager trackListManager) {
        super(chatCommandHandler);
        this.trackListManager = trackListManager;
    }

    @Override
    public List<String> getTriggers() {
        return Arrays.asList("play", "p", "yt");
    }

    @Override
    public void handle(MessageReceivedEvent messageReceivedEvent) {
        deleteMessageIfPossible(messageReceivedEvent.getMessage());
        if (isInSameChannel(messageReceivedEvent.getMember(), messageReceivedEvent.getGuild())) {
            String[] commandContent = messageReceivedEvent.getMessage().getContentRaw().split(" ");
            if (commandContent.length < 2) {
                reply(messageReceivedEvent.getChannel(), String.format("Zeg %s Ik kan niet dingen op de playlist zetten als je me niet zegt wat hè?! :shrug:", messageReceivedEvent.getAuthor().getAsMention()));
                return;
            }
            if (commandContent.length > 2) {
                reply(messageReceivedEvent.getChannel(), String.format("Ho hè, %s! Één ding tegelijk alsjeblieft zeg... :frowning2:", messageReceivedEvent.getAuthor().getAsMention()));
                return;
            }
            String url = commandContent[1];
            if (!url.matches(".*")) {
                reply(messageReceivedEvent.getChannel(), String.format("Maarree %s, dit is niet een linkje waar ik iets mee kan hè? :rolling_eyes:", messageReceivedEvent.getAuthor().getAsMention()));
                return;
            }
            trackListManager.loadTrack(url, messageReceivedEvent.getAuthor().getAsMention()).thenAccept(message -> chatCommandHandler.getDefaultOutputChannel().sendMessage(message).queue());

        } else {
            reply(messageReceivedEvent.getChannel(), String.format("Ik ga geen dingen voor je opzetten als je er toch niet zelf naar gaat luisteren, %s... :expressionless:", messageReceivedEvent.getAuthor().getAsMention()));
        }
    }
}
