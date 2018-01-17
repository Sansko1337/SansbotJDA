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
public class ResumeTrackCommand implements Command {

    private final CommandHandler commandHandler;
    private final VoiceHandler voiceHandler;

    public ResumeTrackCommand(CommandHandler commandHandler, VoiceHandler voiceHandler) {
        this.commandHandler = commandHandler;
        this.voiceHandler = voiceHandler;
    }

    @AfterCreate
    public void afterCreation() {
        commandHandler.registerCommand(this);
    }


    @Override
    public List<String> getTriggers() {
        return Arrays.asList("resume", "DraaiMaarDoor", "WasTochNietZoBelangrijk");
    }

    @Override
    public void handle(MessageReceivedEvent messageReceivedEvent) {
        messageReceivedEvent.getMessage().delete().queue();
        if(voiceHandler.resume()) {
            messageReceivedEvent.getChannel().sendMessage(String.format("En volgens %s kan het feestje weer beginnen! :tada:", messageReceivedEvent.getAuthor().getAsMention())).queue();
        } else {
            messageReceivedEvent.getChannel().sendMessage(String.format("Zeg %s, ik kan niks resumen als er verder niks af te spelen valt! :angry:", messageReceivedEvent.getAuthor().getAsMention())).queue();
        }
    }
}
