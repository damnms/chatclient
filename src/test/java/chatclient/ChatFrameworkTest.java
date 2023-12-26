package chatclient;

import chatclient.entities.ChatAppUser;
import chatclient.entities.Message;
import chatclient.services.EncryptionService;
import chatclient.services.MessageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class ChatFrameworkTest {

    ChatFramework sut;
    EncryptionService encryptionService;
    MessageService messageService;
    MessageReceiver messageReceiver;
    ChatAppUser chatAppUser;

    @BeforeEach
    void setUp() {
        //given
        chatAppUser = mock(ChatAppUser.class);
        messageReceiver = mock(MessageReceiver.class);
        messageService = mock(MessageService.class);
        encryptionService = mock(EncryptionService.class);
        sut = new ChatFramework(chatAppUser, messageReceiver, messageService, encryptionService);
    }

    @Test
    void does_handle_keyexchange_on_retrieved_keyxchange_message() {
        //given
        Message s = mock(Message.class);
        when(s.getType()).thenReturn(Message.MESSAGE_TYPE.KEY_FROM_OTHER);
        //when
        sut.accept(s);

        //then
        verify(encryptionService).registerKey(s);
    }

    @Test
    void does_send_public_key_on_start_encryption() {
        //given
        Message s = mock(Message.class);
        when(s.getType()).thenReturn(Message.MESSAGE_TYPE.ENCRYPT_REQUEST);

        //when
        sut.accept(s);

        //then
        verify(messageService).sendPrivateMessageTo(chatAppUser, s.getReceiverForPm(), Message.encSendKeyPrefix + encryptionService.getOurKey());
    }
}