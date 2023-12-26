package chatclient.gui;

import chatclient.ChatFramework;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class PrivChatWindow {

    private final String user;
    final Stage dialog = new Stage();
    final WebView privateChatWebView = new WebView();
    private final Button encryptionButton = new Button("Encrypt?");

    public PrivChatWindow(Stage parent, ChatFramework chatFramework, String user) {
        this.user = user;
        privateChatWebView.getEngine().setJavaScriptEnabled(true);

        final String init = """
                <html>
                  <head>
                    <script language="JavaScript">function appendText(extraStr) {
                      document.getElementsByTagName('body')[0].innerHTML = document.getElementsByTagName('body')[0].innerHTML + extraStr;
                    }</script>
                  </head>
                  <body>
                  </body>
                </html>
                """;
        FXApplication.runInFxThread(() -> privateChatWebView.getEngine().loadContent(init));
        FXApplication.runInFxThread(() -> privateChatWebView.getEngine().executeScript("window.scrollTo(0, document.body.scrollHeight);"));


        dialog.initOwner(parent);
        dialog.setTitle("Private chat with " + user);
        final TextField dialogInputField = new TextField("input text here...");
        dialogInputField.setOnMouseClicked(event1 -> dialogInputField.setText(""));

        dialogInputField.setOnKeyPressed(event1 -> {
            if (event1.getCode() == KeyCode.ENTER) {
                chatFramework.sendPrivateMessage(user, dialogInputField.getText());
                dialogInputField.clear();
            }
        });

        encryptionButton.setOnMouseClicked(event -> {
            chatFramework.startEncryptionWith(user);
        });
        final VBox dialogVbox = new VBox(20);
        dialogVbox.getChildren().add(privateChatWebView);
        dialogVbox.getChildren().add(encryptionButton);
        dialogVbox.getChildren().add(dialogInputField);
        final Scene dialogScene = new Scene(dialogVbox, 300, 200);
        dialog.setScene(dialogScene);

    }

    public void setEncryptionEnabled() {
        encryptionButton.setText("Encrypted");
    }

    public String user() {
        return user;
    }

    public void setVisible() {
        dialog.show();
    }

    public void addText(String s) {

        String regexToRemoveFluester = "<span onclick=.*\">";

        final String text = s.replaceAll(regexToRemoveFluester, "");
        Runnable runnable = () -> {
            privateChatWebView.getEngine().executeScript("appendText(\"" + text + "\");");
        };

        FXApplication.runInFxThread(runnable);
    }
}
