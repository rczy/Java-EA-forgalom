package app.gui.forex;

import app.forex.Config;
import com.oanda.v20.Context;
import com.oanda.v20.ContextBuilder;
import com.oanda.v20.order.MarketOrderRequest;
import com.oanda.v20.order.OrderCreateRequest;
import com.oanda.v20.order.OrderCreateResponse;
import com.oanda.v20.primitives.Instrument;
import com.oanda.v20.primitives.InstrumentName;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;

import java.util.*;

public class PozicioNyitasController {
    @FXML
    ComboBox<Instrument> devizaCombo;
    @FXML
    TextField mennyisegInput;
    @FXML
    ComboBox<String> iranyCombo;

    @FXML
    private void initialize() {
        initDevizaComboBox();
    }

    private void initDevizaComboBox() {
        try {
            StringConverter<Instrument> converter = new StringConverter<>() {
                @Override
                public String toString(Instrument object) {
                    return object.getDisplayName();
                }
                @Override
                public Instrument fromString(String string) {
                    return null;
                }
            };
            devizaCombo.setConverter(converter);

            Context ctx = new Context(Config.URL, Config.TOKEN);
            List<Instrument> instruments = ctx.account.instruments(Config.ACCOUNTID).getInstruments();
            instruments.sort(Comparator.comparing(Instrument::getDisplayName));

            devizaCombo.setItems(FXCollections.observableList(instruments));
            devizaCombo.getSelectionModel().selectFirst();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage(), ButtonType.OK);
            alert.show();
        }
    }

    @FXML
    private void nyitas(ActionEvent event) {
        try {
            String devizapar = devizaCombo.getSelectionModel().getSelectedItem().getName().toString();
            Double mennyiseg = Double.parseDouble(mennyisegInput.getText());
            int irany = (Objects.equals(iranyCombo.getValue(), "eladás")) ? 1 : -1;
            Context ctx = new ContextBuilder(Config.URL).setToken(Config.TOKEN).setApplication("MarketOrder").build();
            OrderCreateRequest request = new OrderCreateRequest(Config.ACCOUNTID);
            MarketOrderRequest marketorderrequest = new MarketOrderRequest();
            marketorderrequest.setInstrument(new InstrumentName(devizapar));
            marketorderrequest.setUnits(mennyiseg * irany);
            request.setOrder(marketorderrequest);
            OrderCreateResponse response = ctx.order.create(request);
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Sikeres pozíció nyitás.\n\nAzonosító: " + response.getLastTransactionID(), ButtonType.OK);
            alert.show();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage(), ButtonType.OK);
            alert.show();
        }
    }
}
