package ooo.sansk.sansbot.music.commands;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import nl.imine.vaccine.annotation.Component;
import ooo.sansk.sansbot.command.CommandHandler;
import ooo.sansk.sansbot.music.VoiceHandler;

import java.util.Arrays;
import java.util.List;

@Component
public class PlayYoutubeCommand extends AbstractMusicCommand {

    private final VoiceHandler voiceHandler;

    public PlayYoutubeCommand(CommandHandler commandHandler, VoiceHandler voiceHandler) {
        super(commandHandler);
        this.voiceHandler = voiceHandler;
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
            commandHandler.getDefaultOutputChannel().sendMessage(String.format(":notes: Onze grote DJ %s heeft het volgende plaatje aangevraagd! :notes:\n%s", messageReceivedEvent.getAuthor().getAsMention(), url)).queue();
            voiceHandler.queue(url);
        } else {
            reply(messageReceivedEvent.getChannel(), String.format("Ik ga geen dingen voor je opzetten als je er toch niet zelf naar gaat luisteren, %s... :expressionless:", messageReceivedEvent.getAuthor().getAsMention()));
        }
    }
}
