package ooo.sansk.sansbot.module.image;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.requests.restaction.MessageAction;
import nl.imine.vaccine.annotation.Component;
import ooo.sansk.sansbot.command.ChatCommand;
import ooo.sansk.sansbot.command.ChatCommandHandler;
import ooo.sansk.sansbot.module.image.filter.InvertColorImageFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
public class ConvertImageCommand extends ChatCommand {

    private static final Logger logger = LoggerFactory.getLogger(ConvertImageCommand.class);

    public ConvertImageCommand(ChatCommandHandler chatCommandHandler) {
        super(chatCommandHandler);
    }

    @Override
    public List<String> getTriggers() {
        return Arrays.asList("i", "convert");
    }

    @Override
    public void handle(MessageReceivedEvent messageReceivedEvent) {
        Optional<BufferedImage> oBufferedImage = messageReceivedEvent.getMessage().getAttachments().stream()
                .filter(Message.Attachment::isImage)
                .findFirst()
                .map(this::downloadImageFromAttachment);

        if (oBufferedImage.isPresent()) {
            ImageResult imageResult = new InvertColorImageFilter().doFilter(oBufferedImage.get());
            if(imageResult.getImageData().length < 8388608) {
                MessageAction messageAction = messageReceivedEvent.getChannel().sendFile(imageResult.getImageData(), "inverted.png");
                if (messageReceivedEvent.getChannel().getType().isGuild()) {
                    messageAction = messageAction.content(messageReceivedEvent.getMember().getAsMention());
                }
                messageAction.submit();
            } else {
                reply(messageReceivedEvent.getChannel(), String.format("Oh nee! deze is te groot voor mij, %s :confounded:", messageReceivedEvent.getAuthor().getAsMention()));
            }
        } else {
            reply(messageReceivedEvent.getChannel(), String.format("Ge snappe het volgensmij niet helemaal hè, %s. Ge motten wel een plaatje erbij uploaden hè? :angry:", messageReceivedEvent.getAuthor().getAsMention()));
        }
    }

    private BufferedImage downloadImageFromAttachment(Message.Attachment attachment) {
        try {
            return ImageIO.read(attachment.getInputStream());
        } catch (IOException e) {
            return null;
        }
    }
}
