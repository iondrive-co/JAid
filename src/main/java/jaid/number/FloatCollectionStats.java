package jaid.number;

import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.floats.FloatIterator;
import it.unimi.dsi.fastutil.floats.FloatList;
import jaid.collection.Collections;
import jaid.collection.FloatArrayIterator;

import java.util.Arrays;
import java.util.function.DoublePredicate;

/**
 */
public class FloatCollectionStats implements CollectionStats {

    private static final DoublePredicate POSITIVE_CHECK = d -> d >= 0;
    private final int numElements;
    private float[] array;
    private FloatList collection;
    private FloatIterator iterator;
    // On demand
    private float[] sorted;
    private float[] positiveSorted;
    private double mean = Double.NaN;
    private double stdDev= Double.NaN;
    private double max = Double.NaN;
    private double min= Double.NaN;

    public FloatCollectionStats(final float[] array) {
        this.numElements = array.length;
        Preconditions.checkArgument(numElements > 0);
        this.array = array;
        recreateIterator();
    }

    public FloatCollectionStats(final FloatList collection) {
        this.numElements = collection.size();
        Preconditions.checkArgument(numElements > 0);
        this.collection = collection;
        recreateIterator();
    }

    private void recreateIterator() {
        iterator = array == null ? collection.iterator() : new FloatArrayIterator(array);
    }

    private float[] getSorted() {
        if (sorted == null) {
            sorted = Collections.extract(iterator, numElements);
            Arrays.sort(sorted);
            recreateIterator();
        }
        return sorted;
    }

    @Override
    public double getValue(int position) {
        return array == null ? collection.getFloat(position) : array[position];
    }

    @Override
    public int getSize() {
        return array == null ? collection.size() : array.length;
    }

    @Override
    public void fillQuantiles(double[] quantiles) {
        final float[] sorted = getSorted();
        final float numQuantiles = quantiles.length;
        final int partitionIdx = (int)Math.floor(sorted.length / (numQuantiles + 1));
        for (int i = 0; i < numQuantiles; i++) {
            quantiles[i] = sorted[(i + 1) * partitionIdx];
            Preconditions.checkState(!Double.isNaN(quantiles[i]));
        }
    }

    private float[] getPositiveSorted() {
        if (positiveSorted == null) {
            final FloatList collectionCopy = Collections.extract(iterator, POSITIVE_CHECK);
            positiveSorted = collectionCopy.toFloatArray();
            Arrays.sort(positiveSorted);
            recreateIterator();
        }
        return positiveSorted;
    }

    @Override
    public void fillPositiveQuantiles(double[] quantiles) {
        final float[] positiveSorted = getPositiveSorted();
        final float numQuantiles = quantiles.length;
        final int partitionIdx = (int)Math.floor(positiveSorted.length / (numQuantiles + 1));
        for (int i = 0; i < numQuantiles; i++) {
            quantiles[i] = positiveSorted[(i + 1) * partitionIdx];
            Preconditions.checkState(!Double.isNaN(quantiles[i]));
        }
    }

    @Override
    public double mean() {
        if (Double.isNaN(mean)) {
            mean = 0;
            int t = 1;
            for (float value = iterator.nextFloat(); iterator.hasNext(); value = iterator.nextFloat()) {
                mean += (value - mean) / t;
                ++t;
            }
            recreateIterator();
            Preconditions.checkState(!Double.isNaN(mean));
        }
        return mean;
    }

    @Override
    public double stdDev() {
        if (Double.isNaN(stdDev)) {
            double mean = 0;
            double sum = 0;
            int tally = 1;
            for (float value = iterator.nextFloat(); iterator.hasNext(); value = iterator.nextFloat()) {
                double oldMean = mean;
                mean = mean + ((value - mean) / tally++);
                sum = sum + ((value - mean) * (value - oldMean));
            }
            stdDev = StrictMath.sqrt(sum/(numElements-1));
            if (Double.isNaN(this.mean)) {
                this.mean = mean;
            }
            recreateIterator();
            Preconditions.checkState(!Double.isNaN(mean));
            Preconditions.checkState(!Double.isNaN(stdDev));
        }
        return stdDev;
    }

    @Override
    public double max() {
        if (Double.isNaN(max)) {
            max = -Double.MAX_VALUE;
            for (float value = iterator.nextFloat(); iterator.hasNext(); value = iterator.nextFloat()) {
                max = Math.max(max, value);
            }
            recreateIterator();
            Preconditions.checkState(!Double.isNaN(max));
        }
        return max;
    }

    @Override
    public double min() {
        if (Double.isNaN(min)) {
            min = Double.MAX_VALUE;
            for (float value = iterator.nextFloat(); iterator.hasNext(); value = iterator.nextFloat()) {
                min = Math.min(min, value);
            }
            recreateIterator();
            Preconditions.checkState(!Double.isNaN(min));
        }
        return min;
    }

    @Override
    public double[] normalise() {
        // Do std dev before mean since it calculates mean as a byproduct
        final double stdDev = stdDev();
        final double mean = mean();
        final double[] normalised = new double[numElements];
        int i = 0;
        for (float value = iterator.nextFloat(); iterator.hasNext(); value = iterator.nextFloat()) {
            Preconditions.checkState(!Float.isNaN(value));
            normalised[i++] = (((value - mean) / stdDev) * 0.3) + 0.5;
        }
        recreateIterator();
        return normalised;
    }

    @Override
    public double[] standardise() {
        // Do std dev before mean since it calculates mean as a byproduct
        final double stdDev = stdDev();
        final double mean = mean();
        final double[] normalised = new double[numElements];
        int i = 0;
        for (float value = iterator.nextFloat(); iterator.hasNext(); value = iterator.nextFloat()) {
            Preconditions.checkState(!Float.isNaN(value));
            normalised[i++] = (value - mean) / (stdDev * 2);
        }
        recreateIterator();
        return normalised;
    }

    @Override
    public double denormalise(final double normalisedValue) {
        // Do std dev before mean since it calculates mean as a byproduct
        final double stdDev = stdDev();
        final double mean = mean();
        return (((normalisedValue - 0.5) / 0.3) * stdDev) + mean;
    }

    @Override
    public double destandardise(final double normalisedValue) {
        // Do std dev before mean since it calculates mean as a byproduct
        final double stdDev = stdDev();
        final double mean = mean();
        return (normalisedValue * (stdDev * 2)) + mean;
    }
}
