package app.gui.adatbanyaszat;

import app.ForgalomApplication;
import app.adatbanyaszat.GepiTanulas;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.SMO;
import weka.classifiers.lazy.IBk;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.RandomForest;
import weka.core.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class TobbAlgoritmus2Controller {
    Map<String, Classifier> algos;

    @FXML
    CheckBox randomize;
    @FXML
    Text eredmeny;
    @FXML
    ComboBox<String> algoCombo;

    @FXML
    private void initialize() {
        IBk ibk = new IBk();
        try {
            ibk.setOptions(Utils.splitOptions("-K 10"));
        } catch (Exception e) {
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage(), ButtonType.OK);
                alert.show();
            });
        }
        algos = new HashMap<>();
        algos.put("J48", new J48());
        algos.put("SMO", new SMO());
        algos.put("NaiveBayes", new NaiveBayes());
        algos.put("IBk", ibk);
        algos.put("RandomForest", new RandomForest());
        List<String> algoNames = new ArrayList<>(algos.keySet());
        algoCombo.setItems(FXCollections.observableList(algoNames));
        algoCombo.getSelectionModel().selectFirst();
    }

    @FXML
    private void indit(ActionEvent event) {

        Button button = (Button)event.getSource();
        button.setDisable(true);
        ForgalomApplication.getStage().getScene().getRoot().setCursor(Cursor.WAIT);

        new Thread(() -> {
            AtomicReference<String> summary = new AtomicReference<>("");
            AtomicBoolean success = new AtomicBoolean();
            try {
                try {
                    Classifier classifier = algos.get(
                      algoCombo.getSelectionModel().getSelectedItem()
                    );
                    GepiTanulas gt = new GepiTanulas("data/diabetes.arff", 8, classifier, randomize.isSelected());
                    summary.set(gt.printSummary(false, null));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                success.set(true);
            } catch (Exception e) {
                success.set(false);
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage(), ButtonType.OK);
                    alert.show();
                });
            } finally {
                Platform.runLater(() -> {
                    if (success.get()) {
                        eredmeny.setText(summary.get());
                    }
                    ForgalomApplication.getStage().getScene().getRoot().setCursor(Cursor.DEFAULT);
                    button.setDisable(false);
                });
            }
        }).start();
    }
}
