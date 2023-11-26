package app.gui.soap;

import app.ForgalomApplication;
import app.soap.DownloadManager;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;

public class DownloadAllController {
    @FXML
    private void downloadAll(ActionEvent event) {
        Button button = (Button)event.getSource();
        button.setDisable(true);
        ForgalomApplication.getStage().getScene().getRoot().setCursor(Cursor.WAIT);
        new Thread(() -> {
            try {
                DownloadManager.downloadCurrencies(false);
                DownloadManager.downloadRates(null, null, null);
            } finally {
                Platform.runLater(() -> {
                    ForgalomApplication.getStage().getScene().getRoot().setCursor(Cursor.DEFAULT);
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "Az összes adat sikeresen letöltésre került!", ButtonType.OK);
                    alert.show();
                    button.setDisable(false);
                });
            }
        }).start();
    }
}
