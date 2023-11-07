package jaid.number;

import com.google.common.base.Preconditions;

import java.util.Arrays;
import java.util.Random;

/**
 */
public class Maths {

    public static long INTEGER_RANGE = ((long)Integer.MAX_VALUE) - (long)Integer.MIN_VALUE + 1; // plus one to include Integer.MIN_VALUE itself.

    public static boolean isNan(final double[] values) {
        boolean isNan = false;
        for (final double value: values) {
            isNan |= Double.isNaN(value);
        }
        return isNan;
    }

    public static double mean(final double[] values) {
        return Arrays.stream(values).summaryStatistics().getAverage();
    }

    public static double mean(final int[] values) {
        return Arrays.stream(values).summaryStatistics().getAverage();
    }

    public static double[] mean(double[] values1, double[] values2) {
        Preconditions.checkArgument(values1.length == values2.length);
        final double[] results = new double[values1.length];
        for (int i = 0; i < values1.length; i++) {
            results[i] = mean(values1[i], values2[i]);
        }
        return results;
    }

    public static double mean(final double value1, final double value2) {
        return (value2 - value1) / 2;
    }

    public static double sigmoid(final double value) {
        return 1 / (1 + StrictMath.exp(-value));
    }

    public static double sigmoidDerivative(final double value) {
        return value * (1 - value);
    }

    public static double random(final Random random, final int lowerBoundInc, final int upperBoundExc) {
        final double val = random.nextDouble();
        final int range = upperBoundExc - lowerBoundInc;
        return (val * range) + lowerBoundInc;
    }
}
