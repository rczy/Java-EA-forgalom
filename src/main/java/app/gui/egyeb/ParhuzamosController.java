package app.gui.egyeb;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.util.*;

public class ParhuzamosController {
    @FXML
    Label egyes;
    @FXML
    Label kettes;

    List<Label> labelList;

    @FXML
    private void initialize() {
        labelList = new ArrayList<>(Arrays.asList(egyes, kettes));
    }

    @FXML
    private void start(ActionEvent event) {
        Button button = (Button)event.getSource();
        button.setDisable(true);

        for (Label label : labelList) {
            int index = labelList.indexOf(label) + 1;
            new Timer().scheduleAtFixedRate(new TimerTask() {
                int counter = 0;
                @Override
                public void run() {
                    counter++;
                    Platform.runLater(() -> {
                        label.setText("számláló[" + index + "]:     " + counter);
                    });
                }
            }, 0, index * 1000L);
        }
    }
}
