package app.gui.crud;

import app.ForgalomApplication;
import app.models.Aru;
import app.models.Kategoria;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.StringConverter;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import java.util.List;

public class ReadFilterController {
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

    @FXML
    private void initialize() {
        initTableColumns();
        initKategoriakComboBox();
        arRadio.setSelected(true);
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

        List<Aru> aruk;
        try (SessionFactory factory = ForgalomApplication.getDbSessionFactory()) {
            Session session = factory.openSession();
            Transaction t = session.beginTransaction();

            Criteria criteria = session.createCriteria(Aru.class);
            if (kategoriaKod > 0) {
                criteria.add(Restrictions.eq("kategoria.kod", kategoriaKod));
            }
            if (!megnevezes.isEmpty()) {
                criteria.add(Restrictions.like("nev", megnevezes, pontos ? MatchMode.EXACT : MatchMode.ANYWHERE));
            }
            if (min > Integer.MIN_VALUE || max < Integer.MAX_VALUE) {
                String propName = arRadio.isSelected() ? "ar" : "eladas.mennyiseg";
                criteria.createAlias("eladas", "eladas").add(Restrictions.between(propName, min, max));
            }
            aruk = criteria.list();
            t.commit();
        }
        aruTable.setItems(FXCollections.observableList(aruk));
    }
}
