package ooo.sansk.sansbot.module.pokedex;

public class PokemonFlavorText {

    private final String gameVersion;
    private final String language;
    private final String text;

    public PokemonFlavorText(String gameVersion, String language, String text) {
        this.gameVersion = gameVersion;
        this.language = language;
        this.text = text;
    }

    public String getGameVersion() {
        return gameVersion;
    }

    public String getLanguage() {
        return language;
    }

    public String getText() {
        return text;
    }
}
