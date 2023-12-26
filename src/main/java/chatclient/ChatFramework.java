package chatclient;

import chatclient.entities.ChatAppUser;
import chatclient.entities.Message;
import chatclient.services.EncryptionService;
import chatclient.services.KeyNotAvailableException;
import chatclient.services.MessageService;

import java.io.IOException;
import java.util.List;

import static chatclient.entities.Message.MESSAGE_TYPE.ENCRYPTED_WHISPER;
import static chatclient.entities.Message.MESSAGE_TYPE.SERVER;

public class ChatFramework implements Updateable {

    private boolean initial = true;
    private String initialContentFromChat = "";
    private final ChatAppUser chatAppUser;
    private final MessageReceiver messageReceiver;

    private final MessageService messageService;
    private final EncryptionService encryptionService;
    private int timeoutCounter = 0;

    public ChatFramework(ChatAppUser chatAppUser, MessageReceiver fxApplication, MessageService messageService, EncryptionService encryptionService) {
        this.chatAppUser = chatAppUser;
        this.messageReceiver = fxApplication;
        this.messageService = messageService;
        this.encryptionService = encryptionService;
    }

    @Override
    public void accept(Message s) {

        if (initial && s.getType().equals(SERVER)) {
            initializeWebView(s);
        } else {
            switch (s.getType()) {
                case TIMEOUT -> handleTimeOutKiller();
                case WHISPER -> appendPrivateMessage(s);
                case LOGINLOGOUT -> {
                    appendMessage(s);
                    handleLoginLogout();
                }
                case ENCRYPT_REQUEST -> {
                    appendMessage(s);
                    if (!s.isSendingMessage())
                        handleEncryptStart(s);
                }
                case KEY_FROM_OTHER -> {
                    appendMessage(s);
                    if (!s.isSendingMessage())
                        handleKeyExchange(s);
                }
                case ENCRYPTED_WHISPER -> {
                    appendMessage(s);
                    if (!s.isSendingMessage())
                        handleEncryptedReceivedWhisper(s);
                }
                default -> appendMessage(s);
            }
        }
    }

    private void handleEncryptedReceivedWhisper(Message s) {
        Message decrypt = null;
        try {
            decrypt = encryptionService.decrypt(s);
        } catch (KeyNotAvailableException e) {
            messageReceiver.append(new Message("Unable to decrypt received whisper!", chatAppUser.getUsername()));
        }
        appendMessage(decrypt);
    }

    private void handleKeyExchange(Message s) {
        encryptionService.registerKey(s);
    }

    private void handleEncryptStart(Message s) {
        encryptionService.registerKey(s);
        messageService.sendPrivateMessageTo(chatAppUser, s.getSenderForPm(), Message.encSendKeyPrefix + encryptionService.getOurKey());
    }

    private void appendPrivateMessage(Message message) {
        if (message.isEncrypted()) {
            Message decrypted = null;
            try {
                decrypted = encryptionService.decrypt(message);
            } catch (KeyNotAvailableException e) {
                messageReceiver.append(new Message("Unable to decrypt received whisper!", chatAppUser.getUsername()));
            }
            messageReceiver.appendPrivateMessage(decrypted);
        } else {
            messageReceiver.appendPrivateMessage(message);
        }
    }

    private void appendMessage(Message s) {
        if (s.isEncrypted() && s.getType().equals(ENCRYPTED_WHISPER)) {
            Message decrypted = null;
            try {
                decrypted = encryptionService.decrypt(s);
            } catch (KeyNotAvailableException e) {
                messageReceiver.append(new Message("Unable to decrypt received whisper!", chatAppUser.getUsername()));
            }
            messageReceiver.append(decrypted);
        } else {
            messageReceiver.append(s);
        }
    }

    private void handleLoginLogout() {
        List<String> strings = Utility.requestUsers();
        messageReceiver.userListChanged(strings);
    }

    private void initializeWebView(Message s) {
        if (s.getRawString().equals("<!-- update!! //-->\n")) {
            //we encountered the last phase of initialisation
            initial = false;
            String jsFunction = "<script language=\"JavaScript\">function appendText(extraStr) {" +
                    "document.getElementsByTagName('body')[0].innerHTML = document.getElementsByTagName('body')[0].innerHTML + extraStr;" +
                    "}</script>";
            String fullString = initialContentFromChat + jsFunction;
            messageReceiver.init(fullString);
        } else {
            //we found some more initial content, but only if not yet initialized!
            initialContentFromChat = initialContentFromChat + s.getRawString();
        }
    }

    private void handleTimeOutKiller() {
        timeoutCounter++;
        if (timeoutCounter <= 3)
            messageService.sendMessage(chatAppUser, "/away afk");
    }

    public void run() {
        StreamReader streamReader = new StreamReader(chatAppUser, this);
        try {
            streamReader.run();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendMessage(String text) {
        messageService.sendMessage(chatAppUser, text);
    }

    public void sendPrivateMessage(String receiver, String text) {
        boolean keyExists = encryptionService.keyForUserExists(receiver);
        if (keyExists) {
            messageService.sendPrivateMessageTo(chatAppUser, receiver, Message.encMessagePrefix + encryptionService.encrypt(text));
        } else {
            messageService.sendPrivateMessageTo(chatAppUser, receiver, text);
        }
    }

    public void exit() {
        messageService.sendMessage(chatAppUser, "/exit");
    }

    public void startEncryptionWith(String user) {
        messageService.requestKeyFor(user, chatAppUser, encryptionService.getOurKey());
    }

    @Override
    public void encryptionSuccessful() {

    }
}
