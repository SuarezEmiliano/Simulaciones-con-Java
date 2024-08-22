package utn.frc.isi.sim.g4.RungeKutta;

import utn.frc.isi.sim.g4.RungeKutta.GeneradorExcelRG;

public class RungeKutta {

    private static double calcular_x(double x4) {
        return x4;
    }

    private static double calcular_y(double y, double h, double k1, double k2, double k3, double k4) {
            return (y + (h / 6) * (k1 + 2 * k2 + 2 * k3 + k4));
    }

    private static double calcular_k1(double x, double y, double valorMdiferencial, double valorTdiferencial) {
        return ((valorTdiferencial * x) + (valorMdiferencial * y));
    }

    private static double calcular_x2(double x, double y, double h) {
        return (x + h / 2);
    }

    private static double calcular_y2(double y, double h, double k1) {
        return (y + h / 2 * k1);
    }

    private static double calcular_k2(double x2, double y2, double valorMdiferencial, double valorTdiferencial) {
        return ((valorTdiferencial * x2) + (valorMdiferencial * y2));
    }

    private static double calcular_x3(double x2) {
        return x2; //~ x3 = x2
    }

    private static double calcular_y3(double y, double h, double k2) {
        return (y + h / 2 * k2);
    }

    private static double calcular_k3(double x3, double y3, double valorMdiferencial,double valorTdiferencial) {
        return ((valorTdiferencial * x3) + (valorMdiferencial * y3));
    }

    private static double calcular_x4(double x, double h) {
        return (x + h);
    }

    private static double calcular_y4(double y, double h, double k3) {
        return (y + h * k3);
    }

    private static double calcular_k4(double x4, double y4, double valorMdiferencial, double valorTdiferencial) {
        return ((valorTdiferencial * x4) + (valorMdiferencial * y4));
    }

    public static double[][] generadorVectoresParImpar(double h, double valorMdiferencial, double valorTdiferencial, double Max) {
        double y = 0;
        double fila = 0;

        // Creación de los vectores
        double[] vectorPar = new double[12];
        double[] vectorImpar = new double[12];

        // Creación de una lista para almacenar las filas de la matriz
        java.util.List<double[]> matrizList = new java.util.ArrayList<>();

        // Generación de valores aleatorios para los vectores
        while (y <= Max) {
            double x, k1, x2, y2, k2, x3, y3, k3, x4, y4, k4;

            if (fila == 0) {
                x = 0;
                y = 0;
                k1 = calcular_k1(x, y, valorMdiferencial, valorTdiferencial);
                x2 = calcular_x2(x, y, h);
                y2 = calcular_y2(y, h, k1);
                k2 = calcular_k2(x2, y2, valorMdiferencial, valorTdiferencial);
                x3 = calcular_x3(x2);
                y3 = calcular_y3(y, h, k2);
                k3 = calcular_k3(x3, y3, valorMdiferencial, valorTdiferencial);
                x4 = calcular_x4(x, h);
                y4 = calcular_y4(y, h, k3);
                k4 = calcular_k4(x4, y4, valorMdiferencial, valorTdiferencial);
            } else {
                double[] vectorAnterior = fila % 2 == 0 ? vectorImpar : vectorPar;

                x = calcular_x(vectorAnterior[9]);
                y = calcular_y(vectorAnterior[1], h, vectorAnterior[2], vectorAnterior[5], vectorAnterior[8], vectorAnterior[11]);
                k1 = calcular_k1(x, y, valorMdiferencial, valorTdiferencial);
                x2 = calcular_x2(x, y, h);
                y2 = calcular_y2(y, h, k1);
                k2 = calcular_k2(x2, y2, valorMdiferencial, valorTdiferencial);
                x3 = calcular_x3(x2);
                y3 = calcular_y3(y, h, k2);
                k3 = calcular_k3(x3, y3,valorMdiferencial, valorTdiferencial);
                x4 = calcular_x4(x, h);
                y4 = calcular_y4(y, h, k3);
                k4 = calcular_k4(x4, y4,valorMdiferencial, valorTdiferencial);
            }

            double[] vectorActual = {
                    x,
                    y,
                    k1,
                    x2,
                    y2,
                    k2,
                    x3,
                    y3,
                    k3,
                    x4,
                    y4,
                    k4
            };

            // Asignar el vector actual al vector par o impar según corresponda
            if (fila % 2 == 0) {
                vectorPar = vectorActual;
            } else {
                vectorImpar = vectorActual;
            }

            // Añadir el vector actual a la lista
            matrizList.add(vectorActual);

            // Actualizar el valor de y para la siguiente iteración
            y = vectorActual[1];

            fila += 1;
        }

        // Convertir la lista a una matriz
        double[][] matriz = new double[matrizList.size()][12];
        for (int i = 0; i < matrizList.size(); i++) {
            matriz[i] = matrizList.get(i);
        }

        return matriz;
    }

    public static double obtenerPrimerValorXSuperiorA(double[][] matriz, double M) {
        for (int i = 0; i < matriz.length; i++) {
            if (matriz[i][1] > M) {
                return matriz[i][0];
            }
        }
        return Double.NaN; // Indica que no se encontró ningún valor superior a M
    }

    public static void main(String[] args) {
        double[][] matriz = generadorVectoresParImpar(0.1, 0.6, 0.7, 37);
        for (int fila = 0; fila < matriz.length; fila++) {
            GeneradorExcelRG.crearExcel("rg.xls", matriz);
            System.out.print("Fila " + fila + ": [ ");
            for (double value : matriz[fila]) {
                System.out.print(value + " ");
            }
            System.out.println("]");
        }
    }
}
