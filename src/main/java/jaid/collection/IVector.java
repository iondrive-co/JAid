package jaid.collection;

import com.google.common.annotations.VisibleForTesting;

import static jaid.number.Maths.INTEGER_RANGE;

public interface IVector {

    double dotProduct(final IVector comparedTo);

    <T extends IVector> T minus(T operand);

    <T extends IVector> T normalize();

    <T extends IVector> T plus(T operand);

    /**
     * Allocates this vector to one of n buckets, where n is 2^bits.
     * @param bits the number of bits to be used for bucket allocation
     * @return the bucket number the current vector is allocated to
     */
    default int simBucket(int bits) {
        return simBucket(bits, simHashCounts());
    }

    @VisibleForTesting
    default int simBucket(int bits, long hash) {
        int buckets = 1 << bits;
        long segmentSize = INTEGER_RANGE / buckets;
        // Find the relative position of hash within the range starting from Integer.MIN_VALUE
        long hashOffsetFromMin = hash - Integer.MIN_VALUE;
        // Calculate bucket by finding which segment this offset falls into.
        // Since the hash is an int, we need to ensure the sign bit is treated correctly by offsetting from MIN_VALUE.
        int bucket = (int)(hashOffsetFromMin / segmentSize);
        // Since we may lose one bucket due to rounding down division, we ensure that maximum hash values map to the last bucket.
        if (hash == Integer.MAX_VALUE) {
            bucket = buckets - 1; // Assign to last bucket.
        }
        return bucket;
    }

    long simHashCounts();

    String toString();
}
