package utn.frc.isi.sim.g4.Enums;

import java.util.Arrays;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum EnumEventos {

    Inicializacion("Inicializacion", -1),
    Llegada("Llegada de personas al mostrador", 5),
    FinPeticion1("Fin petición de libro servidor 1", 12),
    FinPeticion2("Fin petición de libro servidor 2", 13),
    FinConsulta1("Fin consulta servidor 1", 16),
    FinConsulta2("Fin consulta servidor 2", 17),
    FinDevolucion1("Fin devolución de libro servidor 1", 20),
    FinDevolucion2("Fin devolución de libro servidor 2", 21),
    FinLectura("Fin Lectura", -1);

    String nombre;
    int columna;

    public static EnumEventos fromColumn(int columna) {
        for (EnumEventos reserva : EnumEventos.values()) {
            if (reserva.getColumna() == columna) {
                return reserva;
            }
        }
        return EnumEventos.FinLectura;
        // throw new IllegalArgumentException("Columna inválida: " + columna);
    }

    public static int[] getAllColumns() {
        return Arrays.stream(EnumEventos.values())
                     .filter(reserva -> reserva.getColumna() >= 0)
                     .mapToInt(EnumEventos::getColumna)
                     .toArray();
    }

    public static void main(String[] args) {
        int[] allColumns = getAllColumns();
        System.out.println(Arrays.toString(allColumns));
    }
}
