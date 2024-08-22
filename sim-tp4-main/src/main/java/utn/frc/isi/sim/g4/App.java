package utn.frc.isi.sim.g4;

import static utn.frc.isi.sim.g4.GeneradorExcel.crearExcel;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

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
    static Double minDemoraConsulta = 2.0;
    static Double maxDemoraConsulta = 5.0;
    static Double probabilidadSeRetira = 0.6;
    static Double probabilidadSeQueda = 1.0 - probabilidadSeRetira;
    static Double mediaLectura = 30.0;

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
        generateButton.setDisable(!validateInputsMainScene(null, null, null, null, null, null, null, null, null, null, null));

        // tiempoSimulacion
        Label tiempoSimulacionLabel = new Label("Ingrese el tiempo de la simulacion en minutos (X): ");
        TextField tiempoSimulacionInput = new TextField();
        tiempoSimulacionInput.setPromptText("Ej.: 10");
        tiempoSimulacionInput.textProperty().addListener((observable, oldValue, newValue) -> {
            generateButton.setDisable(!validateInputsMainScene(newValue, null, null, null, null, null, null, null, null, null, null));
        });

        // cantidadDeFilasAMostrarI
        Label cantidadDeFilasAMostrarLabel = new Label("Ingrese la cantidad de filas a mostrar (i): ");
        TextField cantidadDeFilasAMostrarInput = new TextField();
        cantidadDeFilasAMostrarInput.setPromptText("Ej.: 10");
        cantidadDeFilasAMostrarInput.textProperty().addListener((observable, oldValue, newValue) -> {
            generateButton.setDisable(!validateInputsMainScene(null, newValue, null, null, null, null, null, null, null, null, null));
        });

        // primerFilaTablaJ
        Label primerFilaTablaJLabel = new Label("Ingrese la fila a partir de la cual se mostrará la tabla (j): ");
        TextField primerFilaTablaJInput = new TextField();
        primerFilaTablaJInput.setPromptText("Ej.: 10");
        primerFilaTablaJInput.textProperty().addListener((observable, oldValue, newValue) -> {
            generateButton.setDisable(!validateInputsMainScene(null, null, newValue, null, null, null, null, null, null, null, null));
        });
        
        // tiempoLlegada
        Label tiempoLlegadaLabel = new Label("Ingrese la media de llegadas de persona a la biblioteca: ");
        TextField tiempoLlegadaInput = new TextField();
        tiempoLlegadaInput.setPromptText("Ej.: 10");
        tiempoLlegadaInput.setText(mediaLlegada == null ? "" : Double.toString(mediaLlegada));
        tiempoLlegadaInput.textProperty().addListener((observable, oldValue, newValue) -> {
            generateButton.setDisable(!validateInputsMainScene(null, null, null, newValue, null, null, null, null, null, null, null));
        });

        // Probabilidades personas en el mostrador
        Label probabilidadesMostradorLabel = new Label("Ingrese las probabilidades para cada persona que se dirige al mostrador");

        // probabilidadPedir
        Label probabilidadPedirLabel = new Label("Probabilidad de que las personas pidan libros: ");
        TextField probabilidadPedirInput = new TextField();
        probabilidadPedirInput.setPromptText("Ej.: 0.45");
        probabilidadPedirInput.setText(probabilidadPedir == null ? "" : Double.toString(probabilidadPedir));
        probabilidadPedirInput.textProperty().addListener((observable, oldValue, newValue) -> {
            generateButton.setDisable(!validateInputsMainScene(null, null, null, null, newValue, null, null, null, null, null, null));
        });

        // probabilidadDevolver
        Label probabilidadDevolverLabel = new Label("Probabilidad de que las personas devuelvan libros: ");
        TextField probabilidadDevolverInput = new TextField();
        probabilidadDevolverInput.setPromptText("Ej.: 0.45");
        probabilidadDevolverInput.setText(probabilidadDevolver == null ? "" : Double.toString(probabilidadDevolver));
        probabilidadDevolverInput.textProperty().addListener((observable, oldValue, newValue) -> {
            generateButton.setDisable(!validateInputsMainScene(null, null, null, null, null, newValue, null, null, null, null, null));
        });

        // probabilidadConsultar
        Label probabilidadConsultarLabel = new Label("Probabilidad de que las personas consulten: ");
        TextField probabilidadConsultarInput = new TextField();
        probabilidadConsultarInput.setPromptText("Ej.: 0.10");
        probabilidadConsultarInput.setText(probabilidadConsultar == null ? "" : Double.toString(probabilidadConsultar));
        probabilidadConsultarInput.textProperty().addListener((observable, oldValue, newValue) -> {
            generateButton.setDisable(!validateInputsMainScene(null, null, null, null, null, null, newValue, null, null, null, null));
        });

        // demoras
        Label demorasLabel = new Label("Ingrese el intervalo de tiempo de demoras de consultas: ");

        // minDemoraConsulta
        Label minDemoraConsultaLabel = new Label("Demora minima de consulta: ");
        TextField minDemoraConsultaInput = new TextField();
        minDemoraConsultaInput.setPromptText("Ej.: 2");
        minDemoraConsultaInput.setText(minDemoraConsulta == null ? "" : Double.toString(minDemoraConsulta));
        minDemoraConsultaInput.textProperty().addListener((observable, oldValue, newValue) -> {
            generateButton.setDisable(!validateInputsMainScene(null, null, null, null, null, null, null, newValue, null, null, null));
        });

        // maxDemoraConsulta
        Label maxDemoraConsultaLabel = new Label("Demora maxima de consulta: ");
        TextField maxDemoraConsultaInput = new TextField();
        maxDemoraConsultaInput.setPromptText("Ej.: 5");
        maxDemoraConsultaInput.setText(maxDemoraConsulta == null ? "" : Double.toString(maxDemoraConsulta));
        maxDemoraConsultaInput.textProperty().addListener((observable, oldValue, newValue) -> {
            generateButton.setDisable(!validateInputsMainScene(null, null, null, null, null, null, null, null, newValue, null, null));
        });

        // probabilidadSeRetira
        Label probabilidadSeRetiraLabel = new Label("De las personas que piden libros prestados, ingrese la probabilidad de que se retiren de la biblioteca: ");
        TextField probabilidadSeRetiraInput = new TextField();
        probabilidadSeRetiraInput.setPromptText("Ej.: 0.6");
        probabilidadSeRetiraInput.setText(probabilidadSeRetira == null ? "" : Double.toString(probabilidadSeRetira));
        probabilidadSeRetiraInput.textProperty().addListener((observable, oldValue, newValue) -> {
            generateButton.setDisable(!validateInputsMainScene(null, null, null, null, null, null, null, null, null, newValue, null));
        });

        // mediaLectura
        Label mediaLecturaLabel = new Label("Ingrese la media de tiempo de lectura en minutos: ");
        TextField mediaLecturaInput = new TextField();
        mediaLecturaInput.setPromptText("Ej.: 30");
        mediaLecturaInput.setText(mediaLectura == null ? "" : Double.toString(mediaLectura));
        mediaLecturaInput.textProperty().addListener((observable, oldValue, newValue) -> {
            generateButton.setDisable(!validateInputsMainScene(null, null, null, null, null, null, null, null, null, null, newValue));
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
                minDemoraConsultaLabel, minDemoraConsultaInput,
                maxDemoraConsultaLabel, maxDemoraConsultaInput,
                probabilidadSeRetiraLabel, probabilidadSeRetiraInput,
                mediaLecturaLabel, mediaLecturaInput);
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

        Scene mainScene = new Scene(borderPane, 640, 800);
        stage.setScene(mainScene);
        stage.show();

        generateButton.setOnAction(e -> {
            // crear matriz
            Object[][] matriz = VectorEstado.generadorVectoresParImpar();
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
            String probabilidadConsultarNewValue, String minDemoraConsultaNewValue, String maxDemoraConsultaNewValue,
            String probabilidadSeRetiraNewValue, String mediaLecturaNewValue) {
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
            if (minDemoraConsultaNewValue != null)
                minDemoraConsulta = Double.parseDouble(minDemoraConsultaNewValue);
            if (maxDemoraConsultaNewValue != null)
                maxDemoraConsulta = Double.parseDouble(maxDemoraConsultaNewValue);
            if (probabilidadSeRetiraNewValue != null) {
                probabilidadSeRetira = Double.parseDouble(probabilidadSeRetiraNewValue);
                probabilidadSeQueda = 1 - probabilidadSeRetira;
            }
            if (mediaLecturaNewValue != null) mediaLectura = Double.parseDouble(mediaLecturaNewValue);
        } catch (NullPointerException | NumberFormatException e) {
            return false;
        }

        return tiempoSimulacion != null && cantidadDeFilasAMostrarI != null && primerFilaTablaJ != null &&
                probabilidadPedir != null && probabilidadDevolver != null && probabilidadConsultar != null &&
                minDemoraConsulta != null && maxDemoraConsulta != null && probabilidadSeRetira != null &&
                mediaLectura != null &&
                // Validaciones
                0 < tiempoSimulacion &&
                1 <= cantidadDeFilasAMostrarI && cantidadDeFilasAMostrarI <= CANTIDAD_MAXIMA_ITERACIONES - primerFilaTablaJ &&
                0 <= primerFilaTablaJ && primerFilaTablaJ <= CANTIDAD_MAXIMA_ITERACIONES - 1 &&
                0 < mediaLlegada &&
                0 <= probabilidadPedir && probabilidadPedir <= 1 &&
                0 <= probabilidadDevolver && probabilidadDevolver <= 1 &&
                0 <= probabilidadConsultar && probabilidadConsultar <= 1 &&
                probabilidadPedir + probabilidadDevolver + probabilidadConsultar == 1 &&
                0 < minDemoraConsulta && 0 < maxDemoraConsulta && minDemoraConsulta < maxDemoraConsulta &&
                0 <= probabilidadSeRetira && probabilidadSeRetira <= 1 &&
                0 < mediaLectura;
    }
}