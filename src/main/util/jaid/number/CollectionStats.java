package jaid.number;

/**
 * Immutable stats on a collection
 */
public interface CollectionStats {
    double getValue(int position);
    int getSize();
    void fillQuantiles(final double[] quantiles);
    void fillPositiveQuantiles(final double[] quantiles);
    double mean();
    double stdDev();
    double max();
    double min();
    double[] normalise();
    double[] standardise();
    double denormalise(final double normalisedValue);
    double destandardise(final double normalisedValue);
}
