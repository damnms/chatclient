package chatclient;

import chatclient.entities.Message;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MessageTest {

    @Test
    void does_replace_img_src_with_webkicks_url() {
        String raw = """
                <table border=0><tr><td valign=bottom><FONT SIZE=-2>(17:54)</FONT> <b><font title="slackito"><span onclick="fluester('slackito')"><b><FONT COLOR="#000000">s</FONT><FONT COLOR="#000000">l</FONT><FONT COLOR="#000000">a</FONT><FONT COLOR="#000000">c</FONT><FONT COLOR="#000000">k</FONT><FONT COLOR="#000000">i</FONT><FONT COLOR="#000000">t</FONT><FONT COLOR="#000000">o</FONT></b></span></b>: <span onclick="javascript: repClick('lache')" style="cursor: pointer;"><img src="/synapse/replacer/lache.gif" alt="&#58;lache"></span></font></td></tr></table>
                """;
        String expected = """
                <table border=0><tr><td valign=bottom><FONT SIZE=-2>(17:54)</FONT> <b><font title=\\"slackito\\"><span onclick=\\"fluester('slackito')\\"><b><FONT COLOR=\\"#000000\\">s</FONT><FONT COLOR=\\"#000000\\">l</FONT><FONT COLOR=\\"#000000\\">a</FONT><FONT COLOR=\\"#000000\\">c</FONT><FONT COLOR=\\"#000000\\">k</FONT><FONT COLOR=\\"#000000\\">i</FONT><FONT COLOR=\\"#000000\\">t</FONT><FONT COLOR=\\"#000000\\">o</FONT></b></span></b>: <span onclick=\\"javascript: repClick('lache')\\" style=\\"cursor: pointer;\\"><img src=\\"https://server4.webkicks.de/synapse/replacer/lache.gif\\" alt=\\"&#58;lache\\"></span></font></td></tr></table>
                """;
        Message sut = new Message(raw, "xyz");
        assertThat(sut.getCleanedForEngine().trim()).isEqualToIgnoringNewLines(expected);
    }

    @Test
    void can_extract_sender_from_send_pm() {
        Message sut = new Message("<table border=0><tr><td valign=bottom><FONT SIZE=-2>(20:53)</FONT> <b><span onclick=\"fluester('abc')\">Du fl&uuml;sterst an xyz</span>: <font color=red>test</font></td></tr></table>", "ourself");
        String sender = "";
        if (sut.isSendingMessage())
            sender = sut.getSenderForPm();
        assertThat(sender).isEqualTo("abc");
    }

    @Test
    void can_extract_receiver_from_send_pm() {
        Message sut = new Message("<table border=0><tr><td valign=bottom><FONT SIZE=-2>(20:53)</FONT> <b><span onclick=\"fluester('abc')\">Du fl&uuml;sterst an xyz</span>: <font color=red>test</font></td></tr></table>", "ourself");
        String receiver = sut.getReceiverForPm();
        assertThat(receiver).isEqualTo("xyz");
    }

    @Test
    void can_extract_message_from_send_msg() {
        Message sut = new Message("<table border=0><tr><td valign=bottom><FONT SIZE=-2>(20:53)</FONT> <b><span onclick=\"fluester('abc')\">Du fl&uuml;sterst an xyz</span>: <font color=red>test</font></td></tr></table>", "ourself");
        String msg = sut.getMsgFromPm();
        assertThat(msg).isEqualTo("test");
    }

    @Test
    void can_extract_receiver_from_received_pm() {
        Message sut = new Message("<table border=0><tr><td valign=bottom><FONT SIZE=-2>(20:53)</FONT> <b><span onclick=\"fluester('abc')\">abc fl&uuml;stert</span>: <font color=red>test</font></td></tr></table>", "ourself");
        String receiver = sut.getReceiverForPm();
        assertThat(receiver).isEqualTo("ourself");
    }

    @Test
    void can_extract_sender_from_received_pm() {
        Message sut = new Message("<table border=0><tr><td valign=bottom><FONT SIZE=-2>(20:53)</FONT> <b><span onclick=\"fluester('abc')\">abc fl&uuml;stert</span>: <font color=red>test</font></td></tr></table>", "ourself");
        String receiver = sut.getSenderForPm();
        assertThat(receiver).isEqualTo("abc");
    }

    @Test
    void can_extract_message_from_received_msg() {
        Message sut = new Message("<table border=0><tr><td valign=bottom><FONT SIZE=-2>(20:53)</FONT> <b><span onclick=\"fluester('abc')\">abc fl&uuml;stert</span>: <font color=red>test</font></td></tr></table>", "ourself");
        String msg = sut.getMsgFromPm();
        assertThat(msg).isEqualTo("test");
    }

    @Test
    void can_extract_message_from_keyexchange() {
        Message sut = new Message("<table border=0><tr><td valign=bottom><FONT SIZE=-2>(20:53)</FONT> <b><span onclick=\"fluester('abc')\">abc fl&uuml;stert</span>: <font color=red>chat#enc#key#lolyeah</font></td></tr></table>", "ourself");
        String msg = sut.getMsgFromPm();
        assertThat(msg).isEqualTo("chat#enc#key#lolyeah");
    }

    @Test
    void can_extract_msg_from_keyexchange() {
        String crap = "<table border=0><tr><td valign=bottom><FONT SIZE=-2>(10:43)</FONT> <b><span onclick=\"fluester('slackito')\">Du fl&uuml;sterst an slackito</span>: <font color=red>chat#enc#key#lolyeah</font></td></tr></table>\n";
        Message s = new Message(crap, "ourself");
        String msgFromPm = s.getMsgFromPm();
        assertThat(msgFromPm).isEqualTo("chat#enc#key#lolyeah");
    }

}