package ooo.sansk.sansbot.command;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import nl.imine.vaccine.annotation.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class PatatChatCommand extends ChatCommand {

    public PatatChatCommand(ChatCommandHandler chatCommandHandler) {
        super(chatCommandHandler);
    }

    @Override
    public List<String> getTriggers() {
        return Arrays.asList("WeVliegenErIn", "FriedaKroket");
    }

    @Override
    public void handle(MessageReceivedEvent messageReceivedEvent) {
        messageReceivedEvent.getChannel().sendMessage("**PATAT!**").submit();
        messageReceivedEvent.getChannel().sendFile(ClassLoader.getSystemResourceAsStream("Patat.jpg"), "Patat.jpg").submit();
    }
}
