package app;

import app.gui.MenuController;
import app.rest.UserRestClient;
import app.rest.User;
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
        MenuController.loadView("rest/post");

        stage.setScene(scene);
        stage.show();
    }

    public static SessionFactory getDbSessionFactory() {
        Configuration cfg = new Configuration().configure(ForgalomApplication.class.getResource("hibernate.cfg.xml"));
        return cfg.buildSessionFactory();
    }

    public static void main(String[] args) {
        try {
            //restTest();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        launch();
    }

    private static void restTest() throws Exception {
        UserRestClient client = new UserRestClient();

        User newusr = new User("teszt név", "ad@email.com", "male", "inactive");
        newusr = client.POST(newusr);

        newusr.setName("EHEJJ AHAJJ");
        client.PUT(newusr);

        //client.DELETE(newusr);

        for (User user : client.GET()) {
            System.out.println(user.getName() + " " + user.getEmail());
        }

    }
}