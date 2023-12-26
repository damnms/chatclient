package chatclient.entities;

import java.io.InputStream;
import java.net.URL;

public class ChatAppUser {

    private final String username;
    private final String password;
    private boolean loggedIn;
    private InputStream chatInputStream;
    private URL chatStreamUrl;
    private URL chatPostUrl;
    private String internalPass;

    public ChatAppUser(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setLoggedIn() {
        loggedIn = true;
    }

    public InputStream getChatstream() {
        return chatInputStream;
    }

    public void setChatstream(InputStream chatstream) {
        this.chatInputStream = chatstream;
    }

    public URL getChatPostUrl() {
        return chatPostUrl;
    }

    public void setChatPostUrl(URL chatPostUrl) {
        this.chatPostUrl = chatPostUrl;
    }

    public String getInternalPass() {
        return internalPass;
    }

    public void setInternalPass(String internalPass) {
        this.internalPass = internalPass;
    }

    public void setChatUrl(URL url1) {
        this.chatStreamUrl = url1;
    }

    public URL getChatStreamUrl() {
        return chatStreamUrl;
    }
}
