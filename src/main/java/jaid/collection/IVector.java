package jaid.collection;

public interface IVector {

    /**
     * @return the angle between this vector and another vector in radian, in range [0,π]
     */
    double angleBetween(final IVector operand);

    /**
     *The Euclidean distance is the length of the straight-line path between two points
     * in Euclidean space, calculated as the square root of the sum of squared differences
     * between corresponding coordinates.
     *
     * @return the Euclidean distance between the two vectors
     * @throws IllegalArgumentException if vectors have different dimensions
     */
    <T extends IVector> double distance(final T other);

    double dotProduct(final IVector comparedTo);

    <T extends IVector> T minus(final T operand);

    /**
     * The magnitude of a vector is its length in Euclidean space, calculated as
     * the square root of the sum of squares of its components (using the Pythagorean theorem).
     * In 2D, for vector (x,y): magnitude = √(x² + y²)
     * In 3D, for vector (x,y,z): magnitude = √(x² + y² + z²)
     * And so on for higher dimensions.
     */
    double magnitude();

    /**
     * MSE measures the average squared Euclidean distance per dimension between two points, it is the variance of the
     * residuals between corresponding points in the two vectors.
     * @throws IllegalArgumentException if vectors have different dimensions or incompatible types
     */
    <T extends IVector> double meanSquaredError(final T other);

    <T extends IVector> T normalize();

    <T extends IVector> T plus(final T operand);

    <T extends IVector> T scale(final float amount);

    /**
     * Allocates this vector to one of n buckets, where n is 2^bits.
     * @param bits the number of bits to be used for bucket allocation
     * @return the bucket number the current vector is allocated to
     */
    int getSimHashBucket(final byte bits);

    String toString();
}
