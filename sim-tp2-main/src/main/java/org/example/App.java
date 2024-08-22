package org.example;

import com.opencsv.CSVWriter;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.Distribuciones.*;
import org.example.Utils.BooleanUtils;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;
import java.util.stream.Collectors;

public class App extends Application {


    // inputs
    static int[] sampleSize = new int[1];
    static EnumDistribution[] selectedDistribution = new EnumDistribution[1];
    static EnumIntervals[] selectedIntervals = new EnumIntervals[1];

    public static void main(String[] args) {
        launch();
    }

    /**
     * Este metodo es llamado por la clase Application para iniciar la aplicación
     * Genera una ventana con un campo de texto para ingresar el tamaño de la muestra y
     * un botón para generar los números aleatorios.
     * @param stage La ventana principal de la aplicación
     */
    @Override
    public void start(Stage stage) {
        stage.setTitle("Generador de Distribuciones RND");

        // Verificar para cada input si es valido
        boolean[] areInputsValid = new boolean[3];

        // sampleSizeInput
        Label sampleSizeLabel = new Label("Ingrese el tamaño de la muestra n entre 1 y 1.000.000:");
        TextField sampleSizeInput = new TextField();
        sampleSizeInput.setPromptText("Ej.: 1000");

        // continueButton
        Button continueButton = new Button();
        continueButton.setText("Continuar");
        continueButton.setDisable(true);

        // sampleSizeInput validacion
        sampleSizeInput.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                try {
                    sampleSize[0] = Integer.parseInt(newValue);
                    areInputsValid[0] = 1 <= sampleSize[0] && sampleSize[0] <= 1000000;
                } catch (NumberFormatException e) {
                    areInputsValid[0] = false;
                } finally {
                    continueButton.setDisable(BooleanUtils.isAnyValueOfArrayFalse(areInputsValid));
                }
            }
        });

        // distributionDropdown
        ComboBox<String> distributionDropdown = new ComboBox<>();
        distributionDropdown.getItems().addAll(
                Arrays.stream(EnumDistribution.values())
                .map(EnumDistribution::getName)
                .collect(Collectors.toList()));
        distributionDropdown.setPromptText("Seleccione una distribución");

        // distributionDropdown validacion
        distributionDropdown.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                try {
                    selectedDistribution[0] = EnumDistribution.valueOf(newValue.toUpperCase());
                    areInputsValid[1] = true;
                } catch (IllegalArgumentException e) {
                    System.out.println(e.getMessage());
                    areInputsValid[1] = false;
                } finally {
                    continueButton.setDisable(BooleanUtils.isAnyValueOfArrayFalse(areInputsValid));
                }
            }
        });

        // intervalos
        ComboBox<String> intervalosDropdown = new ComboBox<>();
        intervalosDropdown.getItems().addAll(Arrays.stream(EnumIntervals.values())
                .map(EnumIntervals::getValue)
                .map(String::valueOf)
                .collect(Collectors.toList()));
        intervalosDropdown.setPromptText("Seleccione un intervalo");

        // intervalos validacion
        intervalosDropdown.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                try {
                    selectedIntervals[0] = EnumIntervals.fromValue(Integer.parseInt(newValue));
                    areInputsValid[2] = true;
                } catch (IllegalArgumentException e) {
                    areInputsValid[2] = false;
                } finally {
                    continueButton.setDisable(BooleanUtils.isAnyValueOfArrayFalse(areInputsValid));
                }
            }
        });

        // Show window
        VBox vBoxTop = new VBox(sampleSizeLabel, sampleSizeInput, distributionDropdown, intervalosDropdown);
        vBoxTop.setAlignment(Pos.CENTER);
        vBoxTop.setPadding(new Insets(10));
        vBoxTop.setSpacing(8);

        VBox vboxBottom = new VBox(continueButton);
        vboxBottom.setAlignment(Pos.BOTTOM_RIGHT);
        vboxBottom.setPadding(new Insets(10));
        vboxBottom.setSpacing(8);

        BorderPane borderPane = new BorderPane();
        borderPane.setTop(vBoxTop);
        borderPane.setBottom(vboxBottom);

        Scene inputScene = new Scene(borderPane, 600, 200);
        stage.setScene(inputScene);
        stage.show();

        continueButton.setOnAction(e -> {
            ((Stage) inputScene.getWindow()).close();
            generateDistribution();
        });
    }

    /**
     * Genera números aleatorios y los guarda en un archivo CSV
     */
    public static void generateDistribution() {
        // Generar RND en el intervalo [0;1)
        Random random = new Random();
        long seed = random.nextLong();
        random.setSeed(seed);
        System.out.println("Seed: " + seed);

        double[] rnd01 = new double[sampleSize[0]];
        for (int i = 0; i < sampleSize[0]; i++) {
            // Genera un número aleatorio ente 0 y 1
            double randomNumber = random.nextDouble();
            rnd01[i] = randomNumber;
        }

        // Verificar la distribución seleccionada
        EnumDistribution selected = selectedDistribution[0];
        Distribucion distribucion = null;
        switch (selected) {
            case EXPONENCIAL:
                distribucion = new Exponencial();
                break;
            case UNIFORME:
                distribucion = new Uniforme();
                break;
            case NORMAL:
                distribucion = new Normal();
                break;
            default:
                System.out.println("Distribución no soportada");
                return;
        }
        distribucion.setRnd01(rnd01);
        distribucion.setCantidadDeIntervalosK(selectedIntervals[0].getValue());

        distribucion.distributionInput();
    }

}
