package chatclient.gui;

import javafx.application.Platform;
import javafx.stage.Stage;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

@Disabled("find the correct docker image")
@ExtendWith(ApplicationExtension.class)
class PrivChatWindowTest {

    PrivChatWindow sut;

    @Start
    private void start(Stage stage) {
        sut = new PrivChatWindow(null, null, "oli");
    }

    @Test
    void name() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                sut.setVisible();
            }
        });
    }


}