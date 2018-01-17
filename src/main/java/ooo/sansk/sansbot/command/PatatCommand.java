package ooo.sansk.sansbot.command;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import nl.imine.vaccine.annotation.AfterCreate;
import nl.imine.vaccine.annotation.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class PatatCommand implements Command {

    private final CommandHandler commandHandler;

    public PatatCommand(CommandHandler commandHandler) {
        this.commandHandler = commandHandler;
    }

    @AfterCreate
    public void afterCreation(){
        commandHandler.registerCommand(this);
    }

    @Override
    public List<String> getTriggers() {
        return Arrays.asList("WeVliegenErIn");
    }

    @Override
    public void handle(MessageReceivedEvent messageReceivedEvent) {
        messageReceivedEvent.getChannel().sendMessage("**PATAT!**").submit();
        messageReceivedEvent.getChannel().sendFile(ClassLoader.getSystemResourceAsStream("Patat.jpg"), "Patat.jpg").submit();
    }
}
