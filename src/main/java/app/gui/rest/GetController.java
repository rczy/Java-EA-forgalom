package app.gui.rest;

import app.rest.User;
import app.rest.UserRestClient;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.ArrayList;
import java.util.List;

public class GetController {
    @FXML
    TextField idInput;
    @FXML
    TableView<User> userTable;

    @FXML
    private void initialize() {
        initTableColumns();
    }

    private void initTableColumns() {
        TableColumn<User, Integer> id = new TableColumn<>("Id");
        TableColumn<User, String> name = new TableColumn<>("Name");
        TableColumn<User, String> email = new TableColumn<>("Email");
        TableColumn<User, String> gender = new TableColumn<>("Gender");
        TableColumn<User, String> status = new TableColumn<>("Status");

        id.setCellValueFactory(cd -> new SimpleIntegerProperty(
                cd.getValue().getId()
        ).asObject());
        name.setCellValueFactory(cd -> new SimpleStringProperty(
                cd.getValue().getName()
        ));
        email.setCellValueFactory(cd -> new SimpleStringProperty(
                cd.getValue().getEmail()
        ));
        gender.setCellValueFactory(cd -> new SimpleStringProperty(
                cd.getValue().getGender()
        ));
        status.setCellValueFactory(cd -> new SimpleStringProperty(
                cd.getValue().getStatus()
        ));

        userTable.getColumns().add(id);
        userTable.getColumns().add(name);
        userTable.getColumns().add(email);
        userTable.getColumns().add(gender);
        userTable.getColumns().add(status);
    }

    @FXML
    private void get(ActionEvent event) {
        UserRestClient client = new UserRestClient();
        List<User> users;
        try {
            if (idInput.getText().isEmpty()) {
                users = client.GET();
            } else {
                Integer id = Integer.parseInt(idInput.getText());
                users = new ArrayList<>();
                users.add(client.GET(id));
            }
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage(), ButtonType.OK);
            alert.show();
            return;
        }
        userTable.setItems(FXCollections.observableList(users));
    }
}
