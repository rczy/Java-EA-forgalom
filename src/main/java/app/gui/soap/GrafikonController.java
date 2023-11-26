package app.gui.soap;

import app.ForgalomApplication;
import app.models.Aru;
import app.soap.DownloadManager;
import app.soap.db.Arfolyam;
import app.soap.db.Deviza;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.StringConverter;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.*;

public class GrafikonController {
    private Map<String, CheckBox> currencies;
    @FXML
    FlowPane checkboxContainer;
    @FXML
    DatePicker minDatePicker;
    @FXML
    DatePicker maxDatePicker;
    @FXML
    VBox grafikonContainer;

    @FXML
    private void initialize() {
        currencies = initializeCurrencies();
    }

    private Map<String, CheckBox> initializeCurrencies() {
        Session session = ForgalomApplication.getDbSessionFactory().openSession();
        Transaction t = session.beginTransaction();

        List<Deviza> devizak = session.createQuery("FROM Deviza ").list();

        t.commit();
        session.close();

        Map<String, CheckBox> currencies = new HashMap<>();
        for (Deviza currency : devizak) {
            CheckBox checkbox = new CheckBox(currency.getKod());
            checkbox.setPrefWidth(80);
            currencies.put(currency.getKod(), checkbox);
            checkboxContainer.getChildren().add(checkbox);
        }
        return currencies;
    }

    @FXML
    private void draw(ActionEvent event) {
        // input adatok kinyerése
        List<String> selectedCurrencies = new ArrayList<>();
        LocalDate minDate = minDatePicker.getValue();
        LocalDate maxDate = maxDatePicker.getValue();
        for (String currency : currencies.keySet()) {
            if (currencies.get(currency).isSelected()) {
                selectedCurrencies.add(currency);
            }
        }
        // "validáció"
        if (minDate == null || maxDate == null || selectedCurrencies.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Válasszon devizá(ka)t, illetve kezdő- és végdátumot!", ButtonType.OK);
            alert.show();
            return;
        }
        grafikonContainer.getChildren().clear();
        // árfolyamok lekérdezése és megjelenítése
        try (SessionFactory factory = ForgalomApplication.getDbSessionFactory()) {
            Session session = factory.openSession();
            Transaction t = session.beginTransaction();

            for (String deviza: selectedCurrencies) {
                Criteria criteria = session.createCriteria(Arfolyam.class);
                criteria.add(Restrictions.eq("deviza", deviza));
                criteria.add(Restrictions.between("datum", minDate, maxDate));
                List<Arfolyam> arfolyamAdatok = criteria.list();
                System.out.println(arfolyamAdatok.size());
                grafikonContainer.getChildren().add(new Separator());
                if (arfolyamAdatok.isEmpty()) {
                    Text text = new Text(deviza + ": nincs megjeleníthető adat a kiválasztott időszakban");
                    text.setStyle("-fx-font-weight: bold");
                    grafikonContainer.getChildren().add(text);
                } else {
                    Arfolyam first = arfolyamAdatok.stream().min(Comparator.comparing(Arfolyam::getDatum)).orElseThrow(NoSuchElementException::new);
                    Arfolyam last = arfolyamAdatok.stream().max(Comparator.comparing(Arfolyam::getDatum)).orElseThrow(NoSuchElementException::new);
                    NumberAxis xAxis = new NumberAxis();
                    xAxis.setTickLabelFormatter(getDateConverter());
                    xAxis.setAutoRanging(false);
                    xAxis.setLowerBound(first.getDatum().toEpochDay());
                    xAxis.setUpperBound(last.getDatum().toEpochDay());

                    Arfolyam min = arfolyamAdatok.stream().min(Comparator.comparing(Arfolyam::getArfolyam)).orElseThrow(NoSuchElementException::new);
                    Arfolyam max = arfolyamAdatok.stream().max(Comparator.comparing(Arfolyam::getArfolyam)).orElseThrow(NoSuchElementException::new);
                    NumberAxis yAxis = new NumberAxis();
                    yAxis.setAutoRanging(false);
                    yAxis.setLowerBound(min.getArfolyam());
                    yAxis.setUpperBound(max.getArfolyam());

                    LineChart<Number,Number> lineChart = new LineChart<>(xAxis, yAxis);
                    lineChart.setTitle(deviza + " árfolyam");
                    XYChart.Series series = new XYChart.Series();
                    for (Arfolyam arfolyam : arfolyamAdatok) {
                        series.getData().add(new XYChart.Data(arfolyam.getDatum().toEpochDay(), arfolyam.getArfolyam()));
                    }
                    lineChart.getData().add(series);
                    lineChart.setLegendVisible(false);
                    lineChart.setPadding(new Insets(0, 100, 0, 100));
                    grafikonContainer.getChildren().add(lineChart);
                }
            }
            t.commit();
        }
    }

    private StringConverter<Number> getDateConverter() {
        StringConverter<Number> dateConverter = new StringConverter<Number>() {
            @Override public String toString(Number date) {
                DateTimeFormatter dtf = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM);
                return dtf.format(LocalDate.ofEpochDay(date.longValue()));
            }
            @Override public Number fromString(String s) {
                return LocalDate.parse(s).toEpochDay();
            }

        };
        return dateConverter;
    }
}
