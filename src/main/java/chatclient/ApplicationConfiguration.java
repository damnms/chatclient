package chatclient;

import chatclient.entities.ChatAppUser;
import chatclient.services.DummyEncryptionService;
import chatclient.services.EncryptionService;
import chatclient.services.LoginService;
import chatclient.services.MessageService;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

public class ApplicationConfiguration {

    private MessageService messageService;

    public ChatFramework getChatFramework(ChatAppUser chatAppUser, MessageReceiver fxApplication) {
        final LoginService loginService = getLoginService();
        try {
            loginService.login(new URI("https://server4.webkicks.de/synapse/").toURL(), chatAppUser);
        } catch (MalformedURLException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
        final MessageService messageService = getMessageService(chatAppUser);
        final EncryptionService encryptionService = getEncryptionService(chatAppUser);
        return new ChatFramework(chatAppUser, fxApplication, messageService, encryptionService);
    }

    private EncryptionService getEncryptionService(ChatAppUser chatAppUser) {
        return new DummyEncryptionService(getMessageService(chatAppUser));
    }

    private LoginService getLoginService() {
        return new LoginService();
    }

    private MessageService getMessageService(ChatAppUser chatAppUser) {
        if (this.messageService == null)
            messageService = new MessageService(chatAppUser);
        return messageService;
    }
}
