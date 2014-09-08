package sample;
import abiturklassen.netzklassen.Client;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: leobernard
 * Date: 08.09.14
 * Time: 00:09
 */
public class SyncClient extends Client {
    boolean awaitingSingle = false;
    String lastSyncAnswer = "";

    boolean awaitingMulti = false;
    ArrayList<String> lastMultiSyncAnswer = new ArrayList<String>();
    boolean isOpen = false;

    /**
     * Der Client ist mit Ein- und Ausgabestreams initialisiert.<br>
     *
     * @param pIPAdresse IP-Adresse bzw. Domain des Servers
     * @param pPortNr    Portnummer des Sockets
     */
    public SyncClient(String pIPAdresse, int pPortNr) {
        super(pIPAdresse, pPortNr);
    }

    public String sendSync(String message) {
        lastSyncAnswer = "";
        awaitingSingle = true;

        this.send(message);

        while(lastSyncAnswer.equals("")){
            // Wait for an answer...
        }

        awaitingSingle = false;

        return lastSyncAnswer;
    }

    public ArrayList<String> sendMultiSync(String message) {
        lastMultiSyncAnswer.clear();
        awaitingMulti = true;

        this.send(message);

        while(!lastSyncAnswer.trim().equals(".")) {
            // Waiting for the end to come...
        }

        awaitingMulti = false;

        lastMultiSyncAnswer.remove(".");

        return lastMultiSyncAnswer;
    }

    public ArrayList<String> getLastMultiSyncAnswer() {
        return lastMultiSyncAnswer;
    }

    public String getLastSyncAnswer() {
        return lastSyncAnswer;
    }

    public void closeConnection() {
        this.close();
    }

    public void send(String pMessage) {
        super.send(pMessage);
        System.out.println("[CLIENT] " + pMessage);
    }

    @Override
    public void processMessage(String pMessage) {
        System.out.println("[SERVER] " + pMessage);

        lastSyncAnswer = pMessage;
        lastMultiSyncAnswer.add(pMessage);

        if(!awaitingSingle && !awaitingMulti) {
            if(pMessage.startsWith("-ERR") && pMessage.toLowerCase().contains("disconnect")){
                isOpen = false;
                closeConnection();
            }
        }

    }
}
