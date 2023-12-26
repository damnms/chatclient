package chatclient.services;

import chatclient.entities.ChatAppUser;
import chatclient.entities.Message;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class MessageService {

    private final String ourself;
    private final ChatAppUser chatAppUser;

    public MessageService(ChatAppUser chatAppUser) {
        this.chatAppUser = chatAppUser;
        ourself = chatAppUser.getUsername();
    }

    public String getOurself() {
        return ourself;
    }

    public void sendMessage(ChatAppUser chatAppUser, String text) {

        final String message = URLEncoder.encode(text, StandardCharsets.ISO_8859_1);
        final String formData = "AutoScroll=on&user=" + chatAppUser.getUsername() + "&pass=" + chatAppUser.getInternalPass() + "&cid=synapse&message=" + message;
        final byte[] formBytes = formData.getBytes(StandardCharsets.ISO_8859_1);

        try {
            final HttpURLConnection urlConnection = openConnection(chatAppUser.getChatPostUrl());
            internalSendRequest(urlConnection, formBytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //todo: schoeneren namen finden und trennen oder so. is nur hier damit ichs testen kann -> smell?
    protected void internalSendRequest(HttpURLConnection urlConnection, byte[] formBytes) throws IOException {
        urlConnection.setRequestMethod("POST");
        urlConnection.setDoOutput(true);
        urlConnection.setRequestProperty("Content-Type", "Content-Type: application/x-www-form-urlencoded");
        urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/84.0.4147.89 Safari/537.36");
        urlConnection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
        urlConnection.setRequestProperty("Content-Length", formBytes.length + "");
        urlConnection.setFixedLengthStreamingMode(formBytes.length);
        urlConnection.connect();
        DataOutputStream outputStream = new DataOutputStream(urlConnection.getOutputStream());
        outputStream.write(formBytes);
        outputStream.close();
        urlConnection.disconnect(); //todo: brauchts das wirklich?
    }

    protected HttpURLConnection openConnection(URL chatPostUrl) {
        try {
            return (HttpURLConnection) chatPostUrl.openConnection();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void requestKeyFor(String receiver, ChatAppUser chatAppUser, String ourKey) {
        String sendCmd = "/pm " + receiver + " " + Message.encRequestKeyPrefix + ourKey;
        sendMessage(chatAppUser, sendCmd);
    }

    public void sendPrivateMessageTo(ChatAppUser chatAppUser, String receiver, String text) {
        String sendCmd = "/pm " + receiver + " " + text;
        sendMessage(chatAppUser, sendCmd);
    }

    public ChatAppUser getChatAppUser() {
        return chatAppUser;
    }
}
