package org.example.Distribuciones;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum EnumIntervals {

    INTERVALO_1(10),
    INTERVALO_2(15),
    INTERVALO_3(20),
    INTERVALO_4(25);

    int value;

    public static EnumIntervals fromValue(int value) {
        for (EnumIntervals interval : EnumIntervals.values()) {
            if (interval.getValue() == value) {
                return interval;
            }
        }
        throw new IllegalArgumentException("Invalid value: " + value);
    }

}
