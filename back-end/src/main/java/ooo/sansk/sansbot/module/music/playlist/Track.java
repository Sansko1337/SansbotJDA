package ooo.sansk.sansbot.module.music.playlist;

public class Track {

    private final String title;
    private final String author;
    private final String source;

    public Track(String title, String author, String source) {
        this.title = title;
        this.author = author;
        this.source = source;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getSource() {
        return source;
    }
}
