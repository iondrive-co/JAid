package jaid.collection;

/**
 * Used to performance test the FMA intrinsic dot product performance
 */
public record FmaIntrinsicDoublesVector (double[] contents) {

    public double dotProduct(final FmaIntrinsicDoublesVector comparedTo) {
        double sum = 0;
        for (int i = 0; i < contents.length; ++i) {
            sum = Math.fma(contents[i], comparedTo.contents[i], sum);
        }
        return sum;
    }
}
