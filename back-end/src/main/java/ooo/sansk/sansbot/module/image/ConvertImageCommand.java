package ooo.sansk.sansbot.module.image;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.requests.Route;
import net.dv8tion.jda.core.requests.restaction.MessageAction;
import nl.imine.vaccine.annotation.Component;
import ooo.sansk.sansbot.command.ChatCommand;
import ooo.sansk.sansbot.command.ChatCommandHandler;
import ooo.sansk.sansbot.module.image.filter.FilterType;
import ooo.sansk.sansbot.module.image.filter.ImageFilter;
import ooo.sansk.sansbot.module.image.filter.InvertColorImageFilter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
public class ConvertImageCommand extends ChatCommand {

    private static final int MAX_FILE_SIZE_BYTES = 8388608;

    public ConvertImageCommand(ChatCommandHandler chatCommandHandler) {
        super(chatCommandHandler);
    }

    @Override
    public List<String> getTriggers() {
        return Arrays.asList("i", "convert");
    }

    @Override
    public void handle(MessageReceivedEvent messageReceivedEvent) {
        String[] args = messageReceivedEvent.getMessage().getContentRaw().split(" ");
        if(args.length < 2) {
            reply(messageReceivedEvent.getChannel(), "Ik heb geen idee wat je van me mot, %s...");
            return;
        }
        if(args.length > 3) {
            reply(messageReceivedEvent.getChannel(), "Ja ehh, niet zoveel tegelijk hè, %s");
            return;
        }

        Optional<ImageFilter> oFilter = FilterType.getFilter(args[1]).map(FilterType::getFilter);
        if (!oFilter.isPresent()) {
            reply(messageReceivedEvent.getChannel(), "Ik kan een hoop, maar dat nou net weer niet. %s");
            return;
        }

        messageReceivedEvent.getChannel().getHistory().retrievePast(10).queue(messages -> {
            Optional<BufferedImage> oBufferedImage = getFirstLoadedMessageWithImageAttachment(messages);

            if (oBufferedImage.isPresent()) {
                ImageResult imageResult = oFilter.get().doFilter(oBufferedImage.get());
                if (imageResult.getImageData().length < MAX_FILE_SIZE_BYTES) {
                    MessageAction messageAction = messageReceivedEvent.getChannel().sendFile(imageResult.getImageData(), "output." + imageResult.getImageType());
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
        });
    }

    private Optional<BufferedImage> getFirstLoadedMessageWithImageAttachment(List<Message> messages) {
        return messages
                .stream()
                .flatMap(message -> message.getAttachments().stream())
                .filter(Message.Attachment::isImage)
                .findFirst()
                .map(this::downloadImageFromAttachment);
    }

    private BufferedImage downloadImageFromAttachment(Message.Attachment attachment) {
        try {
            return ImageIO.read(attachment.getInputStream());
        } catch (IOException e) {
            return null;
        }
    }
}
