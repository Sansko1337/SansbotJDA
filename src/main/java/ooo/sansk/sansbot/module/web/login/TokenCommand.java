package ooo.sansk.sansbot.module.web.login;

import net.dv8tion.jda.core.entities.PrivateChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import nl.imine.vaccine.annotation.Component;
import ooo.sansk.sansbot.command.ChatCommand;
import ooo.sansk.sansbot.command.ChatCommandHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;

@Component
public class TokenCommand extends ChatCommand {

    private static final Logger logger = LoggerFactory.getLogger(TokenCommand.class);

    private final LoginService loginService;

    public TokenCommand(ChatCommandHandler chatCommandHandler, LoginService loginService) {
        super(chatCommandHandler);
        this.loginService = loginService;
    }

    @Override
    public List<String> getTriggers() {
        return Collections.singletonList("token");
    }

    @Override
    public void handle(MessageReceivedEvent messageReceivedEvent) {
        deleteMessageIfPossible(messageReceivedEvent.getMessage());
        messageReceivedEvent.getAuthor().openPrivateChannel().queue(
                this::sendUserWebToken,
                error -> handleOpenPrivateChannelError(error, messageReceivedEvent.getAuthor().getName()));
    }

    private void sendUserWebToken(PrivateChannel channel) {
        WebToken token = loginService.createWebToken(channel.getUser().getId());
        channel.sendMessage("Your WebToken is: " + token.getToken()).queue();
    }

    private void handleOpenPrivateChannelError(Throwable e, String username) {
        logger.error(
                "Could not send token to user '{}' due to not being able to open a private channel. Reason: ({}, {})",
                username,
                e.getClass().getSimpleName(),
                e.getMessage()
        );
    }
}
