package ooo.sansk.sansbot.voice.commands;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.VoiceChannel;
import ooo.sansk.sansbot.command.Command;
import ooo.sansk.sansbot.command.CommandHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractMusicCommand extends Command {

    private static final Logger logger = LoggerFactory.getLogger(AbstractMusicCommand.class);

    public AbstractMusicCommand(CommandHandler commandHandler) {
        super(commandHandler);
    }

    public boolean isInSameChannel(Member member, Guild guild) {
        if(member != null && guild != null) {
            VoiceChannel voiceChannel = member.getVoiceState().getChannel();
            VoiceChannel botChannel = guild.getSelfMember().getVoiceState().getChannel();
            return voiceChannel != null && botChannel != null && member.getVoiceState().getChannel().equals(guild.getSelfMember().getVoiceState().getChannel());
        } else {
            //Even though Sansbot only runs in one guild at the moment, we should not use that fact to make assumptions. This probably needs some other handling as well
            logger.warn("Not known which guild this command needs to be ran for (nor which member this would be)");
            return false;
        }
    }

    //TODO Add private conversation check
}
