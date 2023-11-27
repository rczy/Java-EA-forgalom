package app.adatbanyaszat;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.trees.J48;
import weka.core.Instances;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.Random;

public class GepiTanulas {
    Instances instances, tanito, kiertekelo;
    Classifier classifier;
    Evaluation evaluation;
    double[] eredmeny;
    double ratio;

    public GepiTanulas(String fajl, int classIndex, Classifier algo) throws Exception {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(fajl));
        instances = new Instances(bufferedReader);
        instances.setClassIndex(classIndex);

        ratio = 0.8;
        int trainSize = (int) Math.round(instances.numInstances() * ratio);
        int testSize = instances.numInstances() - trainSize;
        tanito = new Instances(instances, 0, trainSize);
        kiertekelo = new Instances(instances, trainSize, testSize);

        classifier = algo;
        classifier.buildClassifier(tanito);
        evaluation = new Evaluation(kiertekelo);
        eredmeny = evaluation.evaluateModel(classifier, kiertekelo);
    }

    public int getTanitoHalmazMeret() {
        return tanito.size();
    }

    public int getKiertekeloHalmazMeret() {
        return kiertekelo.size();
    }

    public String getClassifierResult() {
        return classifier.toString();
    }

    public String getEvaluationResult() {
        StringBuilder sb = new StringBuilder();
        sb.append(evaluation.toSummaryString("\nResults", false));
        return sb.toString();
    }

    public String getCorrectlyIncorrectly() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Correctly Classified Instances:\t\t%d\n", (int)evaluation.correct()));
        sb.append(String.format("Incorrectly Classified Instances:\t%d\n", kiertekelo.size()-(int)evaluation.correct()));
        return sb.toString();
    }

    /**
     * TP:TtruePositive, TN:TrueNegative, FP:FalsePositive, FN:FalseNegative
     */
    public String getTpTnFpFn(boolean withResults) {
        StringBuilder summary = new StringBuilder();
        StringBuilder results = new StringBuilder();
        int TP = 0, TN = 0, FP = 0, FN = 0;
        for(int i=0;i<kiertekelo.size();i++) {
            results.append(String.format("%d\t%f\t%f\n", i, kiertekelo.get(i).classValue(), eredmeny[i]));
            if(kiertekelo.get(i).classValue() == 1 && eredmeny[i] == 1)
                TP++;
            if(kiertekelo.get(i).classValue() == 1 && eredmeny[i] == 0)
                FN++;
            if(kiertekelo.get(i).classValue() == 0 && eredmeny[i] == 1)
                FP++;
            if(kiertekelo.get(i).classValue() == 0 && eredmeny[i] == 0)
                TN++;
        }
        summary.append(String.format("TP=%d, TN=%d, FP=%d, FN=%d\n", TP, TN, FP, FN));
        summary.append(String.format("TP+TN=%d\n", TP + TN));
        summary.append(String.format("FP+FN=%d\n", FP + FN));
        if (withResults) {
            summary.append("\nPredikált és valós érték:\n");
            summary.append(results);
        }
        return summary.toString();
    }

    public String printSummary(boolean full, String file) throws FileNotFoundException {
        StringBuilder summary = new StringBuilder();
        if (full) {
            summary.append("Tanító halmaz mérete:\t\t").append(getTanitoHalmazMeret());
            summary.append("\nKiértékelő halmaz mérete:\t\n").append(getKiertekeloHalmazMeret());
            summary.append(getEvaluationResult());
        }
        summary.append(getCorrectlyIncorrectly());
        if (full) {
            summary.append("\n");
            summary.append(getClassifierResult());
        }
        summary.append("\n");
        summary.append(getTpTnFpFn(full));
        if (file != null) {
            PrintWriter pw = new PrintWriter(file);
            pw.println(summary);
            pw.close();
        }
        return summary.toString();
    }
}
