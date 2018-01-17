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
public class PlayYoutubeCommand implements Command {

    private final CommandHandler commandHandler;
    private final VoiceHandler voiceHandler;

    public PlayYoutubeCommand(CommandHandler commandHandler, VoiceHandler voiceHandler) {
        this.commandHandler = commandHandler;
        this.voiceHandler = voiceHandler;
    }

    @AfterCreate
    public void afterCreation() {
        commandHandler.registerCommand(this);
    }


    @Override
    public List<String> getTriggers() {
        return Arrays.asList("play", "p", "yt");
    }

    @Override
    public void handle(MessageReceivedEvent messageReceivedEvent) {
        messageReceivedEvent.getMessage().delete().queue();
        String[] commandContent = messageReceivedEvent.getMessage().getContentRaw().split(" ");
        if(commandContent.length < 2) {
            messageReceivedEvent.getChannel().sendMessage("Ik kan niet dingen op de playlist zetten als je me niet zegt wat hè?! :shrug:").queue();
            return;
        }
        if(commandContent.length > 2) {
            messageReceivedEvent.getChannel().sendMessage("Ho hè! Één ding tegelijk alsjeblieft zeg... :frowning2:").queue();
            return;
        }
        String url = commandContent[1];
        if(!url.matches(".*")) {
            messageReceivedEvent.getChannel().sendMessage("Maarree, dit is niet een linkje waar ik iets mee kan hè? :rolling_eyes:").queue();
            return;
        }
        messageReceivedEvent.getChannel().sendMessage(String.format(":notes: Onze grote DJ %s heeft het volgende plaatje aangevraagd! :notes:\n%s", messageReceivedEvent.getAuthor().getAsMention(), url)).queue();
        voiceHandler.queue(url);
    }
}
