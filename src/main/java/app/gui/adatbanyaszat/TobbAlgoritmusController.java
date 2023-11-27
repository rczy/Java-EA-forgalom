package app.gui.adatbanyaszat;

import app.ForgalomApplication;
import app.adatbanyaszat.GepiTanulas;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.text.Text;
import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.SMO;
import weka.classifiers.lazy.IBk;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.RandomForest;
import weka.core.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class TobbAlgoritmusController {
    Map<String, Classifier> algos;

    @FXML
    CheckBox randomize;
    @FXML
    Text legjobb;

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
    }

    @FXML
    private void indit(ActionEvent event) throws FileNotFoundException {
        String outputFile = "../Gépi tanulás.txt";
        (new File(outputFile)).delete();

        Button button = (Button)event.getSource();
        button.setDisable(true);
        ForgalomApplication.getStage().getScene().getRoot().setCursor(Cursor.WAIT);

        new Thread(() -> {
            Map<String, Integer> results = new HashMap<>();
            AtomicBoolean success = new AtomicBoolean();
            try {
                PrintWriter pw = new PrintWriter(new FileOutputStream(outputFile, true));
                algos.forEach((algoName, classifier) -> {
                    try {
                        pw.println(algoName + ":\n");
                        GepiTanulas gt = new GepiTanulas("data/diabetes.arff", 8, classifier, randomize.isSelected());
                        String summary = gt.printSummary(false, null);
                        pw.println(summary);
                        results.put(algoName, gt.getNoOfCorrect());
                        pw.println("-".repeat(20) + "\n");
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
                pw.close();
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
                        String best = Collections.max(results.entrySet(), Map.Entry.comparingByValue()).getKey();
                        legjobb.setText("A legpontosabbnak bizonyuló algoritmus: " +  best);
                        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Az eredmény kiírásra került a(z) " + outputFile + " fájlba.", ButtonType.OK);
                        alert.show();
                    }
                    ForgalomApplication.getStage().getScene().getRoot().setCursor(Cursor.DEFAULT);
                    button.setDisable(false);
                });
            }
        }).start();
    }
}
