package app.gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.event.ActionEvent;
import javafx.scene.control.MenuItem;

import java.io.IOException;
import java.net.URL;

public class MenuController {
    private static Scene scene;

    public static void setScene(Scene scene) {
        MenuController.scene = scene;
    }

    @FXML
    private void loadView(ActionEvent event) throws IOException {
        MenuItem menuItem = (MenuItem) event.getSource();
        String view = (String) menuItem.getUserData();
        loadView(view);
    }

    public static void loadView(String view) throws IOException {
        String path = view + ".fxml";
        URL resource = MenuController.class.getResource(path);
        if (resource == null) {
            System.err.println(path + " nézet nem található!");
            return;
        }
        Parent root = FXMLLoader.load(resource);
        scene.setRoot(root);
    }
}
