package utn.frc.isi.sim.g4.Enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum EnumTiposLlegada {

    Peticion1("Peticion1"),
    Peticion2("Peticion2"),
    Devolucion1("Devolucion1"),
    Devolucion2("Devolucion2"),
    Consulta1("Consulta1"),
    Consulta2("Consulta2");

    String nombre;

}
