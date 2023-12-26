package chatclient;

import chatclient.entities.ChatAppUser;
import chatclient.services.LoginService;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.net.URL;

import static org.assertj.core.api.Assertions.assertThat;

public class UserStepDefinitions {

    @Given("a logged in user in room A")
    public void a_logged_in_user_in_room_A() {
        // Write code here that turns the phrase above into concrete actions

    }

    @When("the user says {string} to the room")
    public void the_user_says_to_the_room(String string) {
        // Write code here that turns the phrase above into concrete actions

    }

    @Then("the room should list the user saying {string}")
    public void the_room_should_list_the_user_saying(String string) {
        // Write code here that turns the phrase above into concrete actions

    }

    @Given("a username and a password")
    public void a_username_and_a_password() {
        // Write code here that turns the phrase above into concrete actions
        ChatAppUser chatAppUser = new ChatAppUser("username", "password");
    }

    @When("the user logs in he should be logged in")
    public void the_user_logs_in() {
        // Write code here that turns the phrase above into concrete actions
        URL offlineHtmlPageUrl = getClass().getResource("/synapse_login_page.html");
        ChatAppUser chatAppUser = new ChatAppUser("username", "password");
        LoginService loginService = new LoginService();
        loginService.login(offlineHtmlPageUrl, chatAppUser);
        assertThat(chatAppUser.isLoggedIn()).isTrue();
    }
}