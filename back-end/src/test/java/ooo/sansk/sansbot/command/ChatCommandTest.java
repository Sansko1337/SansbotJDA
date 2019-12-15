package ooo.sansk.sansbot.command;

import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.entities.impl.GuildImpl;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.requests.restaction.AuditableRestAction;
import net.dv8tion.jda.core.requests.restaction.MessageAction;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ChatCommandTest {

    private static final String MESSAGE_REPLY = "Reply";

    @Mock
    private ChatCommandHandler mockChatCommandHandler;

    @InjectMocks
    private TestCommand subject;

    //In this test the @Before method has been replaced by Mockito's annotation based testing setup.
    //This class requires no additional setup before testing begins, therefor the method is missing.

    @Test
    public void afterCreationRegistersCommandInCommandHandler() {
        subject.afterCreation();

        verify(mockChatCommandHandler).registerCommand(eq(subject));
    }

    @Test
    public void deleteMessageIfPossibleShouldOnlyDeleteInTextChannels() {
        var mockChannel = mock(MessageChannel.class);
        var mockMessage = mock(Message.class);
        doReturn(mockChannel).when(mockMessage).getChannel();
        doReturn(ChannelType.VOICE).when(mockChannel).getType();

        subject.deleteMessageIfPossible(mockMessage);

        verify(mockMessage, never()).delete();
    }

    @Test
    public void deleteMessageIfPossibleShouldOnlyTryToDeleteWithPermission() {
        var mockMessageChannel = mock(MessageChannel.class);
        var mockTextChannel = mock(TextChannel.class);
        var mockMessage = mock(Message.class);
        var mockGuild = mock(GuildImpl.class);
        var mockSelfMember = mock(Member.class);
        var mockRole = mock(Role.class);
        doReturn(mockMessageChannel).when(mockMessage).getChannel();
        doReturn(mockTextChannel).when(mockMessage).getTextChannel();
        doReturn(ChannelType.TEXT).when(mockMessageChannel).getType();
        doReturn(mockGuild).when(mockTextChannel).getGuild();
        doReturn(mockGuild).when(mockMessage).getGuild();
        doReturn(mockGuild).when(mockSelfMember).getGuild();
        doReturn(mockSelfMember).when(mockGuild).getSelfMember();
        doReturn(mockRole).when(mockGuild).getPublicRole();
        doReturn(false).when(mockSelfMember).isOwner();

        subject.deleteMessageIfPossible(mockMessage);

        verify(mockMessage, never()).delete();
    }

    @Test
    public void deleteMessageIfPossibleSucceeds() {
        var mockMessageChannel = mock(MessageChannel.class);
        var mockTextChannel = mock(TextChannel.class);
        var mockMessage = mock(Message.class);
        var mockGuild = mock(GuildImpl.class);
        var mockSelfMember = mock(Member.class);
        var mockAuditableRestAction = mock(AuditableRestAction.class);
        doReturn(mockMessageChannel).when(mockMessage).getChannel();
        doReturn(mockTextChannel).when(mockMessage).getTextChannel();
        doReturn(ChannelType.TEXT).when(mockMessageChannel).getType();
        doReturn(mockGuild).when(mockTextChannel).getGuild();
        doReturn(mockGuild).when(mockMessage).getGuild();
        doReturn(mockGuild).when(mockSelfMember).getGuild();
        doReturn(mockSelfMember).when(mockGuild).getSelfMember();
        doReturn(mockAuditableRestAction).when(mockMessage).delete();
        doReturn(true).when(mockSelfMember).isOwner();

        subject.deleteMessageIfPossible(mockMessage);

        verify(mockAuditableRestAction).queue();
    }

    @Test
    public void replySendToDefaultChannelIfNotInPrivateConversation() {
        var messageCaptor = ArgumentCaptor.forClass(Message.class);
        var mockOriginalChannel = mock(MessageChannel.class);
        var mockMessageAction = mock(MessageAction.class);
        var mockDefaultChannel = mock(TextChannel.class);
        doReturn(mockDefaultChannel).when(mockChatCommandHandler).getDefaultOutputChannel();
        doReturn(mockMessageAction).when(mockDefaultChannel).sendMessage(messageCaptor.capture());
        doReturn(ChannelType.TEXT).when(mockOriginalChannel).getType();

        subject.reply(mockOriginalChannel, MESSAGE_REPLY);

        verify(mockMessageAction).queue();
        assertThat(messageCaptor.getValue(), is(not(nullValue())));
        assertThat(messageCaptor.getValue().getContentRaw(), is(equalTo(MESSAGE_REPLY)));
    }

    @Test
    public void replySendToOriginalChannelWhenInGroupChat() {
        var messageCaptor = ArgumentCaptor.forClass(Message.class);
        var mockOriginalChannel = mock(MessageChannel.class);
        var mockMessageAction = mock(MessageAction.class);
        doReturn(mockMessageAction).when(mockOriginalChannel).sendMessage(messageCaptor.capture());
        doReturn(ChannelType.GROUP).when(mockOriginalChannel).getType();

        subject.reply(mockOriginalChannel, MESSAGE_REPLY);

        verify(mockMessageAction).queue();
        assertNotNull(messageCaptor.getValue());
        assertEquals(messageCaptor.getValue().getContentRaw(), MESSAGE_REPLY);
    }

    @Test
    public void replySendToOriginalChannelWhenInPrivateChat() {
        var messageCaptor = ArgumentCaptor.forClass(Message.class);
        var mockOriginalChannel = mock(MessageChannel.class);
        var mockMessageAction = mock(MessageAction.class);
        doReturn(mockMessageAction).when(mockOriginalChannel).sendMessage(messageCaptor.capture());
        doReturn(ChannelType.PRIVATE).when(mockOriginalChannel).getType();

        subject.reply(mockOriginalChannel, MESSAGE_REPLY);

        verify(mockMessageAction).queue();
        assertThat(messageCaptor.getValue(), notNullValue());
        assertThat(messageCaptor.getValue().getContentRaw(), equalTo(MESSAGE_REPLY));
    }

    private static class TestCommand extends ChatCommand {

        public TestCommand(ChatCommandHandler chatCommandHandler) {
            super(chatCommandHandler);
        }

        @Override
        public List<String> getTriggers() {
            return null;
        }

        @Override
        public void handle(MessageReceivedEvent messageReceivedEvent) {

        }
    }

}
