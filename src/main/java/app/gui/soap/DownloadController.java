package app.gui.soap;

import app.ForgalomApplication;
import app.soap.db.Deviza;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DownloadController {
    private Map<String, CheckBox> currencies;
    @FXML
    FlowPane checkboxContainer;
    @FXML
    private void initialize() {
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
        for (String currency : currencies.keySet()) {
            if (currencies.get(currency).isSelected()) {
                System.out.println(currency);
            }
        }
    }
}
