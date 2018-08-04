package ooo.sansk.sansbot.module.music.commands.playlist;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import nl.imine.vaccine.annotation.Component;
import ooo.sansk.sansbot.command.ChatCommandHandler;
import ooo.sansk.sansbot.module.music.PlayListService;
import ooo.sansk.sansbot.module.music.commands.AbstractMusicChatCommand;
import ooo.sansk.sansbot.module.music.playlist.PlayList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class CreatePlayListCommand extends AbstractMusicChatCommand {

    private final PlayListService playListService;

    public CreatePlayListCommand(ChatCommandHandler chatCommandHandler, PlayListService playListService) {
        super(chatCommandHandler);
        this.playListService = playListService;
    }

    @Override
    public List<String> getTriggers() {
        return Collections.singletonList("CreatePlaylist");
    }

    @Override
    public void handle(MessageReceivedEvent messageReceivedEvent) {
        deleteMessageIfPossible(messageReceivedEvent.getMessage());
        String[] commandContent = messageReceivedEvent.getMessage().getContentRaw().split(" ");
        if (commandContent.length < 2) {
            reply(messageReceivedEvent.getChannel(), "Not enough arguments");
            return;
        }
        if (commandContent.length > 2) {
            reply(messageReceivedEvent.getChannel(), "Too many arguments");
            return;
        }
        String playListId = commandContent[1];
        if (playListService.findOne(playListId).isPresent()) {
            reply(messageReceivedEvent.getChannel(), "Bestaat al");
            return;
        }
        PlayList playList = new PlayList(playListId, new ArrayList<>());
        playListService.addOne(playList);
        chatCommandHandler.getDefaultOutputChannel().sendMessage("Nieuwe playlist, yay: " + playListId).queue();
    }
}
