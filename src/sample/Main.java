package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import sample.messages.Message;

import java.util.ArrayList;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.show();
    }


    public static void main(String[] args) throws Exception {
        //launch(args);

        testSMTP();
    }

    private static void testPOP() throws Exception {
        POPClient client = new POPClient("SERVER", 110, "USER", "PASS");

        ArrayList<Message> messages = client.getMessageList();
        System.out.println(client.getSingleMessage(messages.get(4).getId()));
    }

    private static void testSMTP() throws Exception {
        SMTPClient client = new SMTPClient("smtp.goneo.de", 587, "leolabs.org");

        client.sendMail(new Message(
                "leolabs<admin@leolabs.org>", // Sender
                "Leo<leo.bernard@me.com>", // Empfänger
                "Test-Mail", // Subject
                "Dies ist ein <b>Test</b>. Dieser <i>Test</i> kann\nvielleicht empfangen werden, mal sehen.\n\nViele Grüße\nLeo Bernard", // Message
                true // Is HTML?
        ));
    }
}
