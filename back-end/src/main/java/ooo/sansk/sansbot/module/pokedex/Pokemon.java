package ooo.sansk.sansbot.module.pokedex;

import java.util.Optional;

public class Pokemon {

    private final int id;
    private final String name;
    private final PokemonType primaryType;
    private final PokemonType secondaryType;
    private final String genus;
    private final String description;
    private final String spriteUrl;

    public Pokemon(int id, String name, PokemonType primaryType, PokemonType secondaryType, String genus, String description, String spriteUrl) {
        this.id = id;
        this.name = name;
        this.primaryType = primaryType;
        this.secondaryType = secondaryType;
        this.genus = genus;
        this.description = description;
        this.spriteUrl = spriteUrl;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public PokemonType getPrimaryType() {
        return primaryType;
    }

    public Optional<PokemonType> getSecondaryType() {
        return Optional.ofNullable(secondaryType);
    }

    public String getGenus() {
        return genus;
    }

    public String getDescription() {
        return description;
    }

    public String getSpriteUrl() {
        return spriteUrl;
    }
}
