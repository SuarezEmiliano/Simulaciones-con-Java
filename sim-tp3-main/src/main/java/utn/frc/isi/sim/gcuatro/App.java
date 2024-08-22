package utn.frc.isi.sim.gcuatro;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import utn.frc.isi.sim.gcuatro.Utils.BuscarIntervalo;

import java.util.*;
import java.util.stream.Collectors;

import static utn.frc.isi.sim.gcuatro.Excel.crearExcel;

public class App extends Application {

    // Constants
    static final int CANTIDAD_MINIMA_RESERVAS = 28;
    static final int PRIMER_VALOR_PROBABILIDADES_PASAJEROS = 31;

    // Inputs mainScene
    static Integer cantidadVuelos = null; // N
    static EnumReservas cantidadDeReservas = null;
    static Integer cantidadDeFilasAMostrarI = null;
    static Integer primerFilaTablaJ = null;
    static Double utilidad = 100.0;

    // Inputs probabilidadesScene
    String[][] probabilidadesPasajerosInputs = {
            {"0.10", "0.25", "0.50", "0.15"}, // 31
            {"0.05", "0.25", "0.50", "0.15", "0.05"}, // 32
            {"0.00", "0.05", "0.20", "0.45", "0.20", "0.10"}, // 33
            {"0.00", "0.05", "0.10", "0.40", "0.30", "0.10", "0.05"} // 34
    };
    Double[][] probabilidadesPasajeros = {
            {0.10, 0.25, 0.50, 0.15}, // 31
            {0.05, 0.25, 0.50, 0.15, 0.05}, // 32
            {0.00, 0.05, 0.20, 0.45, 0.20, 0.10}, // 33
            {0.00, 0.05, 0.10, 0.40, 0.30, 0.10, 0.05} // 34
    };

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) {
        showMainScene(stage);
    }

    public void showMainScene(Stage mainScenStage) {
        // Title
        mainScenStage.setTitle("Pasajes Aéreos");

        // continueButton
        Button continueButton = new Button();
        continueButton.setText("Continuar");
        continueButton.setDisable(true);
        continueButton.setDisable(!validateInputsMainScene(null, null, null, null, null));

        // X=N Cantidad de vuelos = Cantidad de filas generadas. Debe soportar hasta 100000.
        Label cantidadVuelosLabel = new Label("Ingrese la cantidad de vuelos entre 2 y 10^8:");
        TextField cantidadVuelosInput = new TextField();
        cantidadVuelosInput.setPromptText("Ej.: 1000");
        cantidadVuelosInput.setText(cantidadVuelos == null ? "" : Integer.toString(cantidadVuelos));

        // cantidadVuelos validación
        cantidadVuelosInput.textProperty().addListener((observable, oldValue, newValue) -> {
            continueButton.setDisable(!validateInputsMainScene(newValue, null, null, null, null));
        });

        // Reservas
        Label cantidadDeReservasLabel = new Label("Ingrese la cantidad de reservas: ");
        ComboBox<String> reservasDropdown = new ComboBox<>();
        reservasDropdown.getItems().addAll(Arrays.stream(EnumReservas.values())
                .map(EnumReservas::getValue)
                .map(String::valueOf)
                .collect(Collectors.toList()));
        reservasDropdown.setPromptText("Cantidad de reservas");
        if (cantidadDeReservas != null) {
            reservasDropdown.setValue(String.valueOf(cantidadDeReservas.getValue()));
        }

        // Reservas validación
        reservasDropdown.valueProperty().addListener((observable, oldValue, newValue) -> {
            continueButton.setDisable(!validateInputsMainScene(null, newValue, null, null, null));
        });

        // i input
        Label InputILabel = new Label("Ingrese la cantidad de iteraciones a mostrar (i):");
        TextField InputI = new TextField();
        InputI.setPromptText("Ej.: 100");
        InputI.setText(cantidadDeFilasAMostrarI == null ? "" : Integer.toString(cantidadDeFilasAMostrarI));

        // i Input validación
        InputI.textProperty().addListener((observable, oldValue, newValue) -> {
            continueButton.setDisable(!validateInputsMainScene(null, null, newValue, null, null));
        });

        // j input
        Label InputJLabel = new Label("Ingrese la fila a partir de la cual se mostrará la tabla (j):");
        TextField InputJ = new TextField();
        InputJ.setPromptText("Ej.: 100");
        InputJ.setText(primerFilaTablaJ == null ? "" : Integer.toString(primerFilaTablaJ));

        // j Input Validacion
        InputJ.textProperty().addListener((observable, oldValue, newValue) -> {
            continueButton.setDisable(!validateInputsMainScene(null, null, null, newValue, null));
        });

        // Utilidad
        Label utilidadLabel = new Label("Ingrese la utilidad por pasajero:");
        TextField utilidadInput = new TextField();
        utilidadInput.setPromptText("Ej.: 100");
        utilidadInput.setText(utilidad == null ? "" : Double.toString(utilidad));

        // Utilidad validacion
        utilidadInput.textProperty().addListener((observable, oldValue, newValue) -> {
            continueButton.setDisable(!validateInputsMainScene(null, null, null, null, newValue));
        });

        // Show window
        VBox vBoxTop = new VBox(cantidadVuelosLabel, cantidadVuelosInput,
                cantidadDeReservasLabel, reservasDropdown,
                InputILabel, InputI,
                InputJLabel, InputJ,
                utilidadLabel, utilidadInput);

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

        Scene mainScene = new Scene(borderPane, 640, 480);
        mainScenStage.setScene(mainScene);
        mainScenStage.show();

        continueButton.setOnAction(e -> {
            mainScenStage.close();
            mostrarPantallaProbabilidades();
        });
    }

    boolean validateInputsMainScene(
            String cantidadDeVuelosNewValue, String reservasNewValue, String iNewValue, String jNewValue,
            String utilidadNewValue) {
        try {
            if (cantidadDeVuelosNewValue != null) cantidadVuelos = Integer.parseInt(cantidadDeVuelosNewValue);
            if (reservasNewValue != null)
                cantidadDeReservas = EnumReservas.fromValue(Integer.parseInt(reservasNewValue));
            if (iNewValue != null) cantidadDeFilasAMostrarI = Integer.parseInt(iNewValue);
            if (jNewValue != null) primerFilaTablaJ = Integer.parseInt(jNewValue);
            if (utilidadNewValue != null) utilidad = Double.parseDouble(utilidadNewValue);
        } catch (NullPointerException | NumberFormatException e) {
            return false;
        }

        return cantidadVuelos != null && cantidadDeReservas != null && cantidadDeFilasAMostrarI != null &&
                primerFilaTablaJ != null && utilidad != null &&
                // Validaciones
                2 <= cantidadVuelos && cantidadVuelos <= Math.pow(10, 8) &&
                1 <= cantidadDeFilasAMostrarI && cantidadDeFilasAMostrarI <= cantidadVuelos - primerFilaTablaJ &&
                0 <= primerFilaTablaJ && primerFilaTablaJ <= cantidadVuelos - 1 &&
                0 < utilidad && utilidad <= 100000;
    }

    void mostrarPantallaProbabilidades() {
        Stage probabilidadesSceneStage = new Stage();
        probabilidadesSceneStage.setTitle("Pasajes Aéreos");

        final int cantidadDeInputsDeProbabilidad = cantidadDeReservas.getValue() - CANTIDAD_MINIMA_RESERVAS + 1;

        // generateButton
        Button generateButton = new Button();
        generateButton.setText("Generar");
        generateButton.setDisable(true);
        generateButton.setDisable(!validateInputsProbabilidades(null, null));

        // backButton
        Button backButton = new Button();
        backButton.setText("Atras");

        // label suma de probabilidades
        int filaMatrizProbabilidades = cantidadDeReservas.getValue() - PRIMER_VALOR_PROBABILIDADES_PASAJEROS;
        Label labelSumaDeProbabilidades = new Label(String.format("La suma de probabilidades es: %s",
                getSumaDeProbabilidades(filaMatrizProbabilidades)));

        actualizarLimitesInferioresDeBuscarValores(cantidadDeInputsDeProbabilidad, filaMatrizProbabilidades);

        VBox vBoxTop = new VBox();
        vBoxTop.setAlignment(Pos.TOP_CENTER);
        vBoxTop.setPadding(new Insets(10));
        vBoxTop.setSpacing(8);

        // probabilidades inputs
        Label[] labelsProbabilidades = new Label[cantidadDeInputsDeProbabilidad];
        TextField[] textFieldsProbabilidades = new TextField[cantidadDeInputsDeProbabilidad];
        for (int i = 0; i < cantidadDeInputsDeProbabilidad; i++) {
            labelsProbabilidades[i] = new Label(String.format("Ingrese la probabilidad asociada a %s pasajeros: ",
                    i + CANTIDAD_MINIMA_RESERVAS));
            textFieldsProbabilidades[i] = new TextField();
            textFieldsProbabilidades[i].setPromptText("Ej.: 0.1");

            // Add the label and text field to the VBox
            vBoxTop.getChildren().add(labelsProbabilidades[i]);
            vBoxTop.getChildren().add(textFieldsProbabilidades[i]);

            textFieldsProbabilidades[i].setText(Double.toString(
                    probabilidadesPasajeros[cantidadDeReservas.getValue() - PRIMER_VALOR_PROBABILIDADES_PASAJEROS][i]));

            // Validacion
            final int tempFinalI = i;
            textFieldsProbabilidades[i].textProperty().addListener((observable, oldValue, newValue) -> {
                generateButton.setDisable(!validateInputsProbabilidades(tempFinalI, newValue));
                labelSumaDeProbabilidades.setText(String.format("La suma de probabilidades es: %s",
                        getSumaDeProbabilidades(filaMatrizProbabilidades)));

                // Settear li buscar intervalo
                actualizarLimitesInferioresDeBuscarValores(cantidadDeInputsDeProbabilidad, filaMatrizProbabilidades);
            });
        }

        VBox vBoxSumaDeProbabilidades = new VBox(labelSumaDeProbabilidades);
        vBoxSumaDeProbabilidades.setAlignment(Pos.BOTTOM_LEFT);
        vBoxTop.getChildren().add(vBoxSumaDeProbabilidades);

        // Show window
        HBox vboxBottomRight = new HBox(backButton, generateButton);
        vboxBottomRight.setAlignment(Pos.BOTTOM_RIGHT);
        vboxBottomRight.setPadding(new Insets(10));
        vboxBottomRight.setSpacing(8);

        BorderPane borderPane = new BorderPane();
        borderPane.setTop(vBoxTop);
        borderPane.setBottom(vboxBottomRight);

        Scene probabilidadesScene = new Scene(borderPane, 640, 480);
        probabilidadesSceneStage.setScene(probabilidadesScene);
        probabilidadesSceneStage.show();

        backButton.setOnAction(e -> {
            probabilidadesSceneStage.close();
            showMainScene(new Stage());
        });

        generateButton.setOnAction(e -> {
//            probabilidadesSceneStage.close();
            double[][] matriz = VectorEstado.generadorVectoresParImpar(cantidadVuelos, cantidadDeReservas.getValue(),
                    cantidadDeFilasAMostrarI, primerFilaTablaJ, utilidad);
            crearExcel("tablaMontecarlo.xls", matriz);
        });
    }

    private void actualizarLimitesInferioresDeBuscarValores(int cantidadDeInputsDeProbabilidad, int filaMatrizProbabilidades) {
        BuscarIntervalo.cantidadMinimaReservas = CANTIDAD_MINIMA_RESERVAS;
        ArrayList<Double> limitesInferiores = new ArrayList<>(Collections.nCopies(cantidadDeInputsDeProbabilidad, 0.0));
        limitesInferiores.set(0, 0.0);
        for (int j = 1; j < cantidadDeInputsDeProbabilidad; j++) {
            limitesInferiores.set(j, limitesInferiores.get(j - 1) + probabilidadesPasajeros[filaMatrizProbabilidades][j - 1]);
        }
        BuscarIntervalo.limitesInferiores = limitesInferiores;
    }

    boolean validateInputsProbabilidades(Integer probabilidadIndex, String newValue) {
        int filaMatrizProbabilidades = cantidadDeReservas.getValue() - PRIMER_VALOR_PROBABILIDADES_PASAJEROS;

        if (probabilidadIndex != null) {
            probabilidadesPasajerosInputs[filaMatrizProbabilidades][probabilidadIndex] = newValue;
        }

        try {
//            if (probabilidadIndex != null) {
//                if (Objects.equals(newValue, "")) newValue = "0";
//                probabilidadesPasajeros[filaMatrizProbabilidades][probabilidadIndex] = Double.parseDouble(newValue);
//            }
            for (int i = 0; i < probabilidadesPasajerosInputs[filaMatrizProbabilidades].length; i++) {
                probabilidadesPasajeros[filaMatrizProbabilidades][i] =
                        Double.parseDouble(probabilidadesPasajerosInputs[filaMatrizProbabilidades][i]);
            }
        } catch (NullPointerException | NumberFormatException e) {
            return false;
        }

        boolean allValuesInRange = Arrays.stream(probabilidadesPasajeros[filaMatrizProbabilidades])
                .allMatch(value -> 0 <= value && value <= 1);

        return getSumaDeProbabilidades(filaMatrizProbabilidades) == 1 && allValuesInRange;
    }

    private double getSumaDeProbabilidades(int filaMatrizProbabilidades) {
        return Arrays.stream(probabilidadesPasajeros[filaMatrizProbabilidades])
                .mapToDouble(Double::doubleValue)
                .sum();
    }

    public static void mostrarPantallaResultado(double utilidadPromedioFinal, double utilidadingresada) {
        Stage rdoStage = new Stage();
        rdoStage.setTitle("Pasajes Aéreos");

        Label labelRdo = null;
        Double utilidadbase = 28 * utilidadingresada;

        if (utilidadPromedioFinal > utilidadbase) {
            labelRdo = new Label(String.format("Resultado: Se recomienda la sobreventa de pasajes, ya que la " +
                    "utilidad promedio en esta simulación es %s, la cual es mayor que la utilidad sin sobreventa " +
                    "de %s", utilidadPromedioFinal, utilidadbase));
        } else {
            labelRdo = new Label(String.format("Resultado: NO se recomienda la sobreventa de pasajes, ya que la " +
                    "utilidad promedio en esta simulación es %s, la cual es menor que la utilidad sin sobreventa " +
                    "de %s", utilidadPromedioFinal, utilidadbase));
        }

        // Show window
        VBox vBoxCenter = new VBox(labelRdo);
        vBoxCenter.setAlignment(Pos.CENTER);
        vBoxCenter.setPadding(new Insets(10));
        vBoxCenter.setSpacing(8);

        Scene mainScene = new Scene(vBoxCenter, 950, 100);
        rdoStage.setScene(mainScene);
        rdoStage.show();
    }
}