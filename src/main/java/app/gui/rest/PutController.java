package app.gui.rest;

import app.rest.User;
import app.rest.UserRestClient;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

public class PutController {
    @FXML
    TextField idInput;
    @FXML
    TextField nameInput;
    @FXML
    TextField emailInput;
    @FXML
    ComboBox<String> genderCombo;
    @FXML
    ComboBox<String> statusCombo;

    @FXML
    private void put(ActionEvent event) {
        UserRestClient client = new UserRestClient();
        User user = new User(
                nameInput.getText(),
                emailInput.getText(),
                genderCombo.getSelectionModel().getSelectedItem(),
                statusCombo.getSelectionModel().getSelectedItem()
        );
        try {
            user.setId(Integer.parseInt(idInput.getText()));
            client.PUT(user);
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "A felhasználó módosításra került!", ButtonType.OK);
            alert.show();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage(), ButtonType.OK);
            alert.show();
        }
    }
}
