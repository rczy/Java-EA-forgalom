package app.gui.crud;

import app.ForgalomApplication;
import app.models.Aru;
import app.models.Kategoria;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;

public class UpdateController {
    @FXML
    private ComboBox<Integer> kodCombo;
    @FXML
    private ComboBox<Kategoria> kategoriaCombo;
    @FXML
    private TextField megnevezesInput;
    @FXML
    private TextField egysegInput;
    @FXML
    private TextField arInput;
    @FXML
    private TextField mennyisegInput;

    @FXML
    private void initialize() {
        initComboBoxes();
    }

    private void initComboBoxes() {
        List<Integer> kodok;
        List<Kategoria> kategoriak;

        try (SessionFactory factory = ForgalomApplication.getDbSessionFactory()) {
            Session session = factory.openSession();
            Transaction t = session.beginTransaction();
            kodok = session.createQuery("SELECT aru.kod FROM Aru aru ").list();
            kategoriak = session.createQuery("FROM Kategoria ").list();
            t.commit();
        }
        kodCombo.setItems(FXCollections.observableList(kodok));
        kategoriaCombo.setConverter(new StringConverter<>() {
            @Override
            public String toString(Kategoria object) {
                return object != null ? object.getNev() : "";
            }
            @Override
            public Kategoria fromString(String string) {
                return null;
            }
        });
        kategoriaCombo.setItems(FXCollections.observableList(kategoriak));
    }

    @FXML
    private void loadAru(ActionEvent event) {
        int aruKod = kodCombo.getSelectionModel().getSelectedItem();
        Aru aru;
        try (SessionFactory factory = ForgalomApplication.getDbSessionFactory()) {
            Session session = factory.openSession();
            Transaction t = session.beginTransaction();
            aru = session.get(Aru.class, aruKod);
            t.commit();
        }
        kategoriaCombo.getSelectionModel().select(aru.getKategoria());
        megnevezesInput.setText(aru.getNev());
        egysegInput.setText(aru.getEgyseg());
        arInput.setText(String.valueOf(aru.getAr()));
        mennyisegInput.setText(String.valueOf(aru.getEladas().getMennyiseg()));
    }

    @FXML
    private void updateAru(ActionEvent event) {
        int aruKod = kodCombo.getSelectionModel().getSelectedItem();
        Kategoria kategoria = kategoriaCombo.getSelectionModel().getSelectedItem();
        int ar, mennyiseg;
        try {
            ar = Integer.parseInt(arInput.getText());
            mennyiseg = Integer.parseInt(mennyisegInput.getText());
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Árnak és eladott mennyiségnek számot adjon meg!", ButtonType.OK);
            alert.show();
            return;
        }

        try (SessionFactory factory = ForgalomApplication.getDbSessionFactory()) {
            Session session = factory.openSession();
            Transaction t = session.beginTransaction();

            Aru aru = session.get(Aru.class, aruKod);
            if (aru.getKategoria().getKod() != kategoria.getKod()) {
                aru.setKategoria(kategoria);
            }
            aru.setNev(megnevezesInput.getText());
            aru.setEgyseg(egysegInput.getText());
            aru.setAr(ar);
            aru.getEladas().setMennyiseg(mennyiseg);
            session.update(aru);

            t.commit();
        }
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Sikeres mentés!", ButtonType.OK);
        alert.show();
    }
}
