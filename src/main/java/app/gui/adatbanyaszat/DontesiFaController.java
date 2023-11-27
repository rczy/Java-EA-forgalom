package app.gui.adatbanyaszat;

import app.ForgalomApplication;
import app.adatbanyaszat.GepiTanulas;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import weka.classifiers.trees.J48;

public class DontesiFaController {
    @FXML
    private void indit(ActionEvent event) {
        String file = "../Döntési fa.txt";
        Button button = (Button)event.getSource();
        button.setDisable(true);
        ForgalomApplication.getStage().getScene().getRoot().setCursor(Cursor.WAIT);
        new Thread(() -> {
            try {
                GepiTanulas gt = new GepiTanulas("data/diabetes.arff", 8, new J48());
                gt.printSummary(true, file);
            } catch (Exception e) {
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage(), ButtonType.OK);
                    alert.show();
                });
            } finally {
                Platform.runLater(() -> {
                    ForgalomApplication.getStage().getScene().getRoot().setCursor(Cursor.DEFAULT);
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "Az eredmény kiírásra került a(z) " + file + " fájlba.", ButtonType.OK);
                    alert.show();
                    button.setDisable(false);
                });
            }
        }).start();
    }
}
