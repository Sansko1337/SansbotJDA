package ooo.sansk.sansbot.module.music.commands;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import nl.imine.vaccine.annotation.Component;
import ooo.sansk.sansbot.command.ChatCommand;
import ooo.sansk.sansbot.command.ChatCommandHandler;
import ooo.sansk.sansbot.module.music.TrackListManager;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Component
public class PlayListChatCommand extends ChatCommand {

    private final TrackListManager trackListManager;

    public PlayListChatCommand(ChatCommandHandler chatCommandHandler, TrackListManager trackListManager) {
        super(chatCommandHandler);
        this.trackListManager = trackListManager;
    }

    @Override
    public List<String> getTriggers() {
        return Arrays.asList("playlist", "tracks", "commingup", "KomtErNogWatLeuksAan");
    }

    @Override
    public void handle(MessageReceivedEvent messageReceivedEvent) {
        deleteMessageIfPossible(messageReceivedEvent.getMessage());
        if(!trackListManager.getQueue().isEmpty()) {
            EmbedBuilder embedBuilder = new EmbedBuilder().setTitle(":cd: PlayList");
            trackListManager.getQueue().stream()
                    .limit(25)
                    .map(AudioTrack::getInfo)
                    .forEach(track -> embedBuilder.addField(track.title + " | " + track.length, track.author + " (" + track.uri + ")", false));
            reply(messageReceivedEvent.getChannel(), new MessageBuilder(String.format("Hier %s, dit zijn nummers die er nog aan zullen komen!", messageReceivedEvent.getAuthor().getAsMention())).setEmbed(embedBuilder.build()).build());
        } else {
            reply(messageReceivedEvent.getChannel(), String.format("Sorry %s, maar er staat nog niks op de lijst. Misschien kan je zelf wat toevoegen!", messageReceivedEvent.getAuthor().getAsMention()));
        }
    }

    public String getReadableTimeString(long millis) {
        Date resultDate = new Date(millis);
        return new SimpleDateFormat("HH:mm:ss").format(resultDate);
    }
}
