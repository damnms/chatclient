package chatclient;

import chatclient.entities.Message;

import java.util.function.Consumer;

public interface Updateable extends Consumer<Message> {

    /**
     * Set the UI so the user can see that the chat is encrypted. In PrivChatWindow its the Encrypt?-Button.
     */
    void encryptionSuccessful();

}
