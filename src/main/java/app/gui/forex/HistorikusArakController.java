package app.gui.forex;

import app.forex.Config;
import com.oanda.v20.Context;
import com.oanda.v20.ContextBuilder;
import com.oanda.v20.instrument.Candlestick;
import com.oanda.v20.instrument.CandlestickGranularity;
import com.oanda.v20.instrument.InstrumentCandlesRequest;
import com.oanda.v20.instrument.InstrumentCandlesResponse;
import com.oanda.v20.primitives.DateTime;
import com.oanda.v20.primitives.Instrument;
import com.oanda.v20.primitives.InstrumentName;
import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;

public class HistorikusArakController {
    @FXML
    ComboBox<Instrument> devizaCombo;
    @FXML
    DatePicker minDatePicker;
    @FXML
    DatePicker maxDatePicker;
    @FXML
    VBox grafikonContainer;
    @FXML
    TableView<Candlestick> tablazat;

    @FXML
    private void initialize() {
        initComboBox();
        initTableColumns();
    }

    private void initComboBox() {
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

    private void initTableColumns() {
        TableColumn<Candlestick, String> date = new TableColumn<>("Dátum");
        TableColumn<Candlestick, Double> opening = new TableColumn<>("Nyitó ár");
        TableColumn<Candlestick, Double> highest = new TableColumn<>("Legmagasabb ár");
        TableColumn<Candlestick, Double> lowest = new TableColumn<>("Legalacsonyabb ár");
        TableColumn<Candlestick, Double> closing = new TableColumn<>("Záró ár");

        date.setCellValueFactory(cd -> {
            Instant instant = Instant.parse(cd.getValue().getTime().toString());
            LocalDateTime ldt = LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
            String formattedTime = DateTimeFormatter.ofPattern("yyyy. MM. dd.").format(ldt);
            return new SimpleStringProperty(formattedTime);
        });
        opening.setCellValueFactory(cd -> new SimpleDoubleProperty(
                cd.getValue().getMid().getO().doubleValue()
        ).asObject());
        highest.setCellValueFactory(cd -> new SimpleDoubleProperty(
                cd.getValue().getMid().getH().doubleValue()
        ).asObject());
        lowest.setCellValueFactory(cd -> new SimpleDoubleProperty(
                cd.getValue().getMid().getL().doubleValue()
        ).asObject());
        closing.setCellValueFactory(cd -> new SimpleDoubleProperty(
                cd.getValue().getMid().getC().doubleValue()
        ).asObject());

        tablazat.getColumns().add(date);
        tablazat.getColumns().add(opening);
        tablazat.getColumns().add(highest);
        tablazat.getColumns().add(lowest);
        tablazat.getColumns().add(closing);
    }

    @FXML
    private void lekerdez(ActionEvent event) {
        LocalDate minDate = minDatePicker.getValue();
        LocalDate maxDate = maxDatePicker.getValue();
        if (minDate == null || maxDate == null || minDate.isAfter(maxDate) || maxDate.isAfter(LocalDate.now())) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Válasszon kezdő és záró dátumot!\n\nA kezdő dátum nem előzheti meg a zárót, illetve a záró dátum nem lehet a mainál későbbi.", ButtonType.OK);
            alert.show();
            return;
        }
        try {
            String devizapar = devizaCombo.getSelectionModel().getSelectedItem().getName().toString();
            System.out.println(devizapar);

            Context ctx = new ContextBuilder(Config.URL).setToken(Config.TOKEN).setApplication("HistoricalPrices").build();
            InstrumentCandlesRequest request = new InstrumentCandlesRequest(new InstrumentName(devizapar));
            request.setFrom(minDate.toString());
            request.setTo(maxDate.toString());
            request.setGranularity(CandlestickGranularity.D);
            InstrumentCandlesResponse response = ctx.instrument.candles(request);
            for (Candlestick candle : response.getCandles()) {
                System.out.println(candle.getTime() + " " + candle.getMid().getC());
            }
            Platform.runLater(()-> {
                grafikonKirajzol(response.getCandles());
                tablazat.setItems(FXCollections.observableList(response.getCandles()));
                tablazat.setVisible(true);
            });
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage(), ButtonType.OK);
            alert.show();
        }
    }

    private void grafikonKirajzol(List<Candlestick> candles) {
        grafikonContainer.getChildren().clear();
        Comparator<Candlestick> timeComparator = Comparator.comparing(c -> convertToEpochDays(c.getTime()));
        Candlestick first = candles.stream().min(timeComparator).orElseThrow(NoSuchElementException::new);
        Candlestick last = candles.stream().max(timeComparator).orElseThrow(NoSuchElementException::new);
        NumberAxis xAxis = new NumberAxis();
        xAxis.setTickLabelFormatter(getDateConverter());
        xAxis.setAutoRanging(false);
        xAxis.setLowerBound(convertToEpochDays(first.getTime()));
        xAxis.setUpperBound(convertToEpochDays(last.getTime()));

        Comparator<Candlestick> closeComparator = Comparator.comparing(c -> c.getMid().getC().doubleValue());
        Candlestick min = candles.stream().min(closeComparator).orElseThrow(NoSuchElementException::new);
        Candlestick max = candles.stream().max(closeComparator).orElseThrow(NoSuchElementException::new);
        NumberAxis yAxis = new NumberAxis();
        yAxis.setAutoRanging(false);
        yAxis.setLowerBound(min.getMid().getC().doubleValue());
        yAxis.setUpperBound(max.getMid().getC().doubleValue());

        LineChart<Number,Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("Záró árfolyam alakulása");
        XYChart.Series series = new XYChart.Series();
        for (Candlestick candle : candles) {
            series.getData().add(new XYChart.Data(convertToEpochDays(candle.getTime()), candle.getMid().getC().doubleValue()));
        }
        lineChart.getData().add(series);
        lineChart.setLegendVisible(false);
        lineChart.setPadding(new Insets(0, 100, 0, 100));
        grafikonContainer.getChildren().add(lineChart);
    }

    private StringConverter<Number> getDateConverter() {
        return new StringConverter<>() {
            @Override
            public String toString(Number date) {
                DateTimeFormatter dtf = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM);
                return dtf.format(LocalDate.ofEpochDay(date.longValue()));
            }
            @Override
            public Number fromString(String s) {
                return LocalDate.parse(s).toEpochDay();
            }
        };
    }

    private long convertToEpochDays(DateTime time) {
        Instant instant = Instant.parse(time.toString());
        return LocalDate.ofInstant(instant, ZoneOffset.UTC).toEpochDay();
    }
}
