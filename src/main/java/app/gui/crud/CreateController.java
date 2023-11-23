package app.gui.crud;

import app.ForgalomApplication;
import app.models.Aru;
import app.models.Eladas;
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

public class CreateController {
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
        initKategoriakComboBox();
    }

    private void initKategoriakComboBox() {

        List<Kategoria> kategoriak;
        try (SessionFactory factory = ForgalomApplication.getDbSessionFactory()) {
            Session session = factory.openSession();
            Transaction t = session.beginTransaction();
            kategoriak = session.createQuery("FROM Kategoria ").list();
            t.commit();
        }
        kategoriaCombo.setConverter(new StringConverter<>() {
            @Override
            public String toString(Kategoria object) {
                return object.getNev();
            }
            @Override
            public Kategoria fromString(String string) {
                return null;
            }
        });
        kategoriaCombo.setItems(FXCollections.observableList(kategoriak));
        kategoriaCombo.getSelectionModel().selectFirst();
    }

    @FXML
    private void createAru(ActionEvent event) {
        Kategoria kategoria = kategoriaCombo.getSelectionModel().getSelectedItem();
        int ar, mennyiseg;
        try {
            ar = Integer.parseInt(arInput.getText());
            mennyiseg = Integer.parseInt(mennyisegInput.getText());
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Árnak és eladott mennyiségnek számot adjon meg!", ButtonType.OK);
            alert.showAndWait();
            return;
        }

        //Eladas eladas = new Eladas();
        // kategoria, megnevezesInput.getText(), egysegInput.getText(), ar, eladas

        /*aru.setKategoria(kategoria);
        aru.setNev(megnevezesInput.getText());
        aru.setEgyseg(egysegInput.getText());
        aru.setAr(ar);*/
        /*eladas.setAru(aru);
        eladas.setMennyiseg(mennyiseg);*/

        try (SessionFactory factory = ForgalomApplication.getDbSessionFactory()) {
            Session session = factory.openSession();
            Transaction t = session.beginTransaction();

            Eladas eladas = new Eladas();
            Aru aru = new Aru(kategoria, megnevezesInput.getText(), egysegInput.getText(), ar, eladas);
            eladas.setAru(aru);
            eladas.setMennyiseg(mennyiseg);

            session.save(aru);
            session.save(eladas);

            t.commit();
        }

        System.out.println("okay");
    }
}
