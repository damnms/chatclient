package chatclient.services;

import chatclient.entities.Message;

import java.util.HashMap;
import java.util.Map;

public class DummyEncryptionService implements EncryptionService {

    private final MessageService messageService;
    private final Map<String, String> userKeyList = new HashMap<>();

    public DummyEncryptionService(MessageService messageService) {
        this.messageService = messageService;
        userKeyList.put(messageService.getOurself(), getOurKey());
    }

    @Override
    public Message decrypt(Message s) throws KeyNotAvailableException {

        String sender = s.getSenderForPm().trim();
        String key = "";
        if (userKeyList.containsKey(sender)) {
            key = userKeyList.get(sender);
            String encryptedText = s.getMsgFromPm().replaceFirst(Message.encMessagePrefix, "");
            StringXORer stringXORer = new StringXORer();
            String decode = stringXORer.decode(encryptedText, key);
            s.replaceEncryptedTextWith(decode);
        } else {
            messageService.requestKeyFor(sender, messageService.getChatAppUser(), getOurKey());
            throw new KeyNotAvailableException("The key for " + sender + " is not yet at us, please re-send that request!");
        }


        return s;
    }

    @Override
    public String getOurKey() {
        return "lolyeah";
    }

    @Override
    public void registerKey(Message s) {
        userKeyList.put(s.getSenderForPm(), s.getMsgFromPm().replace(Message.encSendKeyPrefix, "").trim());
    }

    @Override
    public String encrypt(String s) {
        StringXORer stringXORer = new StringXORer();
        return stringXORer.encode(s, getOurKey());
    }

    @Override
    public boolean keyForUserExists(String receiver) {
        return userKeyList.containsKey(receiver);
    }

}
