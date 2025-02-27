package jaid.collection;

public interface IVector {

    double angleBetween(final IVector operand);

    <T extends IVector> double distance(final T other);

    double dotProduct(final IVector comparedTo);

    <T extends IVector> T minus(final T operand);

    double magnitude();

    <T extends IVector> double meanSquaredError(final T other);

    <T extends IVector> T normalize();

    <T extends IVector> T plus(final T operand);

    <T extends IVector> T scale(final float amount);

    /**
     * Allocates this vector to one of n buckets, where n is 2^bits.
     * @param bits the number of bits to be used for bucket allocation
     * @return the bucket number the current vector is allocated to
     */
    int simBucket(final byte bits);

    String toString();
}
