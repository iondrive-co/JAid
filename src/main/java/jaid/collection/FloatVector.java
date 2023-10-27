package jaid.collection;

import java.util.Arrays;

public class FloatVector {

    public float[] contents;

    public FloatVector(float[] contents) {
        this.contents = contents;
    }

    public float meanSquaredError(final FloatVector comparedTo) {
        return distance(contents, comparedTo.contents);
    }

    /**
     * Calculate the mean squared error.
     * for vectors v1 and v2 of length I calculate
     * MSE = 1/I * Sum[ ( v1_i - v2_i )^2 ]
     */
    public static float distance(final float[] vector1, final float[] vector2) {
        if (vector1.length != vector2.length) {
            throw new IllegalArgumentException();
        }
        float sum = 0;
        for(int i = 0; i < vector1.length; i++) {
            sum += Math.pow(vector1[i] - vector2[i] , 2 );
        }
        return (sum / vector1.length);
    }

    public double dotProduct(final FloatVector comparedTo) {
        return dotProduct(contents, comparedTo.contents);
    }

    public static double dotProduct(final float[] vector1, final float[] vector2) {
        if (vector1.length != vector2.length) {
            throw new IllegalArgumentException();
        }
        float sum = 0;
        for (int i = 0; i < vector1.length; ++i) {
            sum = Math.fma(vector1[i], vector2[i], sum);
        }
        return sum;
        // TODO when panama is no longer incubating, the following should provide a large speedup
//        var sum = YMM_FLOAT.zero();
//        for (int i = 0; i < size; i += YMM_FLOAT.length()) {
//            var l = YMM_FLOAT.fromArray(left, i);
//            var r = YMM_FLOAT.fromArray(right, i);
//            sum = l.fma(r, sum);
//        }
//        return sum.addAll();
    }

    /**
     * https://en.wikipedia.org/wiki/SimHash
     */
    @Override
    public int hashCode() {
        int[] accum = new int[32];
        for (int i = 0; i < contents.length; i++) {
            int hash = Float.floatToIntBits(contents[i]);
            for (int j = 0; j < 32; j++) {
                if ((hash & (1 << j)) != 0) {
                    accum[j]++;
                } else {
                    accum[j]--;
                }
            }
        }
        int finalHash = 0;
        for (int j = 0; j < 32; j++) {
            if (accum[j] > 0) {
                finalHash |= (1 << j);
            }
        }
        return finalHash;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FloatVector that = (FloatVector) o;
        return Arrays.equals(contents, that.contents);
    }
}
