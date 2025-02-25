package jaid.collection;


import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import static java.util.stream.DoubleStream.generate;

/**
 * Run in gradle with ./gradlew jmh
 * Any results in the comments are from an AMD Ryzen 7 6800H laptop.
 * Benchmark                                                       Mode  Cnt      Score     Error  Units
 * DoublesVectorPerformanceTest.normalisedDotProductAll            avgt   15   2384.509 ´┐¢  61.092  ns/op
 * DoublesVectorPerformanceTest.normalisedDotProductSmall          avgt   15      5.659 ´┐¢   0.192  ns/op
 * DoublesVectorPerformanceTest.normalisedFmaDotProductAll         avgt   15  17439.165 ´┐¢ 137.191  ns/op
 * DoublesVectorPerformanceTest.normalisedFmaDotProductSmall       avgt   15      4.589 ´┐¢   0.019  ns/op
 * DoublesVectorPerformanceTest.normalisedMultiplyDotProductAll    avgt   15  13389.219 ´┐¢ 911.029  ns/op
 * DoublesVectorPerformanceTest.normalisedMultiplyDotProductSmall  avgt   15      5.120 ´┐¢   0.385  ns/op
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

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    public void normalisedDotProductSmall(TestState testState, Blackhole resultConsumer) {
        resultConsumer.consume(testState.normalisedFirst[0].dotProduct(testState.normalisedSecond[0]));
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    public void normalisedDotProductAll(TestState testState, Blackhole resultConsumer) {
        for (int i = 0; i < testState.vectorSizes.length; i++) {
            resultConsumer.consume(testState.normalisedFirst[i].dotProduct(testState.normalisedSecond[i]));

        }
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    public void normalisedFmaDotProductSmall(TestState testState, Blackhole resultConsumer) {
        resultConsumer.consume(testState.normalisedFirstFma[0].dotProduct(testState.normalisedSecondFma[0]));
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    public void normalisedFmaDotProductAll(TestState testState, Blackhole resultConsumer) {
        for (int i = 0; i < testState.vectorSizes.length; i++) {
            resultConsumer.consume(testState.normalisedFirstFma[i].dotProduct(testState.normalisedSecondFma[i]));

        }
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    public void normalisedMultiplyDotProductSmall(TestState testState, Blackhole resultConsumer) {
        resultConsumer.consume(testState.normalisedFirstMultiply[0].dotProduct(testState.normalisedSecondMultiply[0]));
    }

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

    /**
     * Used to performance test the multiply dot product performance
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
}

