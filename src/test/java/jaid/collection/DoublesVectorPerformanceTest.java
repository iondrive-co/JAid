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

        @Setup(Level.Trial)
        public void trialSetup() {
            for (int i = 0; i < vectorSizes.length; i++) {
                normalisedFirst[i] = new DoublesVector(generate(() -> random.nextDouble()).limit(vectorSizes[i]).toArray());
                normalisedSecond[i] = new DoublesVector(generate(() -> random.nextDouble()).limit(vectorSizes[i]).toArray());
            }
        }
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
     * To run from IntelliJ the jmh plugin must be installed first.
     */
    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder().forks(0)
                .include(DoublesVectorPerformanceTest.class.getSimpleName())
                .build();
        new Runner(opt).run();
    }
}

