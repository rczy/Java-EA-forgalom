package app.gui.soap;

import app.ForgalomApplication;
import app.soap.DownloadManager;
import app.soap.db.Deviza;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DownloadController {
    private Map<String, CheckBox> currencies;
    @FXML
    FlowPane checkboxContainer;
    @FXML
    DatePicker minDatePicker;
    @FXML
    DatePicker maxDatePicker;

    @FXML
    private void initialize() {
        DownloadManager.downloadCurrencies();
        currencies = initializeCurrencies();
    }

    private Map<String, CheckBox> initializeCurrencies() {
        Session session = ForgalomApplication.getDbSessionFactory().openSession();
        Transaction t = session.beginTransaction();

        List<Deviza> devizak = session.createQuery("FROM Deviza ").list();

        t.commit();
        session.close();

        Map<String, CheckBox> currencies = new HashMap<>();
        for (Deviza currency : devizak) {
            CheckBox checkbox = new CheckBox(currency.getKod());
            checkbox.setPrefWidth(80);
            currencies.put(currency.getKod(), checkbox);
            checkboxContainer.getChildren().add(checkbox);
        }
        return currencies;
    }

    @FXML
    private void download(ActionEvent event) {
        // input adatok kinyerése
        StringBuilder selectedCurrencies = new StringBuilder();
        LocalDate minDate = minDatePicker.getValue();
        LocalDate maxDate = maxDatePicker.getValue();
        for (String currency : currencies.keySet()) {
            if (currencies.get(currency).isSelected()) {
                selectedCurrencies.append(currency).append(",");
            }
        }
        // utolsó vessző törlése
        if (!selectedCurrencies.isEmpty()) {
            selectedCurrencies.deleteCharAt(selectedCurrencies.length() - 1);
        }
        // "validáció"
        if (minDate == null || maxDate == null || selectedCurrencies.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Válasszon devizá(ka)t, illetve kezdő és vég dátumot!", ButtonType.OK);
            alert.show();
            return;
        }
        // letöltés
        Button button = (Button)event.getSource();
        button.setDisable(true);
        ForgalomApplication.getStage().getScene().getRoot().setCursor(Cursor.WAIT);
        new Thread(() -> {
            try {
                DownloadManager.downloadRates(minDate.toString(), maxDate.toString(), selectedCurrencies.toString());
            } finally {
                Platform.runLater(() -> {
                    ForgalomApplication.getStage().getScene().getRoot().setCursor(Cursor.DEFAULT);
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "Az adatok a kiválasztott devizákra és időszakra sikeresen letöltésre kerültek!", ButtonType.OK);
                    alert.show();
                    button.setDisable(false);
                });
            }
        }).start();
    }
}
