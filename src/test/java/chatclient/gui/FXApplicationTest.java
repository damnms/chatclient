package chatclient.gui;

import chatclient.entities.Message;
import javafx.stage.Stage;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;


@Disabled("we need to get that docker crap working with headless")
@ExtendWith(ApplicationExtension.class)
class FXApplicationTest {


    FXApplication sut;

    private final String killedString = "<b><table border=0><tr><td valign=bottom><FONT SIZE=-2>(17:19)</FONT> <b><font title=\"myself\"><span onclick=\"fluester('myself')\"><b><FONT COLOR=\"#000000\">s</FONT><FONT COLOR=\"#000000\">l</FONT><FONT COLOR=\"#000000\">a</FONT><FONT COLOR=\"#000000\">c</FONT><FONT COLOR=\"#000000\">k</FONT><FONT COLOR=\"#000000\">i</FONT><FONT COLOR=\"#000000\">t</FONT><FONT COLOR=\"#000000\">o</FONT></b></span></b>: test <span onclick=\"javascript: repClick('lache')\" style=\"cursor: pointer;\"><img src=\"https://server4.webkicks.de/synapse/replacer/lache.gif\" alt=\"&#58;lache\"></span></font></td></tr></table><b>";

    private final String initString = """
            <!doctype html>
            <html lang="de">
            <head>
            <meta name="viewport" content="width=device-width, initial-scale=1">
            <meta http-equiv="cache-control" content="no-cache">
            <meta http-equiv="pragma" content="no-cache">
            <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
            <link rel="stylesheet" type="text/css" href="/someroom/style_stream.css?1692439165">
            <script src="https://ajax.googleapis.com/ajax/libs/jquery/2.1.3/jquery.min.js"></script>
            <script language="JavaScript">
            	doscroll = true;
            	parent.forces = false;
            	function Scroller() {
            	 if (parent.force) { doscroll = true }
            	  if (doscroll != false) {
            	    window.scrollBy(0,50);
            	  }
            	  window.setTimeout("Scroller()", 50);
            	}
            	Scroller();
            	parent.atok = 'UuXaM';
            	function atok(p1) {
            	  parent.atok = p1;
            	}
                        
            	window.status="powered by www.somedomain.de";
                        
            	window.onmouseover=function(e){doscroll=false;};\s
            	window.onmouseout=function(e){doscroll=true;}
            	
            	function wopen(link,wi,he) {
            	      wincom = window.open(link ,"fenster","toolbar=0,location=0,directories=0,status=0,menubar=0,scrollbars=1,resizable=0,width="+wi+",height="+he);
            	      wincom.focus() ;
            	}
            	
            	function repClick(rep) {
            	  parent.info.document.eingabe.message.value+=':' + rep;
            	  parent.info.document.eingabe.message.focus();
            	  return false;
            	}
                        
            	function CallPrem() {
            	  window.open("/someroom/premium/myself/","Prem","scrollbars=no,width=205,height=260,resizable=no,toolbar=no,status=no,directories=no,menubar=no,location=no");
            	}
                        
            	function fluester(fchatter) {
            	      parent.info.document.eingabe.message.value="/f "+fchatter+" ";
            	      parent.info.document.eingabe.message.focus();
            	      var pos = parent.info.document.eingabe.message.createTextRange();
            	      pos.collapse(false);
            	      pos.select();
            	      return false;
            	}
                        
            	function team() {
            	      parent.info.document.eingabe.message.value="/team ";
            	      parent.info.document.eingabe.message.focus();
            	      var pos = parent.info.document.eingabe.message.createTextRange();
            	      pos.collapse(false);
            	      pos.select();
            	      return false;
            	}
            	
            	parent.clearTimeout(parent.cdProbmodus);
            	parent.clearTimeout(parent.cdProbmodusRC);
            	
            	function LoginSound() {
            		if (parent.forces) {
            		 ausgabe = '<audio autoplay="autoplay"><source src="/8.wav" type="audio/wav"/></audio>';
                        document.getElementById('soundcontainer').innerHTML=ausgabe;
            	    }
            	}
                        
              setTimeout('parent.info.sende();', 1000);
              setTimeout('parent.info.sendes();', 1000);
            	
            </script>
            </head>
            <body onFocus="doscroll=false" onMouseOver="doscroll=false">
            powered by <a href="https://www.somedomain.de" target="_blank">https://www.somedomain.de</a>
            <hr>
            <script language="JavaScript">parent.rightFrame.document.location="/cgi-bin/ol.cgi?cid=someroom&raum=main";</script>
            <script type="text/javascript" src="/grundscripts.js"></script><script language="javascript">linkCnt=0; tds=document.getElementsByTagName("td"); function autoLink(){for(;linkCnt<tds.length;linkCnt++){ if(tds[linkCnt].innerHTML.match(/([^"'=]+(?:http|ftp|https):\\/\\/[^<>\\s']+)/i)){tds[linkCnt].innerHTML=tds[linkCnt].innerHTML.replace(/([^"'=]+)((?:http|ftp|https):\\/\\/[^<>\\s']+)/i, "$1<a href='$2' target='_blank'>$2</a>");}else{ tds[linkCnt].innerHTML=tds[linkCnt].innerHTML.replace(/([^"'=\\/\\w\\.]+)(\\w+\\.[a-zA-Z0-9_-]+\\.\\w\\w+[^<>\\s']*)/i, "$1<a href='http:\\/\\/$2' target='_blank'>$2</a>");}}}window.setInterval("autoLink()", 500)</script><br><style type=text/css>font[color=red]{color:#ae0aa8}</style><br><br>Willkommen <b><FONT COLOR="#000000">s</FONT><FONT COLOR="#000000">l</FONT><FONT COLOR="#000000">a</FONT><FONT COLOR="#000000">c</FONT><FONT COLOR="#000000">k</FONT><FONT COLOR="#000000">i</FONT><FONT COLOR="#000000">t</FONT><FONT COLOR="#000000">o</FONT></b>. <span onclick="javascript: repClick('huhu')" style="cursor: pointer;"><img src="https://server4.somedomain.de/someroom/replacer/huhu.gif" alt="&#58;huhu"></span> Viel Freude beim chatten. <br><ul><li>Bei Problemen im Chat bitte wenden an: user2old (vorher user1) <span onclick="javascript: repClick('user2old')" style="cursor: pointer;"><img src="https://server4.somedomain.de/someroom/replacer/user2old.gif" alt="&#58;user2old"></span> user1lein@web.de,<br> oder an user2 <span onclick="javascript: repClick('hase')" style="cursor: pointer;"><img src="https://server4.somedomain.de/someroom/replacer/hase.gif" alt="&#58;hase"></span> haseprof@gmail.com.<br>Bitte die Mail-Adressen für eventuelle Notfälle <u>speichern</u> (z.B. Ausfall der Chatsoftware).</li><br><li>Wenn irgendetwas im Chat dauerhaft stört, bitte ich euch, konstruktive Kritik nicht zurückzuhalten, damit nach Lösungen gesucht werden kann.</li><br></ul>
            <script>function appendText(extraStr) {document.getElementsByTagName('body')[0].innerHTML = document.getElementsByTagName('body')[0].innerHTML + extraStr;}</script>
            """;

    @Start
    private void start(Stage stage) {
        sut = new FXApplication();
        sut.init(initString);
    }

    @Test
    void can_init() {
        sut.append(new Message(killedString, "ourself"));
    }
}