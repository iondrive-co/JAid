package jaid.number;

import com.google.common.base.Preconditions;

import java.util.Arrays;
import java.util.Random;

/**
 */
public class Maths {

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

//    public static INDArray sigmoid(final INDArray values) {
//        return pow(exp(values.neg()).add(1), -1);
//    }
//
//    public static INDArray tanh(final INDArray values) {
//        return sigmoid(values.mul(2)).mul(2).sub(1);
//    }

    public static double sigmoidDerivative(final double value) {
        return value * (1 - value);
    }

//    public static INDArray sigmoidDerivative(final INDArray values) {
//        return values.mul(Nd4j.onesLike(values).sub(values));
//    }
//
//    public static INDArray tanhDerivative(final INDArray values) {
//        return Nd4j.onesLike(values).sub(pow(values, 2));
//    }

    public static double random(final Random random, final int lowerBoundInc, final int upperBoundExc) {
        final double val = random.nextDouble();
        final int range = upperBoundExc - lowerBoundInc;
        return (val * range) + lowerBoundInc;
    }
}
