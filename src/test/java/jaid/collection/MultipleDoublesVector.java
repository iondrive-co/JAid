package jaid.collection;

/**
 * Used to performance test the FMA intrinsic dot product performance
 */
public record MultipleDoublesVector(double[] contents) {

    public double dotProduct(final MultipleDoublesVector comparedTo) {
        double sum = 0;
        for (int i = 0; i < contents.length; i++) {
            sum += contents[i] * comparedTo.contents[i];
        }
        return sum;
    }
}
