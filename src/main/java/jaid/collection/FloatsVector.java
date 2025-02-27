package jaid.collection;

import com.google.common.base.Preconditions;
import jdk.incubator.vector.FloatVector;
import jdk.incubator.vector.VectorOperators;

import java.util.Arrays;

import static jaid.number.HashingUtil.compressHash;
import static jdk.incubator.vector.FloatVector.SPECIES_256;

public record FloatsVector(float[] contents) implements IVector {

    public FloatsVector(float[] contents) {
        this.contents = Preconditions.checkNotNull(contents);
    }

    @Override
    public double angleBetween(final IVector other) {
        double dotProduct = normalize().dotProduct(other.normalize());
        // Clamp to valid domain for acos
        return Math.acos(Math.min(1.0, Math.max(-1.0, dotProduct)));
    }

    @Override
    public <T extends IVector> double distance(final T other) {
        // MSE * dimension = sum of squared differences
        return Math.sqrt(meanSquaredError(other) * contents.length);
    }

    @Override
    public double dotProduct(final IVector comparedTo) {
        if (!(comparedTo instanceof FloatsVector) || contents.length != ((FloatsVector) comparedTo).contents.length) {
            throw new IllegalArgumentException();
        }
        // Use unrolled vector calculation based on https://richardstartin.github.io/posts/vector-api-dot-product
        // This is much faster for large vectors than a Math.fma intrinsic, see DoublesVectorPerformanceTest
        float[] left = contents;
        float[] right = ((FloatsVector) comparedTo).contents;
        int size = left.length;
        int width = SPECIES_256.length();
        // Handle case where array length is not a multiple of (width * 4)
        int vectorizableLimit = size - (size % (width * 4));
        // Multiple accumulators for better instruction-level parallelism
        var sum1 = FloatVector.zero(SPECIES_256);
        var sum2 = FloatVector.zero(SPECIES_256);
        var sum3 = FloatVector.zero(SPECIES_256);
        var sum4 = FloatVector.zero(SPECIES_256);
        // Main vectorized loop with 4x unrolling
        for (int i = 0; i < vectorizableLimit; i += width * 4) {
            sum1 = FloatVector.fromArray(SPECIES_256, left, i)
                    .fma(FloatVector.fromArray(SPECIES_256, right, i), sum1);
            sum2 = FloatVector.fromArray(SPECIES_256, left, i + width)
                    .fma(FloatVector.fromArray(SPECIES_256, right, i + width), sum2);
            sum3 = FloatVector.fromArray(SPECIES_256, left, i + width * 2)
                    .fma(FloatVector.fromArray(SPECIES_256, right, i + width * 2), sum3);
            sum4 = FloatVector.fromArray(SPECIES_256, left, i + width * 3)
                    .fma(FloatVector.fromArray(SPECIES_256, right, i + width * 3), sum4);
        }
        // Handle remaining elements
        float remainderSum = 0;
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

    @Override
    public double magnitude() {
        double sum = 0;
        for (int i = 0; i < contents.length; i++) {
            sum += contents[i] * contents[i];
        }
        return Math.sqrt(sum);
    }

    @Override
    public <T extends IVector> double meanSquaredError(final T other) {
        if (!(other instanceof FloatsVector) || contents.length != ((FloatsVector)other).contents().length) {
            throw new IllegalArgumentException("Vectors must have the same dimensions");
        }
        double sumSquaredDiff = 0;
        for (int i = 0; i < contents.length; i++) {
            double diff = contents[i] - ((FloatsVector)other).contents()[i];
            sumSquaredDiff += diff * diff;
        }
        return sumSquaredDiff / contents.length;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends IVector> T minus(T operand) {
        final float[] newContents = new float[contents.length];
        for (int i = 0; i < contents.length; i++) {
            newContents[i] = contents[i] - ((FloatsVector)operand).contents[i];
        }
        return (T)new FloatsVector(newContents);
    }

    @Override
    public FloatsVector normalize() {
        float magnitude = (float)Math.sqrt(this.dotProduct(this));
        if (magnitude == 0) return this; // avoid division by zero for a zero vector

        float[] normalizedContents = new float[this.contents.length];
        for (int i = 0; i < this.contents.length; i++) {
            normalizedContents[i] = this.contents[i] / magnitude;
        }
        return new FloatsVector(normalizedContents);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends IVector> T plus(T operand) {
        final float[] newContents = new float[contents.length];
        for (int i = 0; i < contents.length; i++) {
            newContents[i] = contents[i] + ((FloatsVector)operand).contents[i];
        }
        return (T)new FloatsVector(newContents);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends IVector> T scale(final float amount) {
        final float[] newContents = new float[contents.length];
        for (int i = 0; i < contents.length; i++) {
            newContents[i] = contents[i] * amount;
        }
        return (T)new FloatsVector(newContents);
    }

    @Override
    public int simBucket(byte bits) {
        // Shortcut - if we want every hash to be the same, then don't compute anything
        if (bits == 0) {
            return 0;
        }
        int finalHash = 0;
        for (float content : contents) {
            int hash = Float.floatToIntBits(content);
            for (int j = 0; j < 32; j++) {
                if ((hash & (1 << j)) != 0) {
                    finalHash |= (1 << j);
                }
            }
        }
        return compressHash(bits, finalHash, (byte)32);
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
        FloatsVector that = (FloatsVector) o;
        return Arrays.equals(contents, that.contents);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(contents);
    }
}
