package utn.frc.isi.sim.gcuatro;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum EnumReservas {

    RESERVA_31(31),
    RESERVA_32(32),
    RESERVA_33(33),
    RESERVA_34(34);

    int value;

    public static EnumReservas fromValue(int value) {
        for (EnumReservas reserva : EnumReservas.values()) {
            if (reserva.getValue() == value) {
                return reserva;
            }
        }
        throw new IllegalArgumentException("Valor inválido: " + value);
    }
}
