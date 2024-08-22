package utn.frc.isi.sim.gcuatro.Utils;

import java.util.List;

public class BuscarIntervalo {

    public static int cantidadMinimaReservas;
    public static List<Double> limitesInferiores;

    public static double buscarValor(double rnd) {
        for (int i = 0; i < limitesInferiores.size(); i++) {
            if (limitesInferiores.get(i) == 1) {
                return cantidadMinimaReservas + i;
            }
        }

        for (int i = 0; i < limitesInferiores.size(); i++) {
            if (limitesInferiores.get(i) > rnd) {
                return cantidadMinimaReservas + i - 1;
            } else if (i == limitesInferiores.size() - 1) {
                return cantidadMinimaReservas + i;
            }
        }
        return cantidadMinimaReservas + limitesInferiores.size();
    }
}
