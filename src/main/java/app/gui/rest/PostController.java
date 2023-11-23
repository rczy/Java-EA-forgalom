package app.gui.rest;

import app.rest.User;
import app.rest.UserRestClient;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

public class PostController {
    @FXML
    TextField nameInput;
    @FXML
    TextField emailInput;
    @FXML
    ComboBox<String> genderCombo;
    @FXML
    ComboBox<String> statusCombo;

    @FXML
    private void post(ActionEvent event) {
        UserRestClient client = new UserRestClient();
        User user = new User(
                nameInput.getText(),
                emailInput.getText(),
                genderCombo.getSelectionModel().getSelectedItem(),
                statusCombo.getSelectionModel().getSelectedItem()
        );
        try {
            user = client.POST(user);
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "A felhasználó létrejött! Id: " + user.getId(), ButtonType.OK);
            alert.show();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage(), ButtonType.OK);
            alert.show();
        }
    }
}
