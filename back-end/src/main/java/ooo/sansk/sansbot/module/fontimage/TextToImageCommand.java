package ooo.sansk.sansbot.module.fontimage;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import nl.imine.vaccine.annotation.Component;
import ooo.sansk.sansbot.command.ChatCommand;
import ooo.sansk.sansbot.command.ChatCommandHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
public class TextToImageCommand extends ChatCommand {

    private static final Logger logger = LoggerFactory.getLogger(TextToImageCommand.class);

    private final TextToImageConverter textToImageConverter;

    public TextToImageCommand(ChatCommandHandler chatCommandHandler, TextToImageConverter textToImageConverter) {
        super(chatCommandHandler);
        this.textToImageConverter = textToImageConverter;
    }

    @Override
    public List<String> getTriggers() {
        return Collections.singletonList("tti");
    }

    @Override
    public void handle(MessageReceivedEvent messageReceivedEvent) {
        deleteMessageIfPossible(messageReceivedEvent.getMessage());
        String[] args = messageReceivedEvent.getMessage().getContentRaw().split(" ");
        if (args.length < 3) {
            reply(messageReceivedEvent.getChannel(), String.format("Ik krijg nog een lettertype EN tekst van je! Wil je soms met een blok beton aan je voeten het water in, %s? :angry:", messageReceivedEvent.getAuthor().getAsMention()));
            return;
        }

        String font = args[1];
        String text = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
        TextToImageConversionResult textToImageConversionResult = textToImageConverter.convertText(font, text);
        if (textToImageConversionResult.isSuccessful()) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try {
                ImageIO.write(textToImageConversionResult.getOutput(), "png", baos);
                messageReceivedEvent.getChannel()
                        .sendFile(baos.toByteArray(), "tti.png")
                        .content(messageReceivedEvent.getMember().getAsMention())
                        .submit();
            } catch (IOException e) {
                logger.error("An error occurred while writing the image to Discord ({}: {})", e.getClass().getSimpleName(), e.getMessage());
                reply(messageReceivedEvent.getChannel(), String.format("Ik zal eerlijk zijn %s.... Je hebt het gesloopt! :upside_down:", messageReceivedEvent.getAuthor().getAsMention()));
            }
        } else {
            reply(messageReceivedEvent.getChannel(), String.format("Dat letterype bestaat niet, %s! :confused:", messageReceivedEvent.getAuthor().getAsMention()));
        }


    }
}
