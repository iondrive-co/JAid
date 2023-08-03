package jaid.collection;

public class DoubleVector {

    public double[] contents;

    public DoubleVector(double[] contents) {
        this.contents = contents;
    }

    public double meanSquaredError(final DoubleVector comparedTo) {
        return distance(contents, comparedTo.contents);
    }

    /**
     * Calculate the mean squared error.
     * for vectors v1 and v2 of length I calculate
     * MSE = 1/I * Sum[ ( v1_i - v2_i )^2 ]
     */
    public static double distance(final double[] vector1, final double[] vector2) {
        if (vector1.length != vector2.length) {
            throw new IllegalArgumentException();
        }
        double sum = 0;
        for(int i = 0; i < vector1.length; i++) {
            sum += Math.pow(vector1[i] - vector2[i] , 2 );
        }
        return (sum / vector1.length);
    }

    public double crossProduct(final DoubleVector comparedTo) {
        return crossProduct(contents, comparedTo.contents);
    }

    public static double crossProduct(final double[] vector1, final double[] vector2) {
        if (vector1.length != vector2.length) {
            throw new IllegalArgumentException();
        }
        double sum = 0;
        for (int i = 0; i < vector1.length; ++i) {
            sum = Math.fma(vector1[i], vector2[i], sum);
        }
        return sum;
        // TODO when panama is no longer incubating, the following should provide a large speedup
//        var sum = YMM_DOUBLE.zero();
//        for (int i = 0; i < size; i += YMM_DOUBLE.length()) {
//            var l = YMM_DOUBLE.fromArray(left, i);
//            var r = YMM_DOUBLE.fromArray(right, i);
//            sum = l.fma(r, sum);
//        }
//        return sum.addAll();
    }
}
