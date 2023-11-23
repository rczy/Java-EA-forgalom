package app.gui.crud;

import app.ForgalomApplication;
import app.models.Aru;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;

public class ReadController {
    @FXML
    private TableView<Aru> aruTable;

    @FXML
    private void initialize() {
        initTableColumns();

        for (Aru aru : readAruk()) {
            aruTable.getItems().add(aru);
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

    private List<Aru> readAruk() {
        List<Aru> aruk;
        try (SessionFactory factory = ForgalomApplication.getDbSessionFactory()) {
            Session session = factory.openSession();
            Transaction t = session.beginTransaction();
            aruk = session.createQuery("FROM Aru").list();
            t.commit();
        }
        return aruk;
    }
}
