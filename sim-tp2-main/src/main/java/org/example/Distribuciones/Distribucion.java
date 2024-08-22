package org.example.Distribuciones;

import com.opencsv.CSVWriter;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.example.Graphics.Histogram;
import org.example.Utils.ListUtils;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;

@Data
@FieldDefaults(level = AccessLevel.PROTECTED)
public abstract class Distribucion {

    static final String csvFilePath = "./GeneratedNumbers/numerosgenerados.csv";

    int cantidadDeIntervalosK;
    double[] rnd01;
    double[] rndDistribution;

    int N;
    double min;
    double max;
    double rango;
    double amplitud;
    double media;
    double varianza;
    ArrayList<Double[]> limites;
    ArrayList<Integer> frecuenciasObservadas;
    ArrayList<Double> frecuenciasEsperadas;
    ArrayList<Double> chiCuadradoCalculado;

    public abstract void distributionInput();
    abstract double[] generateRandomNumbersWithDistribution();
    abstract ArrayList<Double> getFrecuenciasEsperadas();

    void pruebaChiCuadrado() {
        rndDistribution = generateRandomNumbersWithDistribution();

        // N
        N = rndDistribution.length;
        // MINIMO
        min = Arrays.stream(rndDistribution).min().orElseThrow(() ->
                new RuntimeException("No se pudo obtener el minimo"));
        // MAXIMO
        max = Arrays.stream(rndDistribution).max().orElseThrow(() ->
                new RuntimeException("No se pudo obtener el maximo"));
        // RANGO
        rango = max - min;
        // AMPLITUD
        amplitud = rango / (double) cantidadDeIntervalosK;
        // MEDIA
        media = Arrays.stream(rndDistribution).average().orElseThrow(() ->
                new RuntimeException("No se pudo calcular la media"));
        // VARIANZA
        varianza = Arrays.stream(rndDistribution)
                .map(x -> Math.pow(x - media, 2))
                .sum() / (double) N;

        // INTERVALOS
        limites = new ArrayList<>();
        for (int i = 0; i < cantidadDeIntervalosK; i++) {
            Double[] interval = new Double[2];
            interval[0] = min + (i * amplitud);
            interval[1] = min + ((i + 1) * amplitud);
            limites.add(interval);
        }

        // FRECUENCIA OBSERVADA
        // Inicializa el array de la frecuencia observada
        frecuenciasObservadas = new ArrayList<>(Collections.nCopies(cantidadDeIntervalosK, 0));
        // Recorremos cada uno de los valores aleatorios con distribución uniforme
        for (double dataValue : rndDistribution) {
            for (int i = 0; i < cantidadDeIntervalosK; i++) {
                // Chequear si el número cae dentro del intervalo
                if (dataValue >= limites.get(i)[0] && dataValue < limites.get(i)[1]) {
                    // Incrementar la frecuencia en ese intervalo
                    frecuenciasObservadas.set(i, frecuenciasObservadas.get(i) + 1);
                    // Una vez que el intervalo es encontrado, pasa al siguiente valor de rndDistribution
                    break;
                } else if (i == cantidadDeIntervalosK - 1) {
                    // Si el valor es igual al último intervalo, se incrementa la frecuencia en el último intervalo
                    frecuenciasObservadas.set(i, frecuenciasObservadas.get(i) + 1);
                }
            }
        }

        // FRECUENCIA ESPERADA
        frecuenciasEsperadas = getFrecuenciasEsperadas();

        // CHI CUADRADO CALCULADO
        chiCuadradoCalculado = new ArrayList<>(Collections.nCopies(cantidadDeIntervalosK, 0.0));
        for (int i = 0; i < cantidadDeIntervalosK; i++) {
            chiCuadradoCalculado.set(i, Math.pow(frecuenciasObservadas.get(i) - frecuenciasEsperadas.get(i), 2) /
                    frecuenciasEsperadas.get(i));
        }

        // Acumular cuando las frecuencias esperadas sean menor que 5
        // Recorrer todas las FE
        filaActualLoop: for (int filaActual = 0; filaActual < cantidadDeIntervalosK; filaActual++) {
            if (frecuenciasEsperadas.get(filaActual) < 5) {
                // Cuando encontramos una FE menor a 5, verificamos hasta qué indice hay que acumular
                int ultimaFilaASumar = filaActual;
                double sumaFrecuenciasEsperadas = 0;
                ultimaFilaLoop: while (sumaFrecuenciasEsperadas < 5) {
                    if (ultimaFilaASumar < cantidadDeIntervalosK) {
                        sumaFrecuenciasEsperadas += frecuenciasEsperadas.get(ultimaFilaASumar);
                        ultimaFilaASumar++;
                    } else {
                        // Se alcanzó el último intervalo y la suma no dio mayor a 5
                        if (filaActual != 0) {
                            // Se debe acumular con el intervalo anterior, el cual ya es >= 5
                            filaActual -= 1;
                            break ultimaFilaLoop;
                        } else {
                            // No alcanzaron todas las filas para llegar a 5, no se acumulan las filas
                            break filaActualLoop;
                        }
                    }
                }
                ultimaFilaASumar -= 1;

                // Actualizar K
                cantidadDeIntervalosK -= ultimaFilaASumar - filaActual;
                // Actualizar Limites
                limites.get(filaActual)[1] = limites.get(ultimaFilaASumar)[1];
                // Acumular FO, FE y Chi Cuadrado Calculado
                frecuenciasObservadas.set(filaActual, ListUtils.sumIntegerListFromIToJ(frecuenciasObservadas, filaActual, ultimaFilaASumar));
                frecuenciasEsperadas.set(filaActual, ListUtils.sumDoubleListFromIToJ(frecuenciasEsperadas, filaActual, ultimaFilaASumar));
                chiCuadradoCalculado.set(filaActual, ListUtils.sumDoubleListFromIToJ(chiCuadradoCalculado, filaActual, ultimaFilaASumar));

                // Eliminar las filas desde la actual mas 1 hasta la ultima fila a sumar
                limites.subList(filaActual + 1, ultimaFilaASumar + 1).clear();
                frecuenciasObservadas.subList(filaActual + 1, ultimaFilaASumar + 1).clear();
                frecuenciasEsperadas.subList(filaActual + 1, ultimaFilaASumar + 1).clear();
                chiCuadradoCalculado.subList(filaActual + 1, ultimaFilaASumar + 1).clear();
            }
        }


    }

