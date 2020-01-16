package ooo.sansk.sansbot.module.pokedex;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import nl.imine.vaccine.annotation.Component;
import ooo.sansk.sansbot.command.ChatCommand;
import ooo.sansk.sansbot.command.ChatCommandHandler;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class PokemonCommand extends ChatCommand {

    private static final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private final PokedexAPI pokedexAPI;

    public PokemonCommand(ChatCommandHandler chatCommandHandler, PokedexAPI pokedexAPI) {
        super(chatCommandHandler);
        this.pokedexAPI = pokedexAPI;
    }

    @Override
    public List<String> getTriggers() {
        return Arrays.asList("Pokedex", "Pokédex", "Pokemon", "Pokémon");
    }

    @Override
    public void handle(MessageReceivedEvent messageReceivedEvent) {
        if (messageReceivedEvent.getMessage().getContentRaw().split(" ").length > 1)
            executorService.submit(() -> {
                Optional<Pokemon> oPokemon = pokedexAPI.getPokemon(messageReceivedEvent.getMessage().getContentRaw().split(" ")[1].toLowerCase());
                if (oPokemon.isPresent()) {
                    Pokemon pokemon = oPokemon.get();
                    EmbedBuilder embedBuilder = new EmbedBuilder().setAuthor("Pokédex", PokedexAPI.BASE_URL, PokedexAPI.POKEDEX_ICON)
                            .setTitle(String.format("#%s: %s", String.valueOf(pokemon.getId()), capitalize(pokemon.getName())))
                            .addField("Primary Type", capitalize(pokemon.getPrimaryType().name()), true);
                    pokemon.getSecondaryType().ifPresent(type -> embedBuilder.addField("Secondary Type", capitalize(type.name()), true));
                    embedBuilder.addField("Description", pokemon.getDescription(), false)
                            .setThumbnail(pokemon.getSpriteUrl())
                            .setColor(pokemon.getPrimaryType().getColor())
                            .setFooter("Pokédex data retrieved from " + PokedexAPI.BASE_URL, PokedexAPI.POKEDEX_ICON);
                    messageReceivedEvent.getChannel().sendMessage(new MessageBuilder(embedBuilder).build()).queue();
                } else {
                    messageReceivedEvent.getChannel().sendMessage(new MessageBuilder("Pokemon not found").build()).queue();
                }
            });
        deleteMessageIfPossible(messageReceivedEvent.getMessage());
    }

    private String capitalize(String string) {
        return string.toLowerCase().substring(0, 1).toUpperCase() + string.substring(1);
    }

}
