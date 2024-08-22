package org.example.Distribuciones;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.example.Utils.BooleanUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Normal extends Distribucion {

    Scene normalInputScene;

    double[] media = new double[1];
    double[] desviacion = new double[1];

    public void distributionInput() {
        Stage stage = new Stage();
        stage.setTitle("Generador de Distribución Normal");

        // Check for each input if it's valid
        boolean[] areInputsValid = new boolean[2];

        // generateButton
        Button generateButton = new Button("Generar");
        generateButton.setDisable(true);

        // inputLabel
        Label inputLabel = new Label("Ingrese la media μ y la desviación σ:");

        // mediaInput
        TextField mediaInput = new TextField();
        mediaInput.setPromptText("Media μ");

        mediaInput.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                try {
                    media[0] = Double.parseDouble(newValue);
                    areInputsValid[0] = -1000000 <= media[0] && media[0] <= 1000000;
                } catch (NumberFormatException e) {
                    areInputsValid[0] = false;
                } finally {
                    generateButton.setDisable(BooleanUtils.isAnyValueOfArrayFalse(areInputsValid));
                }
            }
        });

        // desvacionInput
        TextField desviacionInput = new TextField();
        desviacionInput.setPromptText("Desviacion σ");

        desviacionInput.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                try {
                    desviacion[0] = Double.parseDouble(newValue);
                    areInputsValid[1] = 0 < desviacion[0] && desviacion[0] <= 1000000;
                } catch (NumberFormatException e) {
                    areInputsValid[1] = false;
                } finally {
                    generateButton.setDisable(BooleanUtils.isAnyValueOfArrayFalse(areInputsValid));
                }
            }
        });

        VBox vBoxTop = new VBox(inputLabel, mediaInput, desviacionInput);
        vBoxTop.setAlignment(Pos.CENTER);
        vBoxTop.setPadding(new Insets(20));
        vBoxTop.setSpacing(8);

        VBox vboxBottom = new VBox(generateButton);
        vboxBottom.setAlignment(Pos.BOTTOM_RIGHT);
        vboxBottom.setPadding(new Insets(10));
        vboxBottom.setSpacing(8);

        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(vBoxTop);
        borderPane.setBottom(vboxBottom);

        normalInputScene = new Scene(borderPane, 600, 200);
        stage.setScene(normalInputScene);
        stage.show();

        generateButton.setOnAction(e -> {
            ((Stage) normalInputScene.getWindow()).close();
            pruebaChiCuadrado();
            showResults();
        });
    }

    @Override
    public double[] generateRandomNumbersWithDistribution() {
        int N = rnd01.length;

        double[] normalNumbers = new double[N];
        for (int i = 0; i < N; i += 2) {
            // Obtener los 2 numeros aleatorios que se uaran en el metodo de box muller
            double rnd01Primero = rnd01[i];
            double rnd01Segundo = 0;
            if (i + 1 < N) {
                rnd01Segundo = rnd01[i + 1];
            } else {
                // Si N era impar, para el ultimo rnd01, se debe generar un ultimo rnd01
                rnd01Segundo = new Random().nextDouble();
            }

            // Metodo de box muller
            normalNumbers[i] = Math.sqrt(-2 * Math.log(1 - rnd01Primero)) *
                    Math.sin(2 * Math.PI * rnd01Segundo) *
                    desviacion[0] +
                    media[0];
            if (i + 1 < N) {
                normalNumbers[i + 1] = Math.sqrt(-2 * Math.log(1 - rnd01Primero)) *
                        Math.cos(2 * Math.PI * rnd01Segundo) *
                        desviacion[0] +
                        media[0];
            }
        }
        return normalNumbers;
    }

    @Override
    public ArrayList<Double> getFrecuenciasEsperadas() {
        ArrayList<Double> frecuenciasEsperadas = new ArrayList<>(Collections.nCopies(cantidadDeIntervalosK, 0.0));

        NormalDistribution normalDistribution = new NormalDistribution(media[0], desviacion[0]);
        for (int i = 0; i < cantidadDeIntervalosK; i++) {
            frecuenciasEsperadas.set(i, (normalDistribution.cumulativeProbability(limites.get(i)[1]) -
                    normalDistribution.cumulativeProbability((limites.get(i)[0]))) * rnd01.length);
        }

        return frecuenciasEsperadas;
    }


}
