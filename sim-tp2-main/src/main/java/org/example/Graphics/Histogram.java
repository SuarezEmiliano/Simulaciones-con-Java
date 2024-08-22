package org.example.Graphics;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;

public class Histogram {

    // Creamos el Histograma y lo Visualizamos
    public static void displayHistogram(ArrayList<Double[]> intervalos, ArrayList<Integer> frecuenciasObservadas) {
       // Se crea el Histgrama tomando como entrada todos los intervalos y la cantidad de valores que cae en cada intervalo
        BarChart<String, Number> histogram = createHistogram(intervalos, frecuenciasObservadas);

        // Configuración de la Interfaz
        VBox vBox = new VBox();
        vBox.getChildren().addAll(histogram);
        Scene scene = new Scene(vBox, 800, 400);

        // Creación de la ventana
        Stage stage = new Stage();
        stage.setTitle("Histograma");
        stage.setScene(scene);

        // Asegura que el método show() del objeto "stage" se ejecute
        Platform.runLater(stage::show);
    }

    // Método para crear el Histograma
    public static BarChart<String, Number> createHistogram(ArrayList<Double[]> intervalos, ArrayList<Integer> frecuenciasObservadas) {

        // Configuración de los ejes con los datos que se obtienen como parámetros
        final CategoryAxis ejeXIntervalos = new CategoryAxis();
        final NumberAxis ejeYFrecuenciasObservadas = new NumberAxis();

        final BarChart<String, Number> barChart = new BarChart<>(ejeXIntervalos, ejeYFrecuenciasObservadas);
        barChart.setCategoryGap(0);
        barChart.setBarGap(0);

        ejeXIntervalos.setLabel("Intérvalos");
        ejeYFrecuenciasObservadas.setLabel("Frecuencias Observadas");

        // Agregar título al histograma
        barChart.setTitle("Histograma Frecuencias Observadas");

        //Creación una serie de datos vacía que llenaremos a continuación
        XYChart.Series series = new XYChart.Series();
        series.setName("Histograma de Frecuencias Observadas");

        for (int i = 0; i < intervalos.size(); i++) {
            //Se da un formato con 4 decimales a los intervalos
            String lowerLimit = String.format("%.4f", intervalos.get(i)[0]);
            String upperLimit = String.format("%.4f", intervalos.get(i)[1]);
            // Calcula el número de espacios en blanco necesarios para alinear el límite superior a la derecha
            int numSpaces = 20 - lowerLimit.length() - upperLimit.length();
            String intervalLabel = lowerLimit + " ".repeat(numSpaces) + upperLimit;
            //Agrega una valores a la serie de datos creada
            series.getData().add(new XYChart.Data<>(intervalLabel, frecuenciasObservadas.get(i)));
        }

        // Agrega la serie de datos al histograma
        barChart.getData().addAll(series);

        return barChart;
    }
}