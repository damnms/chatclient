package chatclient.gui;

import chatclient.ApplicationConfiguration;
import chatclient.ChatFramework;
import chatclient.MessageReceiver;
import chatclient.Utility;
import chatclient.entities.ChatAppUser;
import chatclient.entities.Message;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

public class FXApplication extends Application implements MessageReceiver {

    public static final String EVENT_TYPE_CLICK = "click";

    private final ApplicationConfiguration applicationConfiguration = new ApplicationConfiguration();

    private final WebView webView = new WebView();
    ChatFramework chatFramework;
    Stage primaryStage;
    final ListView<String> userListView = new ListView<>();
    final ObservableList<String> observableUserList = FXCollections.observableArrayList(Utility.requestUsers());
    final List<PrivChatWindow> privChatWindows = new ArrayList<>();
    private final ExecutorService es = Executors.newSingleThreadExecutor();

    private boolean initialized = false;

    public FXApplication() {
        webView.getEngine().getLoadWorker().stateProperty().addListener((ov, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED) {

                EventListener listener = new EventListener() {
                    @Override
                    public void handleEvent(Event ev) {
                        String domEventType = ev.getType();
                        if (domEventType.equals(EVENT_TYPE_CLICK)) {
                            String href = ((Element) ev.getTarget()).getAttribute("href");
                            Desktop desk = Desktop.getDesktop();
                            try {
                                desk.browse(URI.create(href));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            ev.preventDefault();
                            ev.stopPropagation();
                        }
                    }
                };

                Document doc = webView.getEngine().getDocument();
                NodeList nodeList = doc.getElementsByTagName("a");
                for (int i = 0; i < nodeList.getLength(); i++) {
                    ((EventTarget) nodeList.item(i)).addEventListener(EVENT_TYPE_CLICK, listener, false);
                }
            }
        });
    }

    @Override
    public void start(Stage primaryStage) {

        webView.getEngine().setJavaScriptEnabled(true);
        this.primaryStage = primaryStage;

        final Scene loginScene = getLoginScene();
        primaryStage.setTitle("Chat Client v0.0.1");
        primaryStage.setScene(loginScene);
        primaryStage.show();

    }

    private Scene getLoginScene() {
        final VBox vBox = new VBox();

        final TextField username = new TextField();
        final Label usernameLabel = new Label("Username:");
        final HBox userBox = new HBox(usernameLabel, username);
        final PasswordField password = new PasswordField();

        password.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                ChatAppUser chatAppUser = new ChatAppUser(username.getText(), password.getText());
                chatFramework = applicationConfiguration.getChatFramework(chatAppUser, this);
                primaryStage.setScene(getChatScene());
                primaryStage.setTitle("Chat v0.0.1 (" + username.getText() + ")");
                Runnable runnable = () -> chatFramework.run();
                es.submit(runnable);
            }
        });

        final Label passwordLabel = new Label("Password:");
        final HBox passwordBox = new HBox(passwordLabel, password);
        vBox.getChildren().add(userBox);
        vBox.getChildren().add(passwordBox);
        final Button loginButton = new Button("Login");
        loginButton.setOnMouseClicked(event ->
                {
                    ChatAppUser chatAppUser = new ChatAppUser(username.getText(), password.getText());
                    chatFramework = applicationConfiguration.getChatFramework(chatAppUser, this);
                    primaryStage.setScene(getChatScene());
                    Runnable runnable = () -> chatFramework.run();
                    es.submit(runnable);
                }
        );
        vBox.getChildren().add(loginButton);

        return new Scene(vBox, 300, 300);
    }

    @Override
    public void init(String s) {
        Runnable runnable = () -> {
            webView.getEngine().loadContent(s);
            webView.getEngine().getLoadWorker().stateProperty()
                    .addListener((obs, oldValue, newValue) -> {
                        if (newValue == Worker.State.SUCCEEDED) {
                            initialized = true;
                        }
                    });
        };
        runInFxThread(runnable);
    }

    private Scene getChatScene() {

        final TextField inputField = new TextField("input text here...");
        inputField.setOnMouseClicked(event -> {
            if (inputField.getText().equals("input text here..."))
                inputField.setText("");
        });

        inputField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                chatFramework.sendMessage(inputField.getText());
                inputField.clear();
            }
        });
        final VBox vBox = new VBox();
        vBox.getChildren().add(inputField);

        userListView.setItems(observableUserList);
        userListView.setEditable(false);
        userListView.setFocusTraversable(true);
        userListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                final String user = userListView.getSelectionModel().getSelectedItem();

                privChatWindows.stream()
                        .filter(privChatWindow -> privChatWindow.user().equals(user))
                        .findFirst()
                        .ifPresentOrElse(
                                PrivChatWindow::setVisible, () -> {
                                    PrivChatWindow privChatWindow = new PrivChatWindow(primaryStage, chatFramework, user);
                                    privChatWindows.add(privChatWindow);
                                    privChatWindow.setVisible();
                                });
            }
        });

        final BorderPane borderPane = new BorderPane(webView, null, userListView, vBox, null);
        return new Scene(borderPane, 960, 600);
    }


    static void runInFxThread(Runnable runnable) {
        try {
            if (Platform.isFxApplicationThread()) {
                runnable.run();
            } else {
                FutureTask<Object> futureTask = new FutureTask<>(runnable,
                        null);
                Platform.runLater(futureTask);
                futureTask.get();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void append(Message s) {
        if (!initialized) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        runInFxThread(() -> {

                    try {
                        if (s != null)
                            webView.getEngine().executeScript("appendText(\"" + s.getCleanedForEngine() + "\");");
                    } catch (Exception ex) {
                        System.out.println("EXCEPTION APPEARED AT: " + s.getCleanedForEngine());
                        ex.printStackTrace();
                    }
                    webView.getEngine().executeScript("window.scrollTo(0, document.body.scrollHeight);");
                }
        );
    }


    @Override
    public void userListChanged(List<String> userList) {

        Runnable runnable = () -> {
            observableUserList.clear();
            observableUserList.addAll(userList);
        };
        runInFxThread(runnable);
    }

    @Override
    public void appendPrivateMessage(Message message) {

        Runnable createPrivChatWindow = () -> {
            PrivChatWindow privChatWindow = new PrivChatWindow(primaryStage, chatFramework, message.getReceiverForPm());
            privChatWindow.addText(message.getCleanedForEngine());
            privChatWindows.add(privChatWindow);
            privChatWindow.setVisible();
        };

        Runnable writeToPrivChatWindow = () -> {
            privChatWindows.stream()
                    .filter(privChatWindow -> privChatWindow.user().equals(message.getReceiverForPm()))
                    .findFirst()
                    .ifPresentOrElse(privChatWindow -> {
                        privChatWindow.addText(message.getCleanedForEngine());
                        privChatWindow.setVisible();
                    }, createPrivChatWindow);
        };
        runInFxThread(writeToPrivChatWindow);
    }


    @Override
    public void stop() throws Exception {
        super.stop();
        Platform.exit();
        if (chatFramework != null)
            chatFramework.exit();
        es.shutdown();
        System.exit(0);
    }
}
