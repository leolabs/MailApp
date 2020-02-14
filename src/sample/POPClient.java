package sample;

import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import javax.security.auth.login.LoginException;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: leobernard
 * Date: 07.09.14
 * Time: 23:43
 */
public class POPClient {
    // Serverdaten
    private String address;
    private int port;

    // Benutzerdaten
    String user;
    String pass = "";

    // Erweiterter TCP Client
    SyncClient client;

    /**
     * Initialisiert einen neuen POP Client mit den angegebenen Daten.
     *
     * @param address die IP-Adresse oder Domain des POP-Servers
     * @param port der Port des POP-Servers (am besten 110)
     * @param user der Benutzername
     * @param pass das Benutzer-Passwort
     */
    public POPClient(String address, int port, String user, String pass) {
        this.address = address;
        this.port = port;
        this.user = user;
        this.pass = pass;

        client = new SyncClient(address, port);
    }

    /**
     * Loggt sich mit den während der Initialisierung
     * angegebenen Benutzerdaten beim Server ein, sofern
     * der Benutzer noch nicht eingeloggt ist.
     *
     * @throws LoginException wenn ein Feher bei der Anmeldung aufgetreten ist.
     */
    private void login() throws LoginException {
        client = new SyncClient(address, port);

        if(!client.sendSync("USER " + user).startsWith("+OK")){
            throw new LoginException(client.getLastSyncAnswer());
        }

        if(!client.sendSync("PASS " + pass).startsWith("+OK")){
            throw new LoginException(client.getLastSyncAnswer());
        }

        if(!client.sendSync("NOOP").startsWith("+OK")) {
            throw new LoginException(client.getLastSyncAnswer());
        }
    }

    /**
     * Ruft die Liste aller Nachrichten vom Server ab.
     *
     * @return die Liste aller Nachrichten
     * @throws LoginException wenn die Anmeldung am Server fehlgeschlagen ist
     */
    public ArrayList<Message> getMessageList() throws LoginException {
        login();

        ArrayList<String> messages = client.sendMultiSync("LIST");
        ArrayList<Message> mails = new ArrayList<>();

        for(String message : messages) {
            if(message.startsWith("+") || message.startsWith("-")) continue;

            String[] parts = message.split(" ");
            int id = Integer.parseInt(parts[0]);
            int size = Integer.parseInt(parts[1]);

            mails.add(new Message(id, size));
        }

        client.sendSync("QUIT");
        client.closeConnection();

        return mails;
    }

    /**
     * Ruft eine einzelne Nachricht mit der gegebenen ID vom Server ab.
     *
     * @param id die ID der Nachricht
     * @return die Nachricht
     * @throws LoginException
     */
    public MimeMessage getSingleMessage(int id) throws Exception {
        login();

        ArrayList<String> answers = client.sendMultiSync("RETR " + id);
        HashMap<String, String> headers = new HashMap<>();
        String message = ""; String rawMessage = "";

        boolean isHeaders = true;

        /*
        String encoding = headers.get("content-type");
        int encodingpos = encoding.indexOf("charset=");

        if(encodingpos != -1) {
            encoding = encoding.substring(encodingpos + 8);
            int endpos = encoding.indexOf(";");

            if(endpos != -1) {
                encoding = encoding.substring(0, endpos);
            }
        }else{
            encoding = "UTF-8";
        }

        if(!isCharsetSupported(encoding)) encoding = "UTF-8";
        */

        boolean isAttachment = false;
        for(String line : answers) {
            if(line.startsWith("+")) continue;
            if(line.trim().equals("")) isHeaders = false;

            if(line.toLowerCase().startsWith("content-disposition: attachment")) isAttachment = true;

            if(!isAttachment){
                message += line + "\n";

                if(!isHeaders){
                    rawMessage += line + "\n";
                }
            }else{
                if(line.startsWith("--")) isAttachment = false;
            }
        }


        /*for(int i = 0x20; i <= 0xFF; i++) {
            String hex = Integer.toHexString(i);
            byte[] chars = {(byte) i};

            try{
                rawMessage = rawMessage.replace("=" + hex.toUpperCase(), new String(chars, "UTF-8"));
            }catch (Exception ex){
                // Skip this round...
            }
        }*/

        client.sendSync("QUIT");

        Session s = Session.getDefaultInstance(new Properties());
        InputStream is = new ByteArrayInputStream(message.getBytes());
        MimeMessage msg = new MimeMessage(s, is);
        msg.setDescription(rawMessage);

        return msg;
    }

    boolean isCharsetSupported(String name) {
        try {
            Charset.forName(name);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
}