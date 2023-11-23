package app.gui.rest;

import app.rest.User;
import app.rest.UserRestClient;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

public class DeleteController {
    @FXML
    TextField idInput;

    @FXML
    private void delete(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Biztosan törli a felhasználót?", ButtonType.YES, ButtonType.NO);
        alert.showAndWait();
        if (alert.getResult() != ButtonType.YES) {
            return;
        }

        try {
            UserRestClient client = new UserRestClient();
            User user = new User();
            user.setId(Integer.parseInt(idInput.getText()));
            client.DELETE(user);
            alert = new Alert(Alert.AlertType.INFORMATION, "A felhasználó törlésre került!", ButtonType.OK);
            alert.show();
        } catch (Exception e) {
            alert = new Alert(Alert.AlertType.ERROR, e.getMessage(), ButtonType.OK);
            alert.show();
        }
    }
}
