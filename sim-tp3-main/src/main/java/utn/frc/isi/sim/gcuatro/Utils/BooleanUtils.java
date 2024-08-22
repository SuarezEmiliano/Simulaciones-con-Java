package utn.frc.isi.sim.gcuatro.Utils;

public class BooleanUtils {

    public static boolean isAnyValueOfArrayFalse(boolean[] array) {
        return !java.util.stream.IntStream.range(0, array.length).allMatch(i -> array[i]);
    }

}
