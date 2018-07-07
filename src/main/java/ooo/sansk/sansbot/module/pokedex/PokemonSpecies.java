package ooo.sansk.sansbot.module.pokedex;

public class PokemonSpecies {

    private final String genus;
    private final String description;

    public PokemonSpecies(String genus, String description) {
        this.genus = genus;
        this.description = description;
    }

    public String getGenus() {
        return genus;
    }

    public String getDescription() {
        return description;
    }
}
