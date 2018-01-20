package ooo.sansk.sansbot.command;

import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Channel;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.utils.PermissionUtil;
import nl.imine.vaccine.annotation.AfterCreate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public abstract class Command {

    private static final Logger logger = LoggerFactory.getLogger(Command.class);

    protected final CommandHandler commandHandler;

    public Command(CommandHandler commandHandler) {
        this.commandHandler = commandHandler;
    }

    @AfterCreate
    public void afterCreation(){
        logger.trace("Registering command: {}", getClass().getSimpleName());
        commandHandler.registerCommand(this);
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

    public void reply(TextChannel original, Message reply) {
        if(original.getType().equals(ChannelType.PRIVATE) || original.getType().equals(ChannelType.GROUP)) {
            original.sendMessage(reply).queue();
        } else {
            commandHandler.getDefaultOutputChannel().sendMessage(reply).queue();
        }
    }

    public void reply(TextChannel original, String reply) {
        reply(original, new MessageBuilder(reply).build());
    }
}
