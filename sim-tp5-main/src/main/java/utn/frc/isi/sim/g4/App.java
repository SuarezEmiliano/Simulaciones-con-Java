package utn.frc.isi.sim.g4;

import static utn.frc.isi.sim.g4.GeneradorExcel.crearExcel;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import utn.frc.isi.sim.g4.RungeKutta.GeneradorExcelRG;
import utn.frc.isi.sim.g4.RungeKutta.RungeKutta;

public class App extends Application {

    // Constants
    static final int CANTIDAD_MAXIMA_ITERACIONES = 100000;

    static final Double MEDIA_PETICION = 6.0;
    static final Double MAX_TIEMPO_DEVOLUCION = 2.5;
    static final Double MIN_TIEMPO_DEVOLUCION = 1.5;

    // Inputs
    static Double tiempoSimulacion = null; // X
    static Integer cantidadDeFilasAMostrarI = null; // i
    static Integer primerFilaTablaJ = null; // j
    static Double mediaLlegada = 4.0;
    static Double probabilidadPedir = 0.45;
    static Double probabilidadDevolver = 0.45;
    static Double probabilidadConsultar = 0.10;
    static Double minM = 2.0;
    static Double maxM = 36.0;
    static Double probabilidadSeRetira = 0.6;
    static Double probabilidadSeQueda = 1.0 - probabilidadSeRetira;
    static Double mediaLectura = 30.0;
    static Double primerFactorEcDif = 0.6;
    static Double segundoFactorEcDif = 0.7;
    static Double h = 0.1;

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) {
        // Title
        stage.setTitle("Biblioteca");

        // generateButton
        Button generateButton = new Button();
        generateButton.setText("Generar");
        generateButton.setDisable(true);
        generateButton.setDisable(!validateInputsMainScene(null, null, null, null, null, null, null, null, null, null, null, null, null, null));

        // tiempoSimulacion
        Label tiempoSimulacionLabel = new Label("Ingrese el tiempo de la simulacion en minutos (X): ");
        TextField tiempoSimulacionInput = new TextField();
        tiempoSimulacionInput.setPromptText("Ej.: 10");
        tiempoSimulacionInput.textProperty().addListener((observable, oldValue, newValue) -> {
            generateButton.setDisable(!validateInputsMainScene(newValue, null, null, null, null, null, null, null, null, null, null, null, null, null));
        });

        // cantidadDeFilasAMostrarI
        Label cantidadDeFilasAMostrarLabel = new Label("Ingrese la cantidad de filas a mostrar (i): ");
        TextField cantidadDeFilasAMostrarInput = new TextField();
        cantidadDeFilasAMostrarInput.setPromptText("Ej.: 10");
        cantidadDeFilasAMostrarInput.textProperty().addListener((observable, oldValue, newValue) -> {
            generateButton.setDisable(!validateInputsMainScene(null, newValue, null, null, null, null, null, null, null, null, null, null, null, null));
        });

        // primerFilaTablaJ
        Label primerFilaTablaJLabel = new Label("Ingrese la fila a partir de la cual se mostrará la tabla (j): ");
        TextField primerFilaTablaJInput = new TextField();
        primerFilaTablaJInput.setPromptText("Ej.: 10");
        primerFilaTablaJInput.textProperty().addListener((observable, oldValue, newValue) -> {
            generateButton.setDisable(!validateInputsMainScene(null, null, newValue, null, null, null, null, null, null, null, null, null, null, null));
        });
        
        // tiempoLlegada
        Label tiempoLlegadaLabel = new Label("Ingrese la media de llegadas de persona a la biblioteca: ");
        TextField tiempoLlegadaInput = new TextField();
        tiempoLlegadaInput.setPromptText("Ej.: 10");
        tiempoLlegadaInput.setText(mediaLlegada == null ? "" : Double.toString(mediaLlegada));
        tiempoLlegadaInput.textProperty().addListener((observable, oldValue, newValue) -> {
            generateButton.setDisable(!validateInputsMainScene(null, null, null, newValue, null, null, null, null, null, null, null, null, null, null));
        });

        // Probabilidades personas en el mostrador
        Label probabilidadesMostradorLabel = new Label("Ingrese las probabilidades para cada persona que se dirige al mostrador");

        // probabilidadPedir
        Label probabilidadPedirLabel = new Label("Probabilidad de que las personas pidan libros: ");
        TextField probabilidadPedirInput = new TextField();
        probabilidadPedirInput.setPromptText("Ej.: 0.45");
        probabilidadPedirInput.setText(probabilidadPedir == null ? "" : Double.toString(probabilidadPedir));
        probabilidadPedirInput.textProperty().addListener((observable, oldValue, newValue) -> {
            generateButton.setDisable(!validateInputsMainScene(null, null, null, null, newValue, null, null, null, null, null, null, null, null, null));
        });

        // probabilidadDevolver
        Label probabilidadDevolverLabel = new Label("Probabilidad de que las personas devuelvan libros: ");
        TextField probabilidadDevolverInput = new TextField();
        probabilidadDevolverInput.setPromptText("Ej.: 0.45");
        probabilidadDevolverInput.setText(probabilidadDevolver == null ? "" : Double.toString(probabilidadDevolver));
        probabilidadDevolverInput.textProperty().addListener((observable, oldValue, newValue) -> {
            generateButton.setDisable(!validateInputsMainScene(null, null, null, null, null, newValue, null, null, null, null, null, null, null, null));
        });

        // probabilidadConsultar
        Label probabilidadConsultarLabel = new Label("Probabilidad de que las personas consulten: ");
        TextField probabilidadConsultarInput = new TextField();
        probabilidadConsultarInput.setPromptText("Ej.: 0.10");
        probabilidadConsultarInput.setText(probabilidadConsultar == null ? "" : Double.toString(probabilidadConsultar));
        probabilidadConsultarInput.textProperty().addListener((observable, oldValue, newValue) -> {
            generateButton.setDisable(!validateInputsMainScene(null, null, null, null, null, null, newValue, null, null, null, null, null, null, null));
        });

        // demoras
        Label demorasLabel = new Label("Ingrese el intervalo de meticulosidad para las consultas: ");

        // minM
        Label minMLabel = new Label("Meticulosidad Minima: ");
        TextField minMInput = new TextField();
        minMInput.setPromptText("Ej.: 2");
        minMInput.setText(minM == null ? "" : Double.toString(minM));
        minMInput.textProperty().addListener((observable, oldValue, newValue) -> {
            generateButton.setDisable(!validateInputsMainScene(null, null, null, null, null, null, null, newValue, null, null, null, null, null, null));
        });

        // maxM
        Label maxMLabel = new Label("Meticulosidad Maxima: ");
        TextField maxMInput = new TextField();
        maxMInput.setPromptText("Ej.: 36");
        maxMInput.setText(maxM == null ? "" : Double.toString(maxM));
        maxMInput.textProperty().addListener((observable, oldValue, newValue) -> {
            generateButton.setDisable(!validateInputsMainScene(null, null, null, null, null, null, null, null, newValue, null, null, null, null, null));
        });

        // probabilidadSeRetira
        Label probabilidadSeRetiraLabel = new Label("De las personas que piden libros prestados, ingrese la probabilidad de que se retiren de la biblioteca: ");
        TextField probabilidadSeRetiraInput = new TextField();
        probabilidadSeRetiraInput.setPromptText("Ej.: 0.6");
        probabilidadSeRetiraInput.setText(probabilidadSeRetira == null ? "" : Double.toString(probabilidadSeRetira));
        probabilidadSeRetiraInput.textProperty().addListener((observable, oldValue, newValue) -> {
            generateButton.setDisable(!validateInputsMainScene(null, null, null, null, null, null, null, null, null, newValue, null, null, null, null));
        });

        // mediaLectura
        Label mediaLecturaLabel = new Label("Ingrese la media de tiempo de lectura en minutos: ");
        TextField mediaLecturaInput = new TextField();
        mediaLecturaInput.setPromptText("Ej.: 30");
        mediaLecturaInput.setText(mediaLectura == null ? "" : Double.toString(mediaLectura));
        mediaLecturaInput.textProperty().addListener((observable, oldValue, newValue) -> {
            generateButton.setDisable(!validateInputsMainScene(null, null, null, null, null, null, null, null, null, null, newValue, null, null, null));
        });

        // RUNGE-KUTTA
        // Primer Factor
        Label primerFactorEcDifLabel = new Label("Ingrese el primer factor de la ecuacion diferencial: ");
        TextField primerFactorEcDifInput = new TextField();
        primerFactorEcDifInput.setPromptText("Ej.: 0.6");
        primerFactorEcDifInput.setText(primerFactorEcDif == null ? "" : Double.toString(primerFactorEcDif));
        primerFactorEcDifInput.textProperty().addListener((observable, oldValue, newValue) -> {
            generateButton.setDisable(!validateInputsMainScene(null, null, null, null, null, null, null, null, null, null, null, newValue, null, null));
        });

        // Segundo Factor
        Label segundoFactorEcDifLabel = new Label("Ingrese el segundo factor de la ecuacion diferencial: ");
        TextField segundoFactorEcDifInput = new TextField();
        segundoFactorEcDifInput.setPromptText("Ej.: 0.7");
        segundoFactorEcDifInput.setText(segundoFactorEcDif == null ? "" : Double.toString(segundoFactorEcDif));
        segundoFactorEcDifInput.textProperty().addListener((observable, oldValue, newValue) -> {
            generateButton.setDisable(!validateInputsMainScene(null, null, null, null, null, null, null, null, null, null, null, null, newValue, null));
        });

        // Paso de calculo h
        Label hLabel = new Label("Ingrese el paso de calculo (h): ");
        TextField hInput = new TextField();
        hInput.setPromptText("Ej.: 0.1");
        hInput.setText(h == null ? "" : Double.toString(h));
        hInput.textProperty().addListener((observable, oldValue, newValue) -> {
            generateButton.setDisable(!validateInputsMainScene(null, null, null, null, null, null, null, null, null, null, null, null, null, newValue));
        });

        // Show window
        VBox vBoxTop = new VBox(
                tiempoSimulacionLabel, tiempoSimulacionInput,
                cantidadDeFilasAMostrarLabel, cantidadDeFilasAMostrarInput,
                primerFilaTablaJLabel, primerFilaTablaJInput,
                tiempoLlegadaLabel, tiempoLlegadaInput,
                probabilidadesMostradorLabel,
                probabilidadPedirLabel, probabilidadPedirInput,
                probabilidadDevolverLabel, probabilidadDevolverInput,
                probabilidadConsultarLabel, probabilidadConsultarInput,
                demorasLabel,
                minMLabel, minMInput,
                maxMLabel, maxMInput,
                probabilidadSeRetiraLabel, probabilidadSeRetiraInput,
                mediaLecturaLabel, mediaLecturaInput,
                primerFactorEcDifLabel, primerFactorEcDifInput,
                segundoFactorEcDifLabel, segundoFactorEcDifInput,
                hLabel, hInput
        );
        vBoxTop.setAlignment(Pos.CENTER);
        vBoxTop.setPadding(new Insets(10));
        vBoxTop.setSpacing(8);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(vBoxTop);
        scrollPane.setPrefSize(640, 550);
        scrollPane.setFitToWidth(true);

        VBox vboxBottom = new VBox(generateButton);
        vboxBottom.setAlignment(Pos.BOTTOM_RIGHT);
        vboxBottom.setPadding(new Insets(10));
        vboxBottom.setSpacing(8);

        BorderPane borderPane = new BorderPane();
        borderPane.setTop(scrollPane);
        borderPane.setBottom(vboxBottom);

        Scene mainScene = new Scene(borderPane, 640, 600);
        stage.setScene(mainScene);
        stage.show();

        generateButton.setOnAction(e -> {
            double[][] matrizRungeKutta = RungeKutta.generadorVectoresParImpar(h, segundoFactorEcDif, primerFactorEcDif, maxM);
            GeneradorExcelRG.crearExcel("Tabla de RungeKutta.xls", matrizRungeKutta);

            // crear matriz
            Object[][] matriz = VectorEstado.generadorVectoresParImpar(matrizRungeKutta);
            Object[][] matrizSinFilasNulas = VectorEstado.eliminarFilasNulasYDuplicadas(matriz);
            crearExcel("sistemadecolas.xls", matrizSinFilasNulas);

            // mostrar filas nulas eliminadas
            int cantidadFilasNulasEliminadas = matriz.length - matrizSinFilasNulas.length;
            if (cantidadFilasNulasEliminadas > 0) {
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Información");
                alert.setHeaderText(null);
                alert.setContentText("Las últimas " + cantidadFilasNulasEliminadas +
                        " iteraciones no se muestran ya que antes se llegó al límite de reloj ingresado.");
                alert.showAndWait();
            }

            Alert alertPromedioPermanencia = new Alert(AlertType.INFORMATION);
            alertPromedioPermanencia.setTitle("Promedio Permanencia");
            alertPromedioPermanencia.setHeaderText(null);
            alertPromedioPermanencia.setContentText("El promedio de permanencia de las personas en la biblioteca es " + matrizSinFilasNulas[matrizSinFilasNulas.length - 1][33] + "'");
            alertPromedioPermanencia.showAndWait();

            Alert alertPersonasNoEntran = new Alert(AlertType.INFORMATION);
            alertPersonasNoEntran.setTitle("Porcentaje Personas que no entran");
            alertPersonasNoEntran.setHeaderText(null);
            alertPersonasNoEntran.setContentText("El porcentaje de personas que llegan y encuentran la biblioteca cerrada por tener su capacidad completa es " + 
                    ((Double) matrizSinFilasNulas[matrizSinFilasNulas.length - 1][29] / (Double) matrizSinFilasNulas[matrizSinFilasNulas.length - 1][30] * 100) + "%");
            alertPersonasNoEntran.showAndWait();
        });
    }

    boolean validateInputsMainScene(
            String tiempoSimulacionNewValue, String cantidadDeFilasAMostrarNewValue,
            String primerFilaTablaJNewValue, String mediaLlegadaNewValue,
            String probabilidadPedirNewValue, String probabilidadDevolverNewValue,
            String probabilidadConsultarNewValue, String minMNewValue, String maxMNewValue,
            String probabilidadSeRetiraNewValue, String mediaLecturaNewValue,
            String primerFactorEcDifNewValue, String segundoFactorEcDifNewValue, String hNewValue
    ) {
        try {
            if (tiempoSimulacionNewValue != null)
                tiempoSimulacion = Double.parseDouble(tiempoSimulacionNewValue);
            if (cantidadDeFilasAMostrarNewValue != null)
                cantidadDeFilasAMostrarI = Integer.parseInt(cantidadDeFilasAMostrarNewValue);
            if (mediaLlegadaNewValue != null)
                mediaLlegada = Double.parseDouble(mediaLlegadaNewValue);
            if (primerFilaTablaJNewValue != null)
                primerFilaTablaJ = Integer.parseInt(primerFilaTablaJNewValue);
            if (probabilidadPedirNewValue != null)
                probabilidadPedir = Double.parseDouble(probabilidadPedirNewValue);
            if (probabilidadDevolverNewValue != null)
                probabilidadDevolver = Double.parseDouble(probabilidadDevolverNewValue);
            if (probabilidadConsultarNewValue != null)
                probabilidadConsultar = Double.parseDouble(probabilidadConsultarNewValue);

            if (minMNewValue != null)
                minM = Double.parseDouble(minMNewValue);
            if (maxMNewValue != null)
                maxM = Double.parseDouble(maxMNewValue);

            if (probabilidadSeRetiraNewValue != null) {
                probabilidadSeRetira = Double.parseDouble(probabilidadSeRetiraNewValue);
                probabilidadSeQueda = 1 - probabilidadSeRetira;
            }
            if (mediaLecturaNewValue != null)
                mediaLectura = Double.parseDouble(mediaLecturaNewValue);

            if (primerFactorEcDifNewValue != null)
                primerFactorEcDif = Double.parseDouble(primerFactorEcDifNewValue);
            if (segundoFactorEcDifNewValue != null)
                segundoFactorEcDif = Double.parseDouble(segundoFactorEcDifNewValue);
            if (hNewValue != null)
                h = Double.parseDouble(hNewValue);

        } catch (NullPointerException | NumberFormatException e) {
            return false;
        }

        return tiempoSimulacion != null && cantidadDeFilasAMostrarI != null && primerFilaTablaJ != null &&
                probabilidadPedir != null && probabilidadDevolver != null && probabilidadConsultar != null &&
                minM != null && maxM != null && probabilidadSeRetira != null &&
                mediaLectura != null && primerFactorEcDif != null && segundoFactorEcDif != null && h != null &&
                // Validaciones
                0 < tiempoSimulacion &&
                1 <= cantidadDeFilasAMostrarI && cantidadDeFilasAMostrarI <= CANTIDAD_MAXIMA_ITERACIONES - primerFilaTablaJ &&
                0 <= primerFilaTablaJ && primerFilaTablaJ <= CANTIDAD_MAXIMA_ITERACIONES - 1 &&
                0 < mediaLlegada &&
                0 <= probabilidadPedir && probabilidadPedir <= 1 &&
                0 <= probabilidadDevolver && probabilidadDevolver <= 1 &&
                0 <= probabilidadConsultar && probabilidadConsultar <= 1 &&
                probabilidadPedir + probabilidadDevolver + probabilidadConsultar == 1 &&
                0 < minM && 0 < maxM && minM < maxM &&
                0 <= probabilidadSeRetira && probabilidadSeRetira <= 1 &&
                0 < mediaLectura &&
                0 < h
                ;
    }
}