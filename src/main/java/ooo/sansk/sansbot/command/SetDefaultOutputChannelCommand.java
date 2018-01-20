package ooo.sansk.sansbot.command;

import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import nl.imine.vaccine.annotation.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class SetDefaultOutputChannelCommand extends Command {

    public SetDefaultOutputChannelCommand(CommandHandler commandHandler) {
        super(commandHandler);
    }

    @Override
    public List<String> getTriggers() {
        return Arrays.asList("botOutputChannel", "botOutput");
    }

    @Override
    public void handle(MessageReceivedEvent messageReceivedEvent) {
        deleteMessageIfPossible(messageReceivedEvent.getMessage());
        List<TextChannel> mentionedChannels = messageReceivedEvent.getMessage().getMentionedChannels();
        if(mentionedChannels.isEmpty()) {
            reply(messageReceivedEvent.getMessage(), String.format("Als je mij gaat commanderen geef doe het dan wel goed hè? Ik heb exact één kanaal nodig om dit goed te kunnen doen, %s", messageReceivedEvent.getAuthor().getAsMention()));
        }
        if(mentionedChannels.size() > 1){
            reply(messageReceivedEvent.getMessage(), String.format("We gaan maar één kanaal onderspammen, %s, niet meer, niet minder. ", messageReceivedEvent.getAuthor().getAsMention()));
        }
        TextChannel spamChannel = mentionedChannels.get(0);
        reply(messageReceivedEvent.getMessage(), String.format("%s is het nieuwe kanaal voor bot spam volgens %s! :ok_hand:", spamChannel.getAsMention(), messageReceivedEvent.getAuthor().getAsMention()));
        commandHandler.setDefaultOutputChannel(spamChannel);
    }
}
