package ooo.sansk.sansbot.voice.commands;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import nl.imine.vaccine.annotation.AfterCreate;
import nl.imine.vaccine.annotation.Component;
import ooo.sansk.sansbot.command.Command;
import ooo.sansk.sansbot.command.CommandHandler;
import ooo.sansk.sansbot.voice.VoiceHandler;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Component
public class PlayListCommand implements Command {

    private final CommandHandler commandHandler;
    private final VoiceHandler voiceHandler;

    public PlayListCommand(CommandHandler commandHandler, VoiceHandler voiceHandler) {
        this.commandHandler = commandHandler;
        this.voiceHandler = voiceHandler;
    }

    @AfterCreate
    public void afterCreation() {
        commandHandler.registerCommand(this);
    }


    @Override
    public List<String> getTriggers() {
        return Arrays.asList("playlist", "tracks", "commingup", "KomtErNogWatLeuksAan");
    }

    @Override
    public void handle(MessageReceivedEvent messageReceivedEvent) {
        messageReceivedEvent.getMessage().delete().queue();
        if(!voiceHandler.getQueue().isEmpty()) {
            EmbedBuilder embedBuilder = new EmbedBuilder().setTitle(":cd: Playlist");
            voiceHandler.getQueue().stream()
                    .limit(25)
                    .map(AudioTrack::getInfo)
                    .forEach(track -> embedBuilder.addField(track.title + " | " + track.length, track.author + " (" + track.uri + ")", false));
            messageReceivedEvent.getChannel().sendMessage(String.format("Hier %s, dit zijn nummers die er nog aan zullen komen!", messageReceivedEvent.getAuthor().getAsMention())).embed(embedBuilder.build()).queue();
        } else {
            messageReceivedEvent.getChannel().sendMessage(String.format("Sorry %s, maar er staat nog niks op de lijst. Misschien kan je zelf wat toevoegen!", messageReceivedEvent.getAuthor().getAsMention())).queue();
        }
    }

    public String getReadableTimeString(long millis) {
        Date resultDate = new Date(millis);
        return new SimpleDateFormat("HH:mm:ss").format(resultDate);
    }
}
