package jaid.collection;

import com.google.common.base.Preconditions;
import jdk.incubator.vector.DoubleVector;
import jdk.incubator.vector.VectorOperators;

import java.util.Arrays;

import static jaid.number.HashingUtil.compressHash;
import static jdk.incubator.vector.DoubleVector.SPECIES_256;

public record DoublesVector(double[] contents) implements IVector {

    public DoublesVector(double[] contents) {
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
        if (!(comparedTo instanceof DoublesVector) || contents.length != ((DoublesVector) comparedTo).contents.length) {
            throw new IllegalArgumentException();
        }
        double[] left = contents;
        double[] right = ((DoublesVector) comparedTo).contents;
        int size = left.length;
        int width = SPECIES_256.length();
        // Handle case where array length is not a multiple of (width * 4)
        int vectorizableLimit = size - (size % (width * 4));
        // Multiple accumulators for better instruction-level parallelism
        var sum1 = DoubleVector.zero(SPECIES_256);
        var sum2 = DoubleVector.zero(SPECIES_256);
        var sum3 = DoubleVector.zero(SPECIES_256);
        var sum4 = DoubleVector.zero(SPECIES_256);
        // Main vectorized loop with 4x unrolling
        for (int i = 0; i < vectorizableLimit; i += width * 4) {
            sum1 = DoubleVector.fromArray(SPECIES_256, left, i)
                    .fma(DoubleVector.fromArray(SPECIES_256, right, i), sum1);
            sum2 = DoubleVector.fromArray(SPECIES_256, left, i + width)
                    .fma(DoubleVector.fromArray(SPECIES_256, right, i + width), sum2);
            sum3 = DoubleVector.fromArray(SPECIES_256, left, i + width * 2)
                    .fma(DoubleVector.fromArray(SPECIES_256, right, i + width * 2), sum3);
            sum4 = DoubleVector.fromArray(SPECIES_256, left, i + width * 3)
                    .fma(DoubleVector.fromArray(SPECIES_256, right, i + width * 3), sum4);
        }
        // Handle remaining elements
        double remainderSum = 0;
        for (int i = vectorizableLimit; i < size; i++) {
            remainderSum += left[i] * right[i];
        }
        // Combine all sums
        return sum1.reduceLanes(VectorOperators.ADD) +
                sum2.reduceLanes(VectorOperators.ADD) +
                sum3.reduceLanes(VectorOperators.ADD) +
                sum4.reduceLanes(VectorOperators.ADD) +
                remainderSum;
    }

    public double meanSquaredError(final DoublesVector comparedTo) {
        return distance(contents, comparedTo.contents);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends IVector> T minus(T operand) {
        final double[] newContents = new double[contents.length];
        for (int i = 0; i < contents.length; i++) {
            newContents[i] = contents[i] - ((DoublesVector)operand).contents[i];
        }
        return (T)new DoublesVector(newContents);
    }

    @Override
    public DoublesVector normalize() {
        double magnitude = Math.sqrt(this.dotProduct(this));
        if (magnitude == 0) return this; // avoid division by zero for a zero vector

        double[] normalizedContents = new double[this.contents.length];
        for (int i = 0; i < this.contents.length; i++) {
            normalizedContents[i] = this.contents[i] / magnitude;
        }
        return new DoublesVector(normalizedContents);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends IVector> T plus(T operand) {
        final double[] newContents = new double[contents.length];
        for (int i = 0; i < contents.length; i++) {
            newContents[i] = contents[i] + ((DoublesVector)operand).contents[i];
        }
        return (T)new DoublesVector(newContents);
    }

    @Override
    public int simBucket(byte bits) {
        // Shortcut - if we want every hash to be the same, then don't compute anything
        if (bits == 0) {
            return 0;
        }
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
        DoublesVector that = (DoublesVector) o;
        return Arrays.equals(contents, that.contents);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(contents);
    }
}
