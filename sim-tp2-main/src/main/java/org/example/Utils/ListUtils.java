package org.example.Utils;

import java.util.List;

public class ListUtils {

    public static double sumDoubleListFromIToJ(List<Double> list, int i, int j) {
        return list.subList(i, j + 1).stream().reduce(0.0, Double::sum);
    }

    public static int sumIntegerListFromIToJ(List<Integer> list, int i, int j) {
        return list.subList(i, j + 1).stream().reduce(0, Integer::sum);
    }

    public static double sumList(List<Double> list) {
        return list.stream().reduce(0.0, Double::sum);
    }

}
