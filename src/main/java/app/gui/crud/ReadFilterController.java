package app.gui.crud;

import app.ForgalomApplication;
import app.models.Aru;
import app.models.Kategoria;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.StringConverter;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.ArrayList;
import java.util.List;

public class ReadFilterController {
    @FXML
    private TableView<Aru> aruTable;
    @FXML
    private ComboBox<Kategoria> kategoriaCombo;
    @FXML
    private ComboBox<String> relacioCombo;

    @FXML
    private void initialize() {
        initTableColumns();
        initComboBoxes();
    }

    private void initTableColumns() {
        TableColumn<Aru, String> kategoria = new TableColumn<>("Kategória");
        TableColumn<Aru, String> megnevezes = new TableColumn<>("Megnevezés");
        TableColumn<Aru, Integer> ar = new TableColumn<>("Ár");
        TableColumn<Aru, Integer> mennyiseg = new TableColumn<>("Eladott mennyiség");
        TableColumn<Aru, String> egyseg = new TableColumn<>("Egység");
        TableColumn<Aru, Integer> osszesen = new TableColumn<>("Összesen");

        kategoria.setCellValueFactory(cd -> new SimpleStringProperty(
                cd.getValue().getKategoria().getNev()
        ));
        megnevezes.setCellValueFactory(cd -> new SimpleStringProperty(
                cd.getValue().getNev()
        ));
        ar.setCellValueFactory(cd -> new SimpleIntegerProperty(
                cd.getValue().getAr()
        ).asObject());
        mennyiseg.setCellValueFactory(cd -> new SimpleIntegerProperty(
                cd.getValue().getEladas().getMennyiseg()
        ).asObject());
        egyseg.setCellValueFactory(cd -> new SimpleStringProperty(
                cd.getValue().getEgyseg()
        ));
        osszesen.setCellValueFactory(cd -> new SimpleIntegerProperty(
                cd.getValue().getAr() * cd.getValue().getEladas().getMennyiseg()
        ).asObject());

        aruTable.getColumns().add(kategoria);
        aruTable.getColumns().add(megnevezes);
        aruTable.getColumns().add(ar);
        aruTable.getColumns().add(mennyiseg);
        aruTable.getColumns().add(egyseg);
        aruTable.getColumns().add(osszesen);
    }

    private void initComboBoxes() {

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
        List<String> relaciok = new ArrayList<>();
        relaciok.add("<");
        relaciok.add(">");
        relaciok.add("=");
        relacioCombo.setItems(FXCollections.observableList(relaciok));
        relacioCombo.getSelectionModel().selectFirst();
    }

    @FXML
    private void filterAruk(ActionEvent event) {
        System.out.println("FILTER");
        System.out.println(kategoriaCombo.getSelectionModel().getSelectedItem().getKod());
    }
}
