package jaid.collection;

import com.google.common.base.Preconditions;

public interface IVector {

    double dotProduct(final IVector comparedTo);

    <T extends IVector> T minus(T operand);

    <T extends IVector> T normalize();

    <T extends IVector> T plus(T operand);

    /**
     * Allocates this vector to one of 256 buckets
     * TODO we want the number of buckets to be user specified, so we can increase the buckets as we get more vectors
     */
    default byte simBucket() {
        final int[] accum = simHashCounts();
        int finalHash = 0;
        int bitsPerSection;
        // Determine the number of bits each section of the 'accum' array contributes to the final byte
        Preconditions.checkArgument(accum.length % Byte.SIZE == 0);
        bitsPerSection = accum.length / Byte.SIZE;
        for (int i = 0; i < 8; i++) {
            int sum = 0;
            // Combine the relevant 'bitsPerSection' bits to get a single bit for the final hash.
            for (int j = i * bitsPerSection; j < (i + 1) * bitsPerSection; j++) {
                sum += accum[j];
            }
            // If the sum is positive, set the bit, otherwise leave it as 0
            if (sum > 0) {
                finalHash |= (1 << i);
            }
        }
        return (byte) finalHash;
    }

    int[] simHashCounts();

    String toString();
}
