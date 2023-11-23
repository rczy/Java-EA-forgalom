package app.gui.crud;

import app.ForgalomApplication;
import app.models.Aru;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;

public class DeleteController {
    @FXML
    private ComboBox<Integer> kodCombo;

    @FXML
    private void initialize() {
        initComboBoxes();
    }

    private void initComboBoxes() {
        List<Integer> kodok;
        try (SessionFactory factory = ForgalomApplication.getDbSessionFactory()) {
            Session session = factory.openSession();
            Transaction t = session.beginTransaction();
            kodok = session.createQuery("SELECT aru.kod FROM Aru aru ").list();
            t.commit();
        }
        kodCombo.setItems(FXCollections.observableList(kodok));
    }

    @FXML
    private void deleteAru(ActionEvent event) {
        int aruKod = kodCombo.getSelectionModel().getSelectedItem();
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Biztosan törli a(z) '"+ aruKod + "' kódú terméket?", ButtonType.YES, ButtonType.NO);
        alert.showAndWait();
        if (alert.getResult() == ButtonType.YES) {
            try (SessionFactory factory = ForgalomApplication.getDbSessionFactory()) {
                Session session = factory.openSession();
                Transaction t = session.beginTransaction();
                Aru aru = new Aru();
                aru.setKod(aruKod);
                session.delete(aru);
                t.commit();
            }
            initComboBoxes();
            alert = new Alert(Alert.AlertType.INFORMATION, "Sikeres törlés!", ButtonType.OK);
            alert.show();
        }
    }
}
