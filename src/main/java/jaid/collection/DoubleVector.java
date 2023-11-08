package jaid.collection;

import com.google.common.base.Preconditions;

import java.util.Arrays;

import static jaid.number.HashingUtil.compressHash;

public record DoubleVector(double[] contents) implements IVector {

    public DoubleVector(double[] contents) {
        this.contents = Preconditions.checkNotNull(contents);
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
        for (int i = 0; i < vector1.length; i++) {
            sum += Math.pow(vector1[i] - vector2[i], 2);
        }
        return (sum / vector1.length);
    }

    @Override
    public double dotProduct(final IVector comparedTo) {
        if (!(comparedTo instanceof DoubleVector) || contents.length != ((DoubleVector) comparedTo).contents.length) {
            throw new IllegalArgumentException();
        }
        double sum = 0;
        for (int i = 0; i < contents.length; ++i) {
            sum = Math.fma(contents[i], ((DoubleVector) comparedTo).contents[i], sum);
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

    public double meanSquaredError(final DoubleVector comparedTo) {
        return distance(contents, comparedTo.contents);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends IVector> T minus(T operand) {
        final double[] newContents = new double[contents.length];
        for (int i = 0; i < contents.length; i++) {
            newContents[i] = contents[i] - ((DoubleVector)operand).contents[i];
        }
        return (T)new DoubleVector(newContents);
    }

    @Override
    public DoubleVector normalize() {
        double magnitude = Math.sqrt(this.dotProduct(this));
        if (magnitude == 0) return this; // avoid division by zero for a zero vector

        double[] normalizedContents = new double[this.contents.length];
        for (int i = 0; i < this.contents.length; i++) {
            normalizedContents[i] = this.contents[i] / magnitude;
        }
        return new DoubleVector(normalizedContents);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends IVector> T plus(T operand) {
        final double[] newContents = new double[contents.length];
        for (int i = 0; i < contents.length; i++) {
            newContents[i] = contents[i] + ((DoubleVector)operand).contents[i];
        }
        return (T)new DoubleVector(newContents);
    }

    @Override
    public int simBucket(byte bits) {
        long finalHash = 0L;
        for (double content : contents) {
            long hash = Double.doubleToLongBits(content);
            for (int j = 0; j < 64; j++) {
                if ((hash & (1L << j)) != 0) {
                    finalHash |= (1L << j);
                }
            }
        }
        return compressHash(bits, finalHash, (byte)64);
    }

    @Override
    public String toString() {
        return Arrays.toString(contents);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DoubleVector that = (DoubleVector) o;
        return Arrays.equals(contents, that.contents);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(contents);
    }
}
