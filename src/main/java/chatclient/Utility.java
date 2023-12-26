package chatclient;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Utility {

    private static List<String> getUserList(String x) {
        List<String> result = new ArrayList<>();
        String[] userArray = x.split("\n");
        Arrays.stream(userArray)
                .map(s -> s.replace("(", ")").replace(")", ""))
                .map(String::trim)
                .forEach(result::add);
        return result;
    }

    public static List<String> requestUsers() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("http://server4.webkicks.de/cgi-bin/raw.cgi?cid=synapse"))
                    .GET()
                    .version(HttpClient.Version.HTTP_1_1)
                    .setHeader("User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:109.0) Gecko/20100101 Firefox/115.0")
                    .build();
            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return getUserList(response.body());
        } catch (URISyntaxException | IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }


}
