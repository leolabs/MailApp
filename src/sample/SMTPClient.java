package sample;

import com.sun.org.apache.xml.internal.security.utils.Base64;
import sample.messages.Message;

import javax.security.auth.login.LoginException;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: leobernard
 * Date: 08.09.14
 * Time: 08:01
 */
public class SMTPClient {
    private final boolean needsLogin;
    private String pIPAdresse;
    private int pPortNr;
    private String clientAddress;
    String user;
    String pass = "";
    SyncClient client;

    /**
     * Der Client ist mit Ein- und Ausgabestreams initialisiert.<br>
     *
     * @param pIPAdresse IP-Adresse bzw. Domain des Servers
     * @param pPortNr    Portnummer des Sockets
     */
    public SMTPClient(String pIPAdresse, int pPortNr, String client, String user, String pass) {
        this.pIPAdresse = pIPAdresse;
        this.pPortNr = pPortNr;
        this.clientAddress = client;
        this.user = user;
        this.pass = pass;
        this.needsLogin = true;

        this.client = new SyncClient(pIPAdresse, pPortNr);
    }

    public SMTPClient(String pIPAdresse, int pPortNr, String clientAddress) {
        this.pIPAdresse = pIPAdresse;
        this.pPortNr = pPortNr;
        this.clientAddress = clientAddress;
        this.needsLogin = false;
    }

    private void login() throws Exception {
        client = new SyncClient(pIPAdresse, pPortNr);

        client.sendSync("EHLO " + clientAddress);

        if(this.needsLogin){
            client.sendSync("AUTH LOGIN");

            if (!client.sendSync(Base64.encode(user.getBytes())).startsWith("3")) {
                throw new LoginException(client.getLastSyncAnswer());
            }

            if(!client.sendSync(Base64.encode(pass.getBytes())).startsWith("3")){
                throw new LoginException(client.getLastSyncAnswer());
            }
        }
    }

    private void trySend(String message) throws Exception {
        String answer = client.sendSync(message);

        if(answer.startsWith("4") || answer.startsWith("5")) {
            throw new Exception(answer);
        }
    }

    public void sendMail(Message message) throws Exception {
        login();

        trySend("MAIL FROM: " + message.getFrom());
        trySend("RCPT TO: " + message.getTo());

        trySend("DATA");

        client.send("From: " + message.getFrom());
        client.send("To: " + message.getTo());
        client.send("Subject: " + message.getSubject());
        client.send("Date: " + new Date().toString());
        client.send("Content-Type: " + (message.isHTML() ? "text/html" : "text/plain") + "; charset=UTF-8");
        client.send("");
        client.send(message.getMessage());
        trySend(".");
        trySend("QUIT");
    }
}
