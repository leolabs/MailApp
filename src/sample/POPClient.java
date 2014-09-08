package sample;

import sample.messages.Message;

import javax.security.auth.login.LoginException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: leobernard
 * Date: 07.09.14
 * Time: 23:43
 */
public class POPClient {
    private String pIPAdresse;
    private int pPortNr;
    String user;
    String pass = "";
    SyncClient client;

    /**
     * Der Client ist mit Ein- und Ausgabestreams initialisiert.<br>
     *
     * @param pIPAdresse IP-Adresse bzw. Domain des Servers
     * @param pPortNr    Portnummer des Sockets
     */
    public POPClient(String pIPAdresse, int pPortNr, String user, String pass) {
        this.pIPAdresse = pIPAdresse;
        this.pPortNr = pPortNr;
        this.user = user;
        this.pass = pass;

        client = new SyncClient(pIPAdresse, pPortNr);
    }

    private void login() throws Exception {
        if(!client.isOpen){
            client = new SyncClient(pIPAdresse, pPortNr);

            if(!client.sendSync("USER " + user).startsWith("+OK")){
                throw new LoginException(client.getLastSyncAnswer());
            }

            if(!client.sendSync("PASS " + pass).startsWith("+OK")){
                throw new LoginException(client.getLastSyncAnswer());
            }
        }
    }

    public ArrayList<Message> getMessageList() throws Exception {
        login();

        ArrayList<String> messages = client.sendMultiSync("LIST");
        ArrayList<Message> mails = new ArrayList<Message>();

        for(String message : messages) {
            if(message.startsWith("+") || message.startsWith("-")) continue;

            String[] parts = message.split(" ");
            int id = Integer.parseInt(parts[0]);
            int size = Integer.parseInt(parts[1]);

            mails.add(new Message(id, size));
        }

        client.closeConnection();

        return mails;
    }



    public Message getSingleMessage(int id) throws Exception {
        login();

        ArrayList<String> answers = client.sendMultiSync("RETR " + id);
        HashMap<String, String> headers = new HashMap<String, String>();
        String message = "";

        boolean isHeaders = true;

        for(String answer : answers) {
            if(answer.startsWith("+")) continue;
            if(answer.trim().equals("")) isHeaders = false;

            if(isHeaders) {
                if(answer.split(":").length >= 2){
                    String[] parts = answer.split(":", 2);
                    headers.put(parts[0].trim().toLowerCase(), parts[1].trim());
                }
            }else{
                message += answer + "\n";
            }
        }

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

        for(int i = 0x21; i <= 0xFF; i++) {
            String hex = Integer.toHexString(i);
            byte[] chars = {(byte) i};
            message = message.replace("=" + hex.toUpperCase(), new String(chars, encoding));
        }

        boolean isHTML = encoding.contains("text/html");
        return new Message(headers, isHTML, message);
    }
}