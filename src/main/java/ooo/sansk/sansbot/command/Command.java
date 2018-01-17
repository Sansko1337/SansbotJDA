package ooo.sansk.sansbot.command;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.List;

public interface Command {

    List<String> getTriggers();
    void handle(MessageReceivedEvent messageReceivedEvent);
}
