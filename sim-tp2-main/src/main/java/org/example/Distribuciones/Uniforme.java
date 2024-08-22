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
import org.example.Utils.BooleanUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class Uniforme extends Distribucion {

    // inputs
    double[] a = new double[1];
    double[] b = new double[1];

    @Override
    public double[] generateRandomNumbersWithDistribution() {
        int N = rnd01.length;

        double[] uniformNumbers = new double[N];
        for (int i = 0; i < N; i++) {
            double randomNumber = a[0] + rnd01[i] * (b[0] - a[0]);
            uniformNumbers[i] = randomNumber;
        }

        return uniformNumbers;
    }

    @Override
    public ArrayList<Double> getFrecuenciasEsperadas() {
        double value = (double) rnd01.length / (double) cantidadDeIntervalosK;

        return new ArrayList<>(Collections.nCopies(cantidadDeIntervalosK, value));
    }

    @Override
    public void distributionInput() {
        Stage stage = new Stage();
        stage.setTitle("Generador de Distribución Uniforme");

        // Verificar para cada input si es valido
        boolean[] areInputsValid = new boolean[3];

        // generateButton
        Button generateButton = new Button("Generar");
        generateButton.setDisable(true);

        // rangeLabel
        Label rangeLabel = new Label("Ingrese el rango (a, b):");

        // aInput
        TextField aInput = new TextField();
        aInput.setPromptText("a");

        // aInputValidation
        aInput.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                try {
                    a[0] = Double.parseDouble(newValue);
                    areInputsValid[0] = -1000000 <= a[0] && a[0] <= 1000000;
                    areInputsValid[2] = a[0] < b[0];
                } catch (NumberFormatException e) {
                    areInputsValid[0] = false;
                } finally {
                    generateButton.setDisable(BooleanUtils.isAnyValueOfArrayFalse(areInputsValid));
                }
            }
        });

        // bInput
        TextField bInput = new TextField();
        bInput.setPromptText("b");

        // bInputValidation
        bInput.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                try {
                    b[0] = Double.parseDouble(newValue);
                    areInputsValid[1] = -1000000 <= b[0] && b[0] <= 1000000;
                    areInputsValid[2] = a[0] < b[0];
                } catch (NumberFormatException e) {
                    areInputsValid[1] = false;
                } finally {
                    generateButton.setDisable(BooleanUtils.isAnyValueOfArrayFalse(areInputsValid));
                }
            }
        });

        // Show window
        VBox vBoxTop = new VBox(rangeLabel, aInput, bInput);
        vBoxTop.setAlignment(Pos.CENTER);
        vBoxTop.setPadding(new Insets(10));
        vBoxTop.setSpacing(8);

        VBox vboxBottom = new VBox(generateButton);
        vboxBottom.setAlignment(Pos.BOTTOM_RIGHT);
        vboxBottom.setPadding(new Insets(10));
        vboxBottom.setSpacing(8);

        BorderPane borderPane = new BorderPane();
        borderPane.setTop(vBoxTop);
        borderPane.setBottom(vboxBottom);

        Scene uniformeInputScene = new Scene(borderPane, 600, 200);
        stage.setScene(uniformeInputScene);
        stage.show();

        generateButton.setOnAction(e -> {
            ((Stage) uniformeInputScene.getWindow()).close();
            pruebaChiCuadrado();
            showResults();
        });
    }


}