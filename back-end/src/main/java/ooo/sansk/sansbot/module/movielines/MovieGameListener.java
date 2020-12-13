package ooo.sansk.sansbot.module.movielines;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.requests.RestAction;
import nl.imine.vaccine.annotation.AfterCreate;
import nl.imine.vaccine.annotation.Component;

import java.util.stream.Collectors;

@Component
public class MovieGameListener implements EventListener {

    private final JDA jda;
    private final MovieGameManager movieGameManager;

    public MovieGameListener(JDA jda, MovieGameManager movieGameManager) {
        this.jda = jda;
        this.movieGameManager = movieGameManager;
    }

    @AfterCreate
    public void postConstruct() {
        jda.addEventListener(this);
    }

    @Override
    public void onEvent(GenericEvent genericEvent) {
        if (!(genericEvent instanceof GuildMessageReactionAddEvent)) {
            return;
        }
        GuildMessageReactionAddEvent guildMessageReactionAddEvent = (GuildMessageReactionAddEvent) genericEvent;
        if (guildMessageReactionAddEvent.getUser().equals(jda.getSelfUser())) {
            return;
        }
        movieGameManager
            .getGame(guildMessageReactionAddEvent.getMessageId())
            .ifPresent(game -> {
                if (game.getReactedUsers().contains(game.getMessageId())) {
                    return;
                }
                game.getReactedUsers().add(game.getMessageId());
                if (game.getWinningEmote().equals(guildMessageReactionAddEvent.getReaction().getReactionEmote().getAsReactionCode())) {
                    final String text = String.join("\n", game.getLines().stream().map(Line::getText).collect(Collectors.toSet()));

                    MessageEmbed embed = new EmbedBuilder().setTitle(guildMessageReactionAddEvent.getMember().getEffectiveName() + " is de beste! :sunglasses:")
                        .setThumbnail(guildMessageReactionAddEvent.getUser().getAvatarUrl())
                        .addField(game.getWinningOption().getTitle() + ", " + game.getLines().peek().getTime(), text, false)
                        .build();
                    guildMessageReactionAddEvent.getReaction().getTextChannel().editMessageById(guildMessageReactionAddEvent.getMessageId(), embed).queue();
                } else {
                    final String text = String.join("\n", game.getLines().stream().map(Line::getText).collect(Collectors.toSet()));

                    MessageEmbed embed = new EmbedBuilder().setTitle("Ha! " + guildMessageReactionAddEvent.getMember().getEffectiveName() + " you suck! :-1:")
                        .setThumbnail("https://twemoji.maxcdn.com/v/13.0.1/72x72/274c.png")
                        .addField(game.getWinningOption().getTitle() + ", " + game.getLines().peek().getTime(), text, false)
                        .build();

                    guildMessageReactionAddEvent.getReaction().getTextChannel().editMessageById(guildMessageReactionAddEvent.getMessageId(), embed).queue();
                }
                guildMessageReactionAddEvent.getChannel().retrieveMessageById(guildMessageReactionAddEvent.getMessageId()).submit()
                    .thenAccept(message -> message.getReactions().stream().map(MessageReaction::clearReactions).forEach(RestAction::queue));
                movieGameManager.removeGame(game.getMessageId());
            });
    }
}
