package sample;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import javax.mail.Header;
import javax.mail.Multipart;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;

public class Controller {
    public WebView mailView;
    public ListView<String> mailList;
    public static final ObservableList<String> mails = FXCollections.observableArrayList();
    public static final ObservableList<String> headers = FXCollections.observableArrayList();
    public ProgressIndicator progressIndicator;
    public Label labelFrom;
    public Label labelTo;
    public Label labelSubject;
    public ListView<String> headerList;

    public void composeMail(ActionEvent actionEvent) throws IOException {
        Stage stage = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("new-mail.fxml"));
        stage.setTitle("New E-Mail");
        stage.setScene(new Scene(root, 500, 400));
        stage.show();
    }

    public void fetchMail(ActionEvent actionEvent) throws Exception {
        progressIndicator.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
        mailList.setItems(mails);
        headerList.setItems(headers);

        final POPClient client = new POPClient(Settings.popServer, Settings.popPort, Settings.popUser, Settings.popPass);
        ArrayList<Message> messages = new ArrayList<>();

        try {
            messages = client.getMessageList();
        } catch(Exception ex) {
            new AlertDialog("Mails could not be fetched:\n" + ex.getMessage());
        }

        mails.clear();

        for(Message msg : messages){
            mails.add(msg.toString());
        }
        progressIndicator.setProgress(1);


        mailList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                int mailID = Integer.parseInt(newValue.split(" ")[0]);

                try{
                    progressIndicator.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);

                    MimeMessage msg = client.getSingleMessage(mailID);

                    if(msg.isMimeType("text/plain")) {
                        mailView.getEngine().loadContent(msg.getContent().toString(), "text/plain");
                    }else if(msg.isMimeType("text/html")){
                        mailView.getEngine().loadContent(msg.getContent().toString(), "text/html");
                    }else{
                        mailView.getEngine().loadContent(msg.getDescription(), "text/plain");
                    }

                    labelFrom.setText(msg.getFrom()[0].toString());
                    labelTo.setText(msg.getRecipients(javax.mail.Message.RecipientType.TO)[0].toString());
                    labelSubject.setText(msg.getSubject());

                    headers.clear();
                    msg.getAllHeaderLines();
                    for (Enumeration<Header> e = msg.getAllHeaders(); e.hasMoreElements();) {
                        Header h = e.nextElement();

                        if(!h.getName().toLowerCase().equals("content-description")){
                            headers.add(h.getName().toUpperCase() + ": " + h.getValue());
                        }
                    }

                    progressIndicator.setProgress(1);
                }catch(Exception ex){
                    new AlertDialog("E-Mail could not be shown:\n" + ex.getMessage());
                }
            }
        });
    }

    public void openSettings(ActionEvent actionEvent) throws Exception {
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("settings.fxml"));
        stage.setTitle("Settings");
        stage.setScene(new Scene((VBox) loader.load(), 500, 400));
        stage.setMinHeight(400);
        stage.setMinWidth(400);

        loader.<SettingsController>getController().init();

        stage.show();
    }
}