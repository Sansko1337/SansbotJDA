package ooo.sansk.sansbot.module.movielines;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import nl.imine.vaccine.annotation.Component;
import ooo.sansk.sansbot.command.ChatCommand;
import ooo.sansk.sansbot.command.ChatCommandHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class MovieQuoteCommand extends ChatCommand {

    private static final Logger logger = LoggerFactory.getLogger(MovieQuoteCommand.class);

    private final MovieGameManager movieGameManager;

    public MovieQuoteCommand(ChatCommandHandler chatCommandHandler, MovieGameManager movieGameManager) {
        super(chatCommandHandler);
        this.movieGameManager = movieGameManager;
    }

    @Override
    public List<String> getTriggers() {
        return Arrays.asList("moviequote", "movieguess", "movie");
    }

    @Override
    public void handle(MessageReceivedEvent messageReceivedEvent) {
        movieGameManager.createGame().ifPresentOrElse(game -> {
            List<Movie> movies = new ArrayList<>();
            movies.add(game.getWinningOption());
            movies.add(game.getFirstAlternativeOption());
            movies.add(game.getSecondAlternativeOption());
            Collections.shuffle(movies);

            int winningIndex = 0;
            for (int i = 0; i < movies.size(); i++) {
                if (game.getWinningOption().equals(movies.get(i))) {
                    winningIndex = i;
                }
            }
            game.setWinningEmote(getEmoteFromIndex(winningIndex));

            final String text = String.join("\n", game.getLines().stream().map(Line::getText).collect(Collectors.toSet()));
            MessageEmbed embed = new EmbedBuilder().setTitle("Filmquote spelletje ding!")
                .setThumbnail("https://fonts.gstatic.com/s/i/materialicons/movie_filter/v6/24px.svg")
                .addField("Uit welk van deze films komen deze lines?", text, false)
                .addField("", getEmoteFromIndex(0) + "\t" + movies.get(0).getTitle(), false)
                .addField("", getEmoteFromIndex(1) + "\t" + movies.get(1).getTitle(), false)
                .addField("", getEmoteFromIndex(2) + "\t" + movies.get(2).getTitle(), false)
                .build();
            messageReceivedEvent.getMessage().getChannel().sendMessage(embed).submit()
                .thenAccept(message -> {
                    game.setMessageId(message.getId());
                    message.addReaction(getEmoteFromIndex(0)).queue();
                    message.addReaction(getEmoteFromIndex(1)).queue();
                    message.addReaction(getEmoteFromIndex(2)).queue();
                });
        }, () -> messageReceivedEvent.getMessage().getChannel().sendMessage("Dit spelleke werkt op het moment niet. Helaas, duo penotti").submit());
        deleteMessageIfPossible(messageReceivedEvent.getMessage());
    }

    private String getEmoteFromIndex(int index) {
        return switch (index) {
            case 0 -> "1\uFE0F\u20E3";
            case 1 -> "2\uFE0F\u20E3";
            case 2 -> "3\uFE0F\u20E3";
            default -> null;
        };
    }
}
