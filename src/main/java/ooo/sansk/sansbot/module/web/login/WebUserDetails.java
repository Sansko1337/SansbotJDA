package ooo.sansk.sansbot.module.web.login;

public class WebUserDetails {

    private final String username;
    private final String avatarUrl;

    public WebUserDetails(String username, String avatarUrl) {
        this.username = username;
        this.avatarUrl = avatarUrl;
    }

    public String getUsername() {
        return username;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }
}
