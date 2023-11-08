package jaid.collection;

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
    int simBucket(int bits);

    String toString();
}
