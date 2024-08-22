package utn.frc.isi.sim.g4.Enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum EnumLuegoPeticion {
    
    SeVa("Se Va"),
    SeQueda("Se Queda");

    String nombre;

}
