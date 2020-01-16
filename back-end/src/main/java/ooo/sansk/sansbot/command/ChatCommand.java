package ooo.sansk.sansbot.command;

import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.internal.utils.PermissionUtil;
import nl.imine.vaccine.annotation.AfterCreate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public abstract class ChatCommand {

    private static final Logger logger = LoggerFactory.getLogger(ChatCommand.class);

    protected final ChatCommandHandler chatCommandHandler;

    public ChatCommand(ChatCommandHandler chatCommandHandler) {
        this.chatCommandHandler = chatCommandHandler;
    }

    @AfterCreate
    public void afterCreation(){
        logger.trace("Registering chat command: {}", getClass().getSimpleName());
        chatCommandHandler.registerCommand(this);
    }

    public abstract List<String> getTriggers();

    public abstract void handle(MessageReceivedEvent messageReceivedEvent);

    public void deleteMessageIfPossible(Message message) {
        if (!message.getChannel().getType().equals(ChannelType.TEXT)) {
            logger.trace("Message could not be deleted due to not being in a Guild Text Channel");
            return;
        }
        if (!PermissionUtil.checkPermission(message.getTextChannel(), message.getGuild().getSelfMember(), Permission.MESSAGE_MANAGE)) {
            logger.trace("Message could not be deleted due to not having permission for this channel ()");
            return;
        }
        message.delete().queue();
    }

    public void reply(MessageChannel original, Message reply) {
        if(original.getType().equals(ChannelType.PRIVATE) || original.getType().equals(ChannelType.GROUP)) {
            original.sendMessage(reply).queue();
        } else {
            chatCommandHandler.getDefaultOutputChannel().sendMessage(reply).queue();
        }
    }

    public void reply(MessageChannel original, String reply) {
        reply(original, new MessageBuilder(reply).build());
    }
}
