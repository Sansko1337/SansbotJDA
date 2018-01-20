package ooo.sansk.sansbot.voice.commands;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import nl.imine.vaccine.annotation.AfterCreate;
import nl.imine.vaccine.annotation.Component;
import ooo.sansk.sansbot.command.Command;
import ooo.sansk.sansbot.command.CommandHandler;
import ooo.sansk.sansbot.voice.VoiceHandler;

import java.util.Arrays;
import java.util.List;

@Component
public class PlayYoutubeCommand extends Command {

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
        String[] commandContent = messageReceivedEvent.getMessage().getContentRaw().split(" ");
        if(commandContent.length < 2) {
            reply(messageReceivedEvent.getMessage(), String.format("Zeg %s Ik kan niet dingen op de playlist zetten als je me niet zegt wat hè?! :shrug:", messageReceivedEvent.getAuthor().getAsMention()));
            return;
        }
        if(commandContent.length > 2) {
            reply(messageReceivedEvent.getMessage(), String.format("Ho hè, %s! Één ding tegelijk alsjeblieft zeg... :frowning2:", messageReceivedEvent.getAuthor().getAsMention()));
            return;
        }
        String url = commandContent[1];
        if(!url.matches(".*")) {
            reply(messageReceivedEvent.getMessage(), String.format("Maarree %s, dit is niet een linkje waar ik iets mee kan hè? :rolling_eyes:", messageReceivedEvent.getAuthor().getAsMention()));
            return;
        }
        reply(messageReceivedEvent.getMessage(), String.format(":notes: Onze grote DJ %s heeft het volgende plaatje aangevraagd! :notes:\n%s", messageReceivedEvent.getAuthor().getAsMention(), url));
        voiceHandler.queue(url);
    }
}
