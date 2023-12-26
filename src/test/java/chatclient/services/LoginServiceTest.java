package chatclient.services;

import chatclient.entities.ChatAppUser;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.net.URL;

import static org.assertj.core.api.Assertions.assertThat;

@Disabled("find the correct docker image")
class LoginServiceTest {

    @Test
    void can_login_with_username_and_password() {
        //given
        URL offlineHtmlPageUrl = getClass().getResource("/synapse_login_page.html");
        LoginService sut = new LoginService();
        ChatAppUser chatAppUser = new ChatAppUser("Thomas", "somePass");
        //when
        sut.login(offlineHtmlPageUrl, chatAppUser);
        //then
        assertThat(chatAppUser.isLoggedIn()).isTrue();
    }
}