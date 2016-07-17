package jaid.number;

/**
 */
public class Doubles {

    public static boolean isNan(final double[] values) {
        boolean isNan = false;
        for (final double value: values) {
            isNan |= Double.isNaN(value);
        }
        return isNan;
    }
}
