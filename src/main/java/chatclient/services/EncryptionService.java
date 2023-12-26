package chatclient.services;

import chatclient.entities.Message;

public interface EncryptionService {
    Message decrypt(Message s) throws KeyNotAvailableException;

    String getOurKey();

    void registerKey(Message s);

    String encrypt(String s);

    boolean keyForUserExists(String receiver);
}
