package ooo.sansk.sansbot.voice.commands;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.VoiceChannel;
import ooo.sansk.sansbot.command.Command;
import ooo.sansk.sansbot.command.CommandHandler;

public abstract class AbstractMusicCommand extends Command {


    public AbstractMusicCommand(CommandHandler commandHandler) {
        super(commandHandler);
    }

    public boolean isInSameChannel(Member member, Guild guild) {
        VoiceChannel voiceChannel = member.getVoiceState().getChannel();
        VoiceChannel botChannel = guild.getSelfMember().getVoiceState().getChannel();
        return voiceChannel != null && botChannel != null && member.getVoiceState().getChannel().equals(guild.getSelfMember().getVoiceState().getChannel());
    }
}
