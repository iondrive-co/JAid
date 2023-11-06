package jaid.collection;

import com.google.common.base.Preconditions;

public interface IVector {

    double dotProduct(final IVector comparedTo);

    <T extends IVector> T minus(T operand);

    <T extends IVector> T normalize();

    <T extends IVector> T plus(T operand);

    /**
     * Allocates this vector to one of n buckets, where n is 2^pow
     */
    default int simBucket(int pow) {
        final int[] accum = simHashCounts();
        Preconditions.checkArgument(pow > 0, "Power must be positive and non-zero.");
        Preconditions.checkArgument(pow <= 5, "Power cannot exceed bit length of hash.");
        int buckets = 1 << pow;
        int segmentSize = accum.length / buckets;
        int finalHash = 0;
        // Calculate the bit value for each bucket by iterating over each segment.
        for (int bucket = 0; bucket < buckets; bucket++) {
            int sum = 0;
            for (int j = bucket * segmentSize; j < (bucket + 1) * segmentSize; j++) {
                sum += accum[j];
            }
            // Set bit in finalHash if sum of that segment is greater than zero.
            if (sum > 0) {
                finalHash |= (1 << bucket);
            }
        }
        return finalHash;
    }

    int[] simHashCounts();

    String toString();
}
