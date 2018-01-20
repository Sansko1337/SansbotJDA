package ooo.sansk.sansbot.command;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import nl.imine.vaccine.annotation.AfterCreate;
import nl.imine.vaccine.annotation.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class PatatCommand extends Command {

    public PatatCommand(CommandHandler commandHandler) {
        super(commandHandler);
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
