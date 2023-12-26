package chatclient.entities;

public class Message {
    private final String rawString;
    public final String ourself;
    private final MESSAGE_TYPE type;
    private String message;
    public static String generalEncryptionPrefix = "chat#enc#";
    public static String encMessagePrefix = generalEncryptionPrefix + "msg#";
    public static String encSendKeyPrefix = generalEncryptionPrefix + "ack#";
    public static String encRequestKeyPrefix = generalEncryptionPrefix + "ack?";

    public Message(String rawString, String ourself) {
        this.ourself = ourself;
        this.rawString = rawString.replaceAll("<img src=\"", "<img src=\"https://server4.webkicks.de");
        message = this.rawString;
        if (isTimeOutMessage())
            type = MESSAGE_TYPE.TIMEOUT;
        else if (isLoginLogoutMessage())
            type = MESSAGE_TYPE.LOGINLOGOUT;
        else if (isEncryptStart())
            type = MESSAGE_TYPE.ENCRYPT_REQUEST;
        else if (isKeyFromOther())
            type = MESSAGE_TYPE.KEY_FROM_OTHER;
        else if (isEncrypted() && isWhisper())
            type = MESSAGE_TYPE.ENCRYPTED_WHISPER;
        else if (isWhisper())
            type = MESSAGE_TYPE.WHISPER;
        else
            type = MESSAGE_TYPE.SERVER;
    }

    private boolean isKeyFromOther() {
        return rawString.contains(encSendKeyPrefix);
    }

    private boolean isEncryptStart() {
        return rawString.contains(encRequestKeyPrefix);
    }


    public String getMsgFromPm() {
        if (rawString.contains("Du fl&uuml;sterst an "))
            return extractMsgFromSendPm();
        else
            return extractMsgFromReceivedPm();
    }

    private String extractMsgFromReceivedPm() {
        return rawString.replaceAll("<[^>]*>", "").split(" fl&uuml;stert:")[1].trim();
    }

    private String extractMsgFromSendPm() {
        return rawString.replaceAll("<[^>]*>", "").split("Du fl&uuml;sterst an ")[1].split(":")[1].trim();
    }

    public String getReceiverForPm() {
        if (rawString.contains("Du fl&uuml;sterst an "))
            return rawString.replaceAll("<[^>]*>", "").split("Du fl&uuml;sterst an ")[1].split("</span>:")[0].split(":")[0].trim();
        else
            return ourself;
    }

    public String getSenderForPm() {
        if (rawString.contains("Du fl&uuml;sterst an "))
            return rawString.split("onclick=\"fluester\\('")[1].replaceFirst("'.*", "");
        else
            return rawString.replaceAll("<[^>]*>", "").split(" fl&uuml;stert:")[0].split(" ")[1].trim();
    }

    private boolean isTimeOutMessage() {
        return rawString.contains("Ohne weitere Eingabe erfolgt in 2 Minuten dein Timeout");
    }

    private boolean isLoginLogoutMessage() {
        return rawString.contains("<span class=\"commandcolor\">");
    }

    private boolean isWhisper() {
        return rawString.contains("Du fl&uuml;sterst an ") || rawString.contains(" fl&uuml;stert</span>:");
    }

    public MESSAGE_TYPE getType() {
        return type;
    }

    public String getRawString() {
        return rawString;
    }

    public String getCleanedForEngine() {

        return message.trim()
                .replace("\n", "\\n")
                .replace("\r", "\\n")
                .replace("\"", "\\\"");
    }

    public boolean isEncrypted() {
        return rawString.contains(generalEncryptionPrefix);
    }

    public boolean isSendingMessage() {
        return rawString.contains("Du fl&uuml;sterst an ");
    }

    public void replaceEncryptedTextWith(String decode) {
        String beginning = rawString.split(" fl&uuml;stert:")[0].trim();
        message = beginning + " " + decode;
    }

    public void replacePlainTextWith(String encoded) {
        String beginning = rawString.split(" fl&uuml;stert:")[0].trim();
        message = beginning + " " + encoded;
    }

    public enum MESSAGE_TYPE {WHISPER, TIMEOUT, LOGINLOGOUT, ENCRYPT_REQUEST, KEY_FROM_OTHER, ENCRYPTED_WHISPER, SERVER}

}
