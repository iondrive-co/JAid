package jaid.collection;


import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

/**
 * Any results in the comments are from an AMD Ryzen 7 6800H laptop.
 * Run in gradle with ./gradlew jmh
 */
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class IntSkipListPerformanceTest {

    @State(Scope.Thread)
    public static class TestState {

        private IntSkipList intSkipList;
        private int[] values;

        @Setup(Level.Trial)
        public void trialSetup() {
            intSkipList = new IntSkipList();
            values = IntStream.range(0, 10_000).toArray();
            for (final int value: values) {
                intSkipList.add(value);
            }
        }

        @Setup(Level.Iteration)
        public void iterationSetup() {
            // Not needed for contains but we might use this later
        }

        @TearDown(Level.Iteration)
        public void iterationTeardown() {
            // Not needed for contains but we might use this later
        }
    }

    /**
     * Benchmark                            Mode  Cnt   Score   Error  Units
     * IntSkipListPerformanceTest.contains  avgt    5  20.458 � 0.936  ns/op
     */
    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    public void contains(TestState testState, Blackhole resultConsumer) {
        resultConsumer.consume(testState.intSkipList.contains(5_000));
    }

    /**
     * To run from IntelliJ the jmh plugin must be installed first.
     */
    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder().forks(0)
                .include(IntSkipListPerformanceTest.class.getSimpleName())
                .build();
        new Runner(opt).run();
    }
}

