package utn.frc.isi.sim.gcuatro;

import java.util.Random;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.Setter;
import utn.frc.isi.sim.gcuatro.Utils.BuscarIntervalo;

import static utn.frc.isi.sim.gcuatro.Excel.crearExcel;

public class VectorEstado {

    // Método para calcular un valor RND entre 0 y 1
    private static double calcularRandom() {
        Random random = new Random();
        double rnd = random.nextDouble();
        String rndString = String.format("%.4f", rnd).replace(",", ".");
        return Double.parseDouble(rndString);
    }

    // Método para calcular la cantidad de pasajeros
    private static double calcularPasajeros(double rnd, int sobreventaABuscar) {
        return BuscarIntervalo.buscarValor(rnd);
    }

    // Método para calcular el costo por sobreventa
    private static double calcularCostoPorSobreventa(double pasajeros) {
        return pasajeros > 30 ? (pasajeros - 30) * 150 : 0;
    }

    // Método para calcular la utilidad
    private static double calcularUtilidad(double pasajeros, double utilidadingresada) {
        return pasajeros <= 30 ? pasajeros * utilidadingresada : 30 * utilidadingresada;
    }

    // Método para calcular el beneficio
    private static double calcularBeneficio(double utilidad, double costoPorSobreventa) {
        return utilidad - costoPorSobreventa;
    }

    private static double calcularBeneficioAcumulado( double beneficio, double BeneficioAcumuladoHastaAhora) {
        return beneficio + BeneficioAcumuladoHastaAhora;
    }

    // Método para calcular el promedio del beneficio
    private static double calcularPromedioBeneficio(double beneficioAcumulado, int i) {
        double promedio = beneficioAcumulado / (i + 1);
        String promedioString = String.format("%.4f", promedio).replace(",", ".");
        return Double.parseDouble(promedioString);
    }

    public static double[][] generadorVectoresParImpar(int cantidadVuelos, int sobreventa, int i, int j, double utilidadingresada) {
        Random random = new Random();

        // Creación de los vectores
        double[] vectorPar = new double[8];
        double[] vectorImpar = new double[8];

        // Creación de la matriz para almacenar la información de las columnas
        double[][] matriz = new double[j + i < cantidadVuelos ? i + 1 : i][8];
        double[] ultimaFila = null;

        // Generación de valores aleatorios para los vectores
        for (int vuelos = 0; vuelos < cantidadVuelos; vuelos++) {
            double rnd, pasajeros, costoPorSobreventa, utilidad, beneficio, beneficioAcumulado;

            if (vuelos == 0) {
                rnd = calcularRandom();
                pasajeros = calcularPasajeros(rnd, sobreventa);
                costoPorSobreventa = calcularCostoPorSobreventa(pasajeros);
                utilidad = calcularUtilidad(pasajeros, utilidadingresada);
                beneficio = calcularBeneficio(utilidad, costoPorSobreventa);
                beneficioAcumulado = calcularBeneficioAcumulado(beneficio, 0);
            } else {
                double[] vectorAnterior = vuelos % 2 == 0 ? vectorImpar : vectorPar;

                rnd = calcularRandom();
                pasajeros = calcularPasajeros(rnd, sobreventa);
                costoPorSobreventa = calcularCostoPorSobreventa(pasajeros);
                utilidad = calcularUtilidad(pasajeros, utilidadingresada);
                beneficio = calcularBeneficio(utilidad, costoPorSobreventa);
                beneficioAcumulado = calcularBeneficioAcumulado(beneficio, vectorAnterior[6]);
            }

            double promedioBeneficio = calcularPromedioBeneficio(beneficioAcumulado, vuelos);

            double[] vectorActual = {
                    vuelos,
                    rnd,
                    pasajeros,
                    costoPorSobreventa,
                    utilidad,
                    beneficio,
                    beneficioAcumulado,
                    promedioBeneficio
            };

            // Asignar el vector actual al vector par o impar según corresponda
            if (vuelos % 2 == 0) {
                vectorPar = vectorActual;
            } else {
                vectorImpar = vectorActual;
            }
            
            if (vuelos >= j && vuelos < j + i) {
                matriz[vuelos - j] = vectorActual;
            }

            // Guardar la última fila generada
            if (vuelos == cantidadVuelos - 1 && j + i < cantidadVuelos) {
                ultimaFila = vectorActual;
            }


            if (ultimaFila != null) {
                matriz[i] = ultimaFila;
            }
        }

        App.mostrarPantallaResultado(matriz[matriz.length - 1][7], utilidadingresada);

        return matriz;
    }


    public static void main(String[] args) {
//        double[][] matriz = generadorVectoresParImpar(50, 75);
//        crearExcel("tablaMontecarlo.xls", matriz);
//        for (int fila = 0; fila < matriz.length; fila++) {
//            System.out.print("Fila " + fila + ": [ ");
//            for (double value : matriz[fila]) {
//                System.out.print(value + " ");
//            }
//            System.out.println("]");
//        }
    }
}
