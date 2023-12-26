package chatclient;

import chatclient.entities.ChatAppUser;
import chatclient.entities.Message;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.Charset;
import java.util.Scanner;

public class StreamReader {

    private final ChatAppUser chatAppUser;
    private final Updateable writeTo;

    public StreamReader(ChatAppUser chatAppUser, Updateable writeTo) {
        this.chatAppUser = chatAppUser;
        this.writeTo = writeTo;
    }

    public void run() throws IOException {

        final URI chatStreamUri = getUserStreamUrl();

        HttpClient httpClient = HttpClient.newBuilder().build();
        HttpRequest httpRequest = HttpRequest.newBuilder(chatStreamUri)
                .version(HttpClient.Version.HTTP_1_1)
                .setHeader("User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:109.0) Gecko/20100101 Firefox/115.0")
                .GET()
                .build();
        try {
            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString(Charset.forName("ISO8859-1")));
            String realStreamUrl = getRealStreamUrl(response.body(), chatAppUser.getInternalPass());
            HttpRequest httpRequest2 = HttpRequest.newBuilder(URI.create(realStreamUrl))
                    .version(HttpClient.Version.HTTP_1_1)
                    .setHeader("User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:109.0) Gecko/20100101 Firefox/115.0")
                    .GET()
                    .build();
            HttpResponse<String> crapUrl = httpClient.send(httpRequest2, HttpResponse.BodyHandlers.ofString(Charset.forName("ISO8859-1")));
            String realrealStreamUrl = getCrapStreamUrl(crapUrl.body());
            HttpRequest httpRequest3 = HttpRequest.newBuilder(URI.create(realrealStreamUrl))
                    .version(HttpClient.Version.HTTP_1_1)
                    .setHeader("User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:109.0) Gecko/20100101 Firefox/115.0")
                    .GET()
                    .build();

            httpClient.sendAsync(httpRequest3, HttpResponse.BodyHandlers.ofInputStream())
                    .thenApply(HttpResponse::body)
                    .thenAccept(inputStream -> {
                        try {
                            byte[] buffer = new byte[8192];
                            for (int length; (length = inputStream.read(buffer)) != -1; ) {
                                //find the first 0 byte in the buffer and only send that as string
                                int i;
                                for (i = 0; i < buffer.length && buffer[i] != 0; i++) {
                                }
                                String s = new String(buffer, 0, i, Charset.forName("ISO8859-1"));
                                writeTo.accept(new Message(s, chatAppUser.getUsername()));
                                buffer = new byte[8192];
                            }
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }

                    })
                    .join();


        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


    }


    private URI getUserStreamUrl() {
        final URI chatStreamUri;
        try {
            chatStreamUri = chatAppUser.getChatStreamUrl().toURI();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        return chatStreamUri;
    }

    private String getRealStreamUrl(String s, String usersInternalPass) {
        Scanner scanner = new Scanner(s);
        while (scanner.hasNextLine()) {
            String newLine = scanner.nextLine();
            System.out.println("blah: " + newLine);
            if (newLine.contains("window.location.replace"))
                return newLine
                        .replace("window.location.replace('", "")
                        .replace("'+pass+'", usersInternalPass)
                        .replace("');", "")
                        .trim();
        }
        throw new RuntimeException("got " + s + " but cant work with that");
    }

    private String getCrapStreamUrl(String s) {
        Scanner scanner = new Scanner(s);
        while (scanner.hasNextLine()) {
            String s1 = scanner.nextLine();
            String timedOutError = "<p><b>Fehler: Timeout. Bitte neu einloggen.</b></p>";
            if (s1.equals(timedOutError)) {
                //TODO: show a popup or so that we must re-login
            }
            System.out.println("stream: " + s1);
            if (s1.contains("URL=")) {
                String trim = s1
                        .replace("<meta http-equiv=\"refresh\" content=\"0; URL=", "")
                        .replace("\">", "")
                        .trim();
                System.out.println("stream result: " + trim);
                return trim;
            }
        }

        throw new RuntimeException("not found in: " + s);
    }

}