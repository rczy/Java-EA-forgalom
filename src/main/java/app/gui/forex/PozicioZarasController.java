package app.gui.forex;

import app.forex.Config;
import com.oanda.v20.Context;
import com.oanda.v20.ContextBuilder;
import com.oanda.v20.trade.TradeCloseRequest;
import com.oanda.v20.trade.TradeCloseResponse;
import com.oanda.v20.trade.TradeSpecifier;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;

public class PozicioZarasController {
    @FXML
    TextField idField;

    @FXML
    private void lezar(ActionEvent event) {
        try {
            Context ctx = new ContextBuilder(Config.URL).setToken(Config.TOKEN).setApplication("CloseTrade").build();
            TradeCloseResponse response = ctx.trade.close(new TradeCloseRequest(Config.ACCOUNTID, new TradeSpecifier(idField.getText())));
            String egyenleg = response.getOrderFillTransaction().getAccountBalance().toString();
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "A(z) " + idField.getText() + " azonosítójú pozíció sikeresen lezárásra került.\n\nEgyenleg: " + egyenleg, ButtonType.OK);
            alert.show();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage(), ButtonType.OK);
            alert.show();
        }

    }
}
