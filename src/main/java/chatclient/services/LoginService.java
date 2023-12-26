package chatclient.services;

import chatclient.entities.ChatAppUser;
import org.htmlunit.BrowserVersion;
import org.htmlunit.WebClient;
import org.htmlunit.html.HtmlPage;
import org.htmlunit.html.HtmlPasswordInput;
import org.htmlunit.html.HtmlSubmitInput;
import org.htmlunit.html.HtmlTextInput;

import java.io.IOException;
import java.net.URI;
import java.net.URL;

public class LoginService {

    public void login(URL url, ChatAppUser chatAppUser) {
        WebClient webClient = new WebClient(BrowserVersion.BEST_SUPPORTED);


        webClient.setRefreshHandler((page, url1, seconds) -> {
            if (url1.toString().contains("/chatstream/")) {
                chatAppUser.setChatstream(url1.openStream());
                chatAppUser.setChatUrl(url1);
                int startOfPasscode = page.getUrl().toString().lastIndexOf(chatAppUser.getUsername()) + chatAppUser.getUsername().length() + 1;
                chatAppUser.setInternalPass(page.getUrl().toString().substring(startOfPasscode, page.getUrl().toString().length() - 6));
                String chatPostStreamUrl = "https://server4.webkicks.de/cgi-bin/chat.cgi";
                chatAppUser.setChatPostUrl(URI.create(chatPostStreamUrl).toURL());
            } else
                System.out.println("unknown redirect url: " + url1);
        });
        webClient.setFrameContentHandler(baseFrameElement -> {
            if (baseFrameElement.getSrcAttribute().contains("/chatstream/") ||
                    baseFrameElement.getSrcAttribute().contains("/index/")) {
                System.out.println("Loading frame: " + baseFrameElement.getSrcAttribute());
                return true;
            }
            return true;
        });

        HtmlPage page;
        try {
            page = webClient.getPage(url);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        HtmlTextInput userField = page.getElementByName("user");
        HtmlPasswordInput passwordField = page.getElementByName("pass");
        HtmlSubmitInput loginButton = page.getElementByName("login");

        userField.setText(chatAppUser.getUsername());
        passwordField.setText(chatAppUser.getPassword());


        try {
            loginButton.click();
            chatAppUser.setLoggedIn();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        webClient.close();
    }

}
