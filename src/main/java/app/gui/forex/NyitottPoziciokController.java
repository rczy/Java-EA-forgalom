package app.gui.forex;

import app.forex.Config;
import com.oanda.v20.Context;
import com.oanda.v20.ContextBuilder;
import com.oanda.v20.instrument.Candlestick;
import com.oanda.v20.trade.Trade;
import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;

public class NyitottPoziciokController {
    @FXML
    TableView<Trade> nyitottPoziciok;

    @FXML
    private void initialize() {
        try {
            initTableColumns();
            Context ctx = new ContextBuilder(Config.URL).setToken(Config.TOKEN).setApplication("OpenTradesList").build();
            List<Trade> trades = ctx.trade.listOpen(Config.ACCOUNTID).getTrades();
            Platform.runLater(()-> nyitottPoziciok.setItems(FXCollections.observableList(trades)));
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage(), ButtonType.OK);
            alert.show();
        }
    }

    private void initTableColumns() {
        TableColumn<Trade, String> id = new TableColumn<>("ID");
        TableColumn<Trade, String> instrument = new TableColumn<>("Devizapár kódja");
        TableColumn<Trade, String> date = new TableColumn<>("Dátum");
        TableColumn<Trade, Double> units = new TableColumn<>("Egység");
        TableColumn<Trade, Double> price = new TableColumn<>("Ár");
        TableColumn<Trade, Double> upl = new TableColumn<>("Nem realizált nyereség/veszteség");

        id.setCellValueFactory(cd -> new SimpleStringProperty(
                cd.getValue().getId().toString()
        ));
        instrument.setCellValueFactory(cd -> new SimpleStringProperty(
                cd.getValue().getInstrument().toString()
        ));
        date.setCellValueFactory(cd -> {
            Instant instant = Instant.parse(cd.getValue().getOpenTime().toString());
            LocalDateTime ldt = LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
            String formattedTime = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM).format(ldt);
            return new SimpleStringProperty(formattedTime);
        });
        units.setCellValueFactory(cd -> new SimpleDoubleProperty(
                cd.getValue().getCurrentUnits().doubleValue()
        ).asObject());
        price.setCellValueFactory(cd -> new SimpleDoubleProperty(
                cd.getValue().getPrice().doubleValue()
        ).asObject());
        upl.setCellValueFactory(cd -> new SimpleDoubleProperty(
                cd.getValue().getUnrealizedPL().doubleValue()
        ).asObject());

        nyitottPoziciok.getColumns().add(id);
        nyitottPoziciok.getColumns().add(instrument);
        nyitottPoziciok.getColumns().add(date);
        nyitottPoziciok.getColumns().add(units);
        nyitottPoziciok.getColumns().add(price);
        nyitottPoziciok.getColumns().add(upl);
    }
}
