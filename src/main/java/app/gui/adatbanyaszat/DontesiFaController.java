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

import java.util.concurrent.atomic.AtomicBoolean;

public class DontesiFaController {
    @FXML
    private void indit(ActionEvent event) {
        String file = "../Döntési fa.txt";
        Button button = (Button)event.getSource();
        button.setDisable(true);
        ForgalomApplication.getStage().getScene().getRoot().setCursor(Cursor.WAIT);
        AtomicBoolean success = new AtomicBoolean();
        new Thread(() -> {
            try {
                GepiTanulas gt = new GepiTanulas("data/diabetes.arff", 8, new J48(), false);
                gt.printSummary(true, file);
                success.set(true);
            } catch (Exception e) {
                success.set(false);
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage(), ButtonType.OK);
                    alert.show();
                });
            } finally {
                Platform.runLater(() -> {
                    if (success.get()) {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Az eredmény kiírásra került a(z) " + file + " fájlba.", ButtonType.OK);
                        alert.show();
                    }
                    ForgalomApplication.getStage().getScene().getRoot().setCursor(Cursor.DEFAULT);
                    button.setDisable(false);
                });
            }
        }).start();
    }
}
