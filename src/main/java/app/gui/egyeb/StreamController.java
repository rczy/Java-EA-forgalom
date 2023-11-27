package app.gui.egyeb;

import app.ForgalomApplication;
import app.models.Aru;
import app.models.Kategoria;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.StringConverter;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;


import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StreamController {
    @FXML
    private TableView<Aru> aruTable;
    @FXML
    private ComboBox<Kategoria> kategoriaCombo;
    @FXML
    private TextField megnevezesInput;
    @FXML
    private CheckBox pontosCheck;
    @FXML
    private RadioButton arRadio;
    @FXML
    private TextField minInput;
    @FXML
    private TextField maxInput;

    List<Aru> aruk;

    @FXML
    private void initialize() {
        initTableColumns();
        initKategoriakComboBox();
        arRadio.setSelected(true);
        try (SessionFactory factory = ForgalomApplication.getDbSessionFactory()) {
            Session session = factory.openSession();
            Transaction t = session.beginTransaction();
            aruk = session.createQuery("FROM Aru").list();
            t.commit();
        }
    }

    private void initTableColumns() {
        TableColumn<Aru, Integer> kod = new TableColumn<>("Kód");
        TableColumn<Aru, String> kategoria = new TableColumn<>("Kategória");
        TableColumn<Aru, String> megnevezes = new TableColumn<>("Megnevezés");
        TableColumn<Aru, Integer> ar = new TableColumn<>("Ár");
        TableColumn<Aru, Integer> mennyiseg = new TableColumn<>("Eladott mennyiség");
        TableColumn<Aru, String> egyseg = new TableColumn<>("Egység");
        TableColumn<Aru, Integer> osszesen = new TableColumn<>("Összesen");

        kod.setCellValueFactory(cd -> new SimpleIntegerProperty(
                cd.getValue().getKod()
        ).asObject());
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

        aruTable.getColumns().add(kod);
        aruTable.getColumns().add(kategoria);
        aruTable.getColumns().add(megnevezes);
        aruTable.getColumns().add(ar);
        aruTable.getColumns().add(mennyiseg);
        aruTable.getColumns().add(egyseg);
        aruTable.getColumns().add(osszesen);
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
        Kategoria osszesKategoria = new Kategoria("-- összes --", null);
        osszesKategoria.setKod(0);
        kategoriaCombo.getItems().add(osszesKategoria);
        kategoriaCombo.getSelectionModel().selectLast();
    }

    @FXML
    private void filterAruk(ActionEvent event) {
        int kategoriaKod = kategoriaCombo.getSelectionModel().getSelectedItem().getKod();
        String megnevezes = megnevezesInput.getText();
        Boolean pontos = pontosCheck.isSelected();

        int min, max;
        try {
            min = Integer.parseInt(minInput.getText());
        } catch (NumberFormatException e) {
            min = Integer.MIN_VALUE;
        }
        try {
            max = Integer.parseInt(maxInput.getText());
        } catch (NumberFormatException e) {
            max = Integer.MAX_VALUE;
        }

        Stream<Aru> filterStream = aruk.stream();
        if (kategoriaKod > 0) {
            filterStream = filterStream.filter(aru -> aru.getKategoria().getKod() == kategoriaKod);
        }
        if (!megnevezes.isEmpty()) {
            filterStream = filterStream.filter(aru -> pontos ? aru.getNev().equals(megnevezes) : aru.getNev().contains(megnevezes));
        }
        if (min > Integer.MIN_VALUE || max < Integer.MAX_VALUE) {
            int finalMin = min;
            int finalMax = max;
            filterStream = filterStream.filter(aru -> {
                int prop = arRadio.isSelected() ? aru.getAr() : aru.getEladas().getMennyiseg();
                return prop >= finalMin && prop <= finalMax;
            });
        }
        List<Aru> filtered = filterStream.collect(Collectors.toList());
        Platform.runLater(()-> aruTable.setItems(FXCollections.observableList(filtered)));
    }
}