    void showResults() {
        generateCsv();

        Label muestraLabel = new Label("Tamaño de la muestra: " + N);
        muestraLabel.setPadding(new Insets(0, 0, 10, 0));
        Label intervalosLabel = new Label("Cantidad de Intervalos Seleccionados: " + cantidadDeIntervalosK);
        intervalosLabel.setPadding(new Insets(0, 0, 10, 0));
        StringBuilder intervalosStr = new StringBuilder();
        for (Double[] intervalo : limites) {
            intervalosStr.append(Arrays.toString(intervalo)).append(" ");
        }

        VBox vbox = new VBox();
        vbox.setAlignment(Pos.CENTER_LEFT); // Alineación a la izquierda
        vbox.setPadding(new Insets(20));

        // Crear el botón
        Button histogramButton = new Button("Ver Histograma");
        histogramButton.setOnAction(e -> {
            // Llama a la función showHistogram cuando se presiona el botón
            Histogram.displayHistogram(limites, frecuenciasObservadas);
        });

        // Agregar el botón al VBox
        vbox.getChildren().add(histogramButton);


        Label minLabel = new Label("Mínimo: " + String.format("%.4f", min));
        minLabel.setPadding(new Insets(0, 0, 10, 0));
        Label maxLabel = new Label("Máximo: " + String.format("%.4f", max));
        maxLabel.setPadding(new Insets(0, 0, 10, 0));
        Label rangoLabel = new Label("Rango: " + String.format("%.4f", rango));
        rangoLabel.setPadding(new Insets(0, 0, 10, 0));
        Label amplitudLabel = new Label("Amplitud: " + String.format("%.4f", amplitud));
        amplitudLabel.setPadding(new Insets(0, 0, 10, 0));
        Label mediaLabel = new Label("Media: " + String.format("%.4f", media));
        mediaLabel.setPadding(new Insets(0, 0, 10, 0));
        Label varianzaLabel = new Label("Varianza: " + String.format("%.4f", varianza));
        varianzaLabel.setPadding(new Insets(0, 0, 10, 0));
        Label frecuenciaObservadaLabel = new Label("Frecuencia Observada: " + frecuenciasObservadas);
        frecuenciaObservadaLabel.setPadding(new Insets(0, 0, 10, 0));

        // Formatear la frecuencia esperada con cuatro decimales
        StringBuilder frecuenciaEsperadaStr = new StringBuilder();
        for (Double frecuenciaEsperada : frecuenciasEsperadas) {
            frecuenciaEsperadaStr.append(String.format("%.4f", frecuenciaEsperada)).append(" ");
        }
        Label frecuenciaEsperadaLabel = new Label("Frecuencia Esperada: " + frecuenciaEsperadaStr.toString());
        frecuenciaEsperadaLabel.setPadding(new Insets(0, 0, 10, 0));

        Label chiCuadradoLabel = new Label("Chi Cuadrado: " + String.format("%.4f", ListUtils.sumList(chiCuadradoCalculado)));
        chiCuadradoLabel.setPadding(new Insets(0, 0, 10, 0));

        // Limpiar el VBox antes de agregar nuevas etiquetas
        vbox.getChildren().clear();
        // Agregar etiquetas al VBox existente
        vbox.getChildren().addAll(
                muestraLabel, intervalosLabel, minLabel, maxLabel, rangoLabel,
                amplitudLabel, mediaLabel, varianzaLabel, frecuenciaObservadaLabel,
                frecuenciaEsperadaLabel, chiCuadradoLabel, histogramButton);

        // Establecer el título de la ventana
        Stage stage = new Stage();
        stage.setTitle("Resultados del Análisis");
        Scene dataScene = new Scene(vbox, 600, 500);
        stage.setScene(dataScene);
        stage.show();
    }

    private void generateCsv() {
        // Escribe los números aleatorios generados en un archivo CSV
        try (CSVWriter writer = new CSVWriter(new FileWriter(csvFilePath))) {
            writer.writeNext(new String[] {"Random (0;1)", "Random Distribucion"});

            for (int i = 0; i < rnd01.length; i++) {
                String formattedRnd01 = String.format("%.4f", rnd01[i]);
                String formattedRndDistribution = String.format("%.4f", rndDistribution[i]);
                writer.writeNext(new String[] { formattedRnd01, formattedRndDistribution } );
            }
            System.out.println("Números aleatorios generados y guardados en el archivo CSV: " + csvFilePath);
        } catch (IOException e) {
            System.out.println("Error al escribir en el archivo CSV: " + e.getMessage());
        }
    }

}
