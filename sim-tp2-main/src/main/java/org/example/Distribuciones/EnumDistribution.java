package org.example.Distribuciones;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum EnumDistribution {

    EXPONENCIAL("Exponencial"),
    NORMAL("Normal"),
    UNIFORME("Uniforme");

    String name;

}
