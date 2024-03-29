package jaid.collection;

import com.google.common.base.Preconditions;

import java.util.Arrays;

import static jaid.number.HashingUtil.compressHash;

public record FloatVector(float[] contents) implements IVector {

    public FloatVector(float[] contents) {
        this.contents = Preconditions.checkNotNull(contents);
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
        for (int i = 0; i < vector1.length; i++) {
            sum += Math.pow(vector1[i] - vector2[i], 2);
        }
        return (sum / vector1.length);
    }

    @Override
    public double dotProduct(final IVector comparedTo) {
        if (!(comparedTo instanceof FloatVector) || contents.length != ((FloatVector) comparedTo).contents.length) {
            throw new IllegalArgumentException();
        }
        float sum = 0;
        for (int i = 0; i < contents.length; ++i) {
            sum = Math.fma(contents[i], ((FloatVector) comparedTo).contents[i], sum);
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

    public float meanSquaredError(final FloatVector comparedTo) {
        return distance(contents, comparedTo.contents);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends IVector> T minus(T operand) {
        final float[] newContents = new float[contents.length];
        for (int i = 0; i < contents.length; i++) {
            newContents[i] = contents[i] - ((FloatVector)operand).contents[i];
        }
        return (T)new FloatVector(newContents);
    }

    @Override
    public FloatVector normalize() {
        float magnitude = (float)Math.sqrt(this.dotProduct(this));
        if (magnitude == 0) return this; // avoid division by zero for a zero vector

        float[] normalizedContents = new float[this.contents.length];
        for (int i = 0; i < this.contents.length; i++) {
            normalizedContents[i] = this.contents[i] / magnitude;
        }
        return new FloatVector(normalizedContents);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends IVector> T plus(T operand) {
        final float[] newContents = new float[contents.length];
        for (int i = 0; i < contents.length; i++) {
            newContents[i] = contents[i] + ((FloatVector)operand).contents[i];
        }
        return (T)new FloatVector(newContents);
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
        FloatVector that = (FloatVector) o;
        return Arrays.equals(contents, that.contents);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(contents);
    }
}
