package app;

import app.gui.MenuController;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.io.IOException;

public class ForgalomApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        var scene = new Scene(new Pane());

        MenuController.setScene(scene);
        MenuController.loadView("startup");

        stage.setScene(scene);
        stage.show();
    }

    public static SessionFactory getDbSessionFactory() {
        Configuration cfg = new Configuration().configure(ForgalomApplication.class.getResource("hibernate.cfg.xml"));
        return cfg.buildSessionFactory();
    }

    public static void main(String[] args) {
        launch();
    }
}