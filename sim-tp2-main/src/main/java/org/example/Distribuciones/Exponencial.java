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
import org.apache.commons.math3.distribution.ExponentialDistribution;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicBoolean;

public class Exponencial extends Distribucion {

    // input
    double[] lambda = new double[1];

    @Override
    public double[] generateRandomNumbersWithDistribution() {
        int N = rnd01.length;

        double[] exponentialNumbers = new double[N];
        for (int i = 0; i < N; i++) {
            double randomNumber = Math.log(1 - rnd01[i]) / (-lambda[0]);
            exponentialNumbers[i] = randomNumber;
        }

        return exponentialNumbers;

    }

    @Override
    public ArrayList<Double> getFrecuenciasEsperadas() {
        ArrayList<Double> frecuenciasEsperadas = new ArrayList<>(Collections.nCopies(cantidadDeIntervalosK, 0.0));

        ExponentialDistribution exponentialDistribution = new ExponentialDistribution(1 / lambda[0]);
        for (int i = 0; i < cantidadDeIntervalosK; i++) {
            frecuenciasEsperadas.set(i, (exponentialDistribution.cumulativeProbability(limites.get(i)[1]) -
                    exponentialDistribution.cumulativeProbability(limites.get(i)[0])) * rnd01.length);
        }

        return frecuenciasEsperadas;
    }

    //
    @Override
    public void distributionInput() {
        Stage stage = new Stage();
        stage.setTitle("Generador de Distribución Exponencial");

        // Verificar si el input es valido
        AtomicBoolean isValid = new AtomicBoolean(false);

        // generateButton
        Button generateButton = new Button("Generar");
        generateButton.setDisable(true);

        // inputLabel
        Label inputLabel = new Label("Ingrese λ:");

        // lambdaInput
        TextField lambdaInput = new TextField();
        lambdaInput.setPromptText("Lambda");

        // lambdaInputValidation
        lambdaInput.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                try {
                    lambda[0] = Double.parseDouble(newValue);
                    if (lambda[0] > 0) {
                        isValid.set(true);
                        generateButton.setDisable(false);

                    } else {
                        isValid.set(false);
                        generateButton.setDisable(true);
                    }
                } catch (NumberFormatException e) {
                    isValid.set(false);
                } finally {
                    generateButton.setDisable(!isValid.get());
                }
            }
        });

        // Show Window
        VBox vbox = new VBox(inputLabel, lambdaInput);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(10));
        vbox.setSpacing(8);

        VBox VboxBottom = new VBox(generateButton);
        VboxBottom.setAlignment(Pos.CENTER);
        VboxBottom.setPadding(new Insets(20));
        VboxBottom.setSpacing(10);

        BorderPane borderPane = new BorderPane();
        borderPane.setTop(vbox);
        borderPane.setBottom(VboxBottom);

        Scene exponentialInputScene = new Scene(borderPane, 600, 200);
        stage.setScene(exponentialInputScene);
        stage.show();

        generateButton.setOnAction(e -> {
            ((Stage) exponentialInputScene.getWindow()).close();
            pruebaChiCuadrado();
            showResults();
        });

    }

}
