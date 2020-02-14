package sample;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Created with IntelliJ IDEA.
 * User: leobernard
 * Date: 14.09.14
 * Time: 12:24
 */
public class AlertDialog extends Stage {

    public AlertDialog(String msg) {
        initModality(Modality.APPLICATION_MODAL);
        initStyle(StageStyle.DECORATED);

        Label label = new Label(msg);
        label.setWrapText(true);
        label.setGraphicTextGap(20);

        Button button = new Button("OK");
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                AlertDialog.this.close();
            }
        });

        BorderPane borderPane = new BorderPane();
        borderPane.setTop(label);
        borderPane.setPadding(new Insets(20, 20, 20, 20));

        HBox hbox2 = new HBox();
        hbox2.setAlignment(Pos.CENTER);
        hbox2.getChildren().add(button);
        borderPane.setBottom(hbox2);

        // calculate width of string
        final Text text = new Text(msg);
        text.snapshot(null, null);

        final Scene scene = new Scene(borderPane, 500, 300);
        setScene(scene);

        this.showAndWait();
    }
}