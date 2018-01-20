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
public class ResumeTrackCommand extends AbstractMusicCommand {

    private final VoiceHandler voiceHandler;

    public ResumeTrackCommand(CommandHandler commandHandler, VoiceHandler voiceHandler) {
        super(commandHandler);
        this.voiceHandler = voiceHandler;
    }

    @Override
    public List<String> getTriggers() {
        return Arrays.asList("resume", "DraaiMaarDoor", "WasTochNietZoBelangrijk");
    }

    @Override
    public void handle(MessageReceivedEvent messageReceivedEvent) {
        deleteMessageIfPossible(messageReceivedEvent.getMessage());
        if(isInSameChannel(messageReceivedEvent.getMember(), messageReceivedEvent.getGuild())) {
            if (voiceHandler.resume()) {
                commandHandler.getDefaultOutputChannel().sendMessage(String.format("En volgens %s kan het feestje weer beginnen! :tada:", messageReceivedEvent.getAuthor().getAsMention())).queue();
            } else {
                reply(messageReceivedEvent.getTextChannel(), String.format("Zeg %s, ik kan niks resumen als er verder niks af te spelen valt! :angry:", messageReceivedEvent.getAuthor().getAsMention()));
            }
        } else {
            reply(messageReceivedEvent.getTextChannel(), String.format("Yo %s, ik geloof niet dat jij kan weten of men alweer zin heeft in de muziek... :confused:", messageReceivedEvent.getAuthor().getAsMention()));
        }
    }
}
