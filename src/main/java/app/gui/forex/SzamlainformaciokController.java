package app.gui.forex;

import app.forex.Config;
import com.oanda.v20.Context;
import com.oanda.v20.account.AccountSummary;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.HashMap;
import java.util.Map;

public class SzamlainformaciokController {
    @FXML
    TableView<Map.Entry<String, String>> szamlaInfok;

    @FXML
    private void initialize() {
        try {
            Context ctx = new Context(Config.URL, Config.TOKEN);
            AccountSummary summary = ctx.account.summary(Config.ACCOUNTID).getAccount();
            String str = summary.toString();
            str = str.substring("AccountSummary(".length(), str.lastIndexOf(")"));
            Map<String, String> infok = new HashMap<>();
            for (String values : str.split(", ")) {
                String[] value = values.split("=");
                infok.put(value[0], value[1]);
            }

            TableColumn<Map.Entry<String, String>, String> tulajdonsag = new TableColumn<>("Tulajdonság");
            TableColumn<Map.Entry<String, String>, String> ertek = new TableColumn<>("Érték");
            tulajdonsag.setCellValueFactory(cd -> new SimpleStringProperty(
                    cd.getValue().getKey()
            ));
            ertek.setCellValueFactory(cd -> new SimpleStringProperty(
                    cd.getValue().getValue()
            ));
            szamlaInfok.getColumns().setAll(tulajdonsag, ertek);
            Platform.runLater(()-> szamlaInfok.setItems(FXCollections.observableArrayList(infok.entrySet())));
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage(), ButtonType.OK);
            alert.show();
        }
    }
}
