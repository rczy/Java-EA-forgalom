package app;

import app.models.Aru;
import app.models.Eladas;
import app.models.Kategoria;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import java.util.List;

public class HelloController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
        Configuration cfg = new Configuration().configure(HelloController.class.getResource("hibernate.cfg.xml"));
        try (SessionFactory factory = cfg.buildSessionFactory()) {
            Session session = factory.openSession();
            Transaction t = session.beginTransaction();

            /*Kategoria kategoria = new Kategoria("Teszt kategória", null);
            session.save(kategoria);
            Aru aru = new Aru(kategoria, "Teszt áru", "db", 15000, null);
            session.save(aru);
            Eladas eladas = new Eladas(aru, 42);
            session.save(eladas);*/

            List<Aru> aruk = session.createQuery("FROM Aru").list();
            for (Aru aru : aruk) {
                System.out.println(String.format("%s: %s %s", aru.getNev(), aru.getEladas().getMennyiseg(), aru.getEgyseg()));
            }
            t.commit();
        }
    }
}