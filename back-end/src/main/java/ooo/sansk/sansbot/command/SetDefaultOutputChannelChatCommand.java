package ooo.sansk.sansbot.command;

import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import nl.imine.vaccine.annotation.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class SetDefaultOutputChannelChatCommand extends ChatCommand {

    public SetDefaultOutputChannelChatCommand(ChatCommandHandler chatCommandHandler) {
        super(chatCommandHandler);
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
            reply(messageReceivedEvent.getChannel(), String.format("Als je mij gaat commanderen geef doe het dan wel goed hè? Ik heb exact één kanaal nodig om dit goed te kunnen doen, %s", messageReceivedEvent.getAuthor().getAsMention()));
        }
        if(mentionedChannels.size() > 1){
            reply(messageReceivedEvent.getChannel(), String.format("We gaan maar één kanaal onderspammen, %s, niet meer, niet minder. ", messageReceivedEvent.getAuthor().getAsMention()));
        }
        TextChannel spamChannel = mentionedChannels.get(0);
        reply(messageReceivedEvent.getChannel(), String.format("%s is het nieuwe kanaal voor bot spam volgens %s! :ok_hand:", spamChannel.getAsMention(), messageReceivedEvent.getAuthor().getAsMention()));
        chatCommandHandler.setDefaultOutputChannel(spamChannel);
    }
}
