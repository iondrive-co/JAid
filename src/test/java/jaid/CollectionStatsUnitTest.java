package jaid;

import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.floats.FloatArrayList;
import jaid.number.CollectionStats;
import jaid.number.DoubleCollectionStats;
import jaid.number.FloatCollectionStats;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.Percentage.withPercentage;

public class CollectionStatsUnitTest {

    List<CollectionStats> collectionStats;
    double[] vals;
    final double significance = 0.00001;

    @BeforeEach
    public void createStats() {
        vals = new double[10_000];
        final Random tlr = ThreadLocalRandom.current();
        for (int i = -5_000; i < 5_000; i++) {
            vals[i+5_000] = tlr.nextDouble() * significance;
        }
        final float[] floatVals = new float[vals.length];
        for (int i = 0; i < vals.length; i++) {
            floatVals[i] = (float)vals[i];
        }
        collectionStats = ImmutableList.of(new DoubleCollectionStats(vals),
                new DoubleCollectionStats(new DoubleArrayList(vals)),
                new FloatCollectionStats(floatVals), new FloatCollectionStats(new FloatArrayList(floatVals)));
    }

    @Test
    public void quantiles() {
        for (final CollectionStats collectionStat : collectionStats) {
            final double[] quantiles = new double[1];
            collectionStat.fillQuantiles(quantiles);
            assertThat(Math.abs(quantiles[0]) < 0.001);
            mean();
            stdDev();
        }
    }

    @Test
    public void mean() {
        double mean = Arrays.stream(vals).summaryStatistics().getAverage();
        for (final CollectionStats collectionStat : collectionStats) {
            assertThat(collectionStat.mean()).isCloseTo(mean, withPercentage(0.02));
        }
    }

    @Test
    public void stdDev() {
        double mean = Arrays.stream(vals).summaryStatistics().getAverage();
        double stdDev = new StandardDeviation().evaluate(vals);
        for (final CollectionStats collectionStat : collectionStats) {
            assertThat(collectionStat.stdDev()).isCloseTo(stdDev, withPercentage(0.02));
            // Mean is calculated as a by product of std dev in this case, so need to test it again.
            assertThat(collectionStat.mean()).isCloseTo(mean, withPercentage(0.02));
        }
    }

    @Test
    public void normalise() {
        for (final CollectionStats collectionStat : collectionStats) {
            final double[] normalised = collectionStat.normalise();
            double normalisedMin = Arrays.stream(normalised).summaryStatistics().getMin();
            double normalisedMax = Arrays.stream(normalised).summaryStatistics().getMax();
            assertThat(Math.abs(normalisedMin) < 0.1).isTrue();
            assertThat(normalisedMax - 1 < 0.1).isTrue();
        }
    }

    @Test
    public void standardise() {
        for (final CollectionStats collectionStat : collectionStats) {
            final double[] standardised = collectionStat.standardise();
            double standardisedMean = Arrays.stream(standardised).summaryStatistics().getAverage();
            double standardisedStdDev = new StandardDeviation().evaluate(standardised);
            assertThat(Math.abs(standardisedMean) < 0.001).isTrue();
            assertThat(Math.abs(standardisedStdDev - 0.5) < 0.001).isTrue();
        }
    }

    @Test
    public void denormalise() {
        for (final CollectionStats collectionStat : collectionStats) {
            for (final double normalised : collectionStat.normalise()) {
                final double denormalised = collectionStat.denormalise(normalised);
                boolean contained = false;
                for (final double value : vals) {
                    if (Math.abs(value - denormalised) < significance) {
                        contained = true;
                        break;
                    }
                }
                assertThat(contained).isTrue();
            }
        }
    }

    @Test
    public void destandardise() {
        for (final CollectionStats collectionStat : collectionStats) {
            for (final double normalised : collectionStat.standardise()) {
                final double destandardised = collectionStat.destandardise(normalised);
                boolean contained = false;
                for (final double value : vals) {
                    if (Math.abs(value - destandardised) <= significance) {
                        contained = true;
                        break;
                    }
                }
                assertThat(contained).isTrue();
            }
        }
    }

//    @Test
//    public void manualCheck() {
//        final double[] values = new double[]{-0.00002, 0.00032, 0.00028};
//        final CollectionStats collectionStat = new DoubleCollectionStats(values);
//        final double expectedMean = 0.000193333;
//        assertThat(collectionStat.mean()).isEqualTo(expectedMean);
//        final double expectedRange = 0.00034;
//        assertThat(collectionStat.max() - collectionStat.min()).isEqualTo(expectedRange);
//        final double vtM = -0.000213333;
//        final double[] expectedNormalised = new double[]{-1.2549, 0, 0};
//        assertThat(collectionStat.normalise()[0]).isEqualTo(expectedNormalised[0]);
//    }
}