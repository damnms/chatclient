package chatclient;

import chatclient.entities.Message;

import java.util.List;

public interface MessageReceiver {

    void append(Message s);

    void init(String s);

    void userListChanged(List<String> userList);

    void appendPrivateMessage(Message message);
}
