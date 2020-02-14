package sample;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.web.HTMLEditor;
import javafx.stage.Stage;

/**
 * Created with IntelliJ IDEA.
 * User: leobernard
 * Date: 11.09.14
 * Time: 08:58
 */
public class ComposeController {
    public Button btnSend;
    public TextField fieldReceipient;
    public TextField fieldSubject;
    public HTMLEditor fieldEditor;
    public VBox form;


    public void sendMail(ActionEvent actionEvent) {
        SMTPClient client = new SMTPClient(Settings.smtpServer, Settings.smtpPort, Settings.smtpServer, Settings.smtpUser, Settings.smtpPass);

        Message msg = new Message(
                Settings.smtpUser, // Sender
                fieldReceipient.getText(), // Empfänger
                fieldSubject.getText(), // Subject
                fieldEditor.getHtmlText().replace(" contenteditable=\"true\"", ""), // Message
                true // Is HTML?
        );


        try {
            client.sendMail(msg);

            Node source = (Node) actionEvent.getSource();
            Stage stage = (Stage) source.getScene().getWindow();
            stage.close();
        } catch (Exception e) {
            new AlertDialog("The E-Mail could not be sent:\n" + e.getMessage());
        }
    }
}
