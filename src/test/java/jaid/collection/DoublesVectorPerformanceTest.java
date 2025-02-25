package jaid.collection;


import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

import static java.util.stream.DoubleStream.generate;

/**
 * Any results in the comments are from an AMD Ryzen 7 6800H laptop.
 * Run in gradle with ./gradlew jmh
 */
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class DoublesVectorPerformanceTest {

    @State(Scope.Thread)
    public static class TestState {

        final Random random = new Random(42);
        final int[] vectorSizes = new int[]{8, 65, 256, 768, 2048, 16384};
        final DoublesVector[] normalisedFirst = new DoublesVector[vectorSizes.length];
        final DoublesVector[] normalisedSecond = new DoublesVector[vectorSizes.length];
        final FmaIntrinsicDoublesVector[] normalisedFirstFma = new FmaIntrinsicDoublesVector[vectorSizes.length];
        final FmaIntrinsicDoublesVector[] normalisedSecondFma = new FmaIntrinsicDoublesVector[vectorSizes.length];
        final MultipleDoublesVector[] normalisedFirstMultiply = new MultipleDoublesVector[vectorSizes.length];
        final MultipleDoublesVector[] normalisedSecondMultiply = new MultipleDoublesVector[vectorSizes.length];

        @Setup(Level.Trial)
        public void trialSetup() {
            for (int i = 0; i < vectorSizes.length; i++) {
                normalisedFirst[i] = new DoublesVector(generate(() -> random.nextDouble()).limit(vectorSizes[i]).toArray());
                normalisedSecond[i] = new DoublesVector(generate(() -> random.nextDouble()).limit(vectorSizes[i]).toArray());
                normalisedFirstFma[i] = new FmaIntrinsicDoublesVector(generate(() -> random.nextDouble()).limit(vectorSizes[i]).toArray());
                normalisedSecondFma[i] = new FmaIntrinsicDoublesVector(generate(() -> random.nextDouble()).limit(vectorSizes[i]).toArray());
                normalisedFirstMultiply[i] = new MultipleDoublesVector(generate(() -> random.nextDouble()).limit(vectorSizes[i]).toArray());
                normalisedSecondMultiply[i] = new MultipleDoublesVector(generate(() -> random.nextDouble()).limit(vectorSizes[i]).toArray());
            }
        }
    }

    /**
     * Result "jaid.collection.DoublesVectorPerformanceTest.normalisedDotProductSmall":
     *   5.698 ´┐¢(99.9%) 0.171 ns/op [Average]
     *   (min, avg, max) = (5.477, 5.698, 5.896), stdev = 0.160
     *   CI (99.9%): [5.527, 5.870] (assumes normal distribution)
     */
    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    public void normalisedDotProductSmall(TestState testState, Blackhole resultConsumer) {
        resultConsumer.consume(testState.normalisedFirst[0].dotProduct(testState.normalisedSecond[0]));
    }

    /**
     * Result "jaid.collection.DoublesVectorPerformanceTest.normalisedDotProductAll":
     *   2381.113 ´┐¢(99.9%) 28.970 ns/op [Average]
     *   (min, avg, max) = (2301.166, 2381.113, 2445.583), stdev = 38.674
     *   CI (99.9%): [2352.143, 2410.082] (assumes normal distribution)
     */
    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    public void normalisedDotProductAll(TestState testState, Blackhole resultConsumer) {
        for (int i = 0; i < testState.vectorSizes.length; i++) {
            resultConsumer.consume(testState.normalisedFirst[i].dotProduct(testState.normalisedSecond[i]));

        }
    }

    /**
     * Result "jaid.collection.DoublesVectorPerformanceTest.normalisedDotProductSmall":
     *   4.614 ´┐¢(99.9%) 0.021 ns/op [Average]
     *   (min, avg, max) = (4.574, 4.614, 4.649), stdev = 0.020
     *   CI (99.9%): [4.593, 4.636] (assumes normal distribution)
     */
    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    public void normalisedFmaDotProductSmall(TestState testState, Blackhole resultConsumer) {
        resultConsumer.consume(testState.normalisedFirstFma[0].dotProduct(testState.normalisedSecondFma[0]));
    }

    /**
     * Result "jaid.collection.DoublesVectorPerformanceTest.normalisedDotProductAll":
     *   17437.131 ´┐¢(99.9%) 64.608 ns/op [Average]
     *   (min, avg, max) = (17330.221, 17437.131, 17549.885), stdev = 60.435
     *   CI (99.9%): [17372.522, 17501.739] (assumes normal distribution)
     */
    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    public void normalisedFmaDotProductAll(TestState testState, Blackhole resultConsumer) {
        for (int i = 0; i < testState.vectorSizes.length; i++) {
            resultConsumer.consume(testState.normalisedFirstFma[i].dotProduct(testState.normalisedSecondFma[i]));

        }
    }

    /**
     * Result "jaid.collection.DoublesVectorPerformanceTest.normalisedMultiplyDotProductSmall":
     *   5.258 ´┐¢(99.9%) 0.424 ns/op [Average]
     *   (min, avg, max) = (4.678, 5.258, 5.665), stdev = 0.397
     *   CI (99.9%): [4.833, 5.682] (assumes normal distribution)
     */
    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    public void normalisedMultiplyDotProductSmall(TestState testState, Blackhole resultConsumer) {
        resultConsumer.consume(testState.normalisedFirstMultiply[0].dotProduct(testState.normalisedSecondMultiply[0]));
    }

    /**
     * Result "jaid.collection.DoublesVectorPerformanceTest.normalisedMultiplyDotProductAll":
     *   13561.563 ´┐¢(99.9%) 247.475 ns/op [Average]
     *   (min, avg, max) = (13177.615, 13561.563, 14023.692), stdev = 231.488
     *   CI (99.9%): [13314.088, 13809.038] (assumes normal distribution)
     */
    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    public void normalisedMultiplyDotProductAll(TestState testState, Blackhole resultConsumer) {
        for (int i = 0; i < testState.vectorSizes.length; i++) {
            resultConsumer.consume(testState.normalisedFirstMultiply[i].dotProduct(testState.normalisedSecondMultiply[i]));

        }
    }

    /**
     * To run from IntelliJ the jmh plugin must be installed first.
     */
    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder().forks(0)
                .include(DoublesVectorPerformanceTest.class.getSimpleName())
                .build();
        new Runner(opt).run();
    }
}

