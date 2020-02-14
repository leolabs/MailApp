package sample;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Created with IntelliJ IDEA.
 * User: leobernard
 * Date: 14.09.14
 * Time: 11:57
 */
public class SettingsController {
    public TextField smtpServer;
    public TextField smtpPort;
    public TextField smtpUser;
    public TextField smtpPass;
    public TextField popServer;
    public TextField popPort;
    public TextField popUser;
    public TextField popPass;

    public void saveSettings(ActionEvent actionEvent) {
        try {
            Settings.popServer = popServer.getText();
            Settings.popPort = Integer.parseInt(popPort.getText());
            Settings.popUser = popUser.getText();
            Settings.popPass = popPass.getText();

            Settings.smtpServer = smtpServer.getText();
            Settings.smtpPort = Integer.parseInt(smtpPort.getText());
            Settings.smtpUser = smtpUser.getText();
            Settings.smtpPass = smtpPass.getText();

            cancel(actionEvent);
        } catch(Exception ex) {
            new AlertDialog("Settings could not be saved:\n" + ex.getMessage());
        }

    }

    public Stage getStage(ActionEvent actionEvent) {
        Node source = (Node) actionEvent.getSource();
        return (Stage) source.getScene().getWindow();
    }

    public void cancel(ActionEvent actionEvent) {
        getStage(actionEvent).close();
    }

    public void init() {
        popServer.setText(Settings.popServer);
        popPort.setText(Settings.popPort + "");
        popUser.setText(Settings.popUser);
        popPass.setText(Settings.popPass);

        smtpServer.setText(Settings.smtpServer);
        smtpPort.setText(Settings.smtpPort + "");
        smtpUser.setText(Settings.smtpUser);
        smtpPass.setText(Settings.smtpPass);
    }
}
