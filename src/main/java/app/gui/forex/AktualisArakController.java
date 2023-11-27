package app.gui.forex;

import app.forex.Config;
import com.oanda.v20.Context;
import com.oanda.v20.ContextBuilder;
import com.oanda.v20.pricing.ClientPrice;
import com.oanda.v20.pricing.PricingGetRequest;
import com.oanda.v20.pricing.PricingGetResponse;
import com.oanda.v20.primitives.Instrument;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.text.Text;
import javafx.util.StringConverter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.*;

public class AktualisArakController {
    @FXML
    ComboBox<Instrument> devizaCombo;
    @FXML
    Text aktualis;

    @FXML
    private void initialize() {
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
    private void lekerdez(ActionEvent event) {
        try {
            Button button = (Button)event.getSource();
            button.setDisable(true);

            String devizapar = devizaCombo.getSelectionModel().getSelectedItem().getName().toString();
            System.out.println(devizapar);

            List<String> instruments = new ArrayList<>(Arrays.asList(devizapar));


            new Timer().scheduleAtFixedRate(new TimerTask() {
                String[]  progress = {"\\", "|", "/", "--"};
                int counter = 0;

                @Override
                public void run() {
                    Context ctx = new ContextBuilder(Config.URL).setToken(Config.TOKEN).setApplication("PricePolling").build();
                    PricingGetRequest request = new PricingGetRequest(Config.ACCOUNTID, instruments);
                    try {
                        PricingGetResponse response = ctx.pricing.get(request);
                        ClientPrice price = response.getPrices().get(0);

                        Instant instant = Instant.parse(price.getTime());
                        LocalDateTime ldt = LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
                        String formattedTime = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM).format(ldt);

                        String text = String.format("%s\t%s\t\tvétel %s\t\teladás %s", progress[counter], formattedTime, price.getCloseoutBid(), price.getCloseoutAsk());
                        Platform.runLater(() -> aktualis.setText(text));
                        counter = (counter == progress.length - 1) ? 0 : counter + 1;
                    } catch (Exception e) {
                        Platform.runLater(() -> {
                            Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage(), ButtonType.OK);
                            alert.show();
                        });
                        cancel();
                    }
                }
            }, 0, 1000L);
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage(), ButtonType.OK);
            alert.show();
        }
    }
}
