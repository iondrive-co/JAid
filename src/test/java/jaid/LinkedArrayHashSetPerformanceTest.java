package jaid;

import java.util.*;
import java.util.concurrent.Callable;

import jaid.collection.LinkedArrayHashSet;
import jaid.performanceTestFramework.ICollectionToTest;
import jaid.performanceTestFramework.ResultSet;
import jaid.performanceTestFramework.TestFunctions;
import jaid.performanceTestFramework.TestObject;
import jaid.performanceTestFramework.tests.TestContainsAllToArray;
import jaid.performanceTestFramework.tests.TestIteration;
import jaid.performanceTestFramework.tests.TestReAddDouble;

/**
 * Runs a couple of simple synthetic tests.
 * It is recommended to specify the following params and check the compilation and gc logs are sane:
 * large XMX=MXS, -server -Xbatch -XX:CICompilerCount=1 -XX:+PrintCompilation, -verbose:gc
 */
public class LinkedArrayHashSetPerformanceTest {
    // These take about 50 hours on 2013 medium spec hardware, scale appropriately
    private static final int SIZE_TEST_DATA = 5000;
    private static final int NUM_LOOPS_PER_RUN = 1000;

    public static void main(String... args) throws Exception {
        final List<TestObject> testData = TestFunctions.rangeClosedOpen(0, SIZE_TEST_DATA);
        // Store test cases in the order they are defined in testCases
        final Map<String, Set<TestFunctions>> testNameToFns = new LinkedHashMap<String, Set<TestFunctions>>();
        for (final ICollectionToTest toTest: typesToTest) {
            for (final Callable<TestFunctions> testCase: testCases) {
                TestFunctions testFns = testCase.call();
                testFns.init(toTest, testData);
                Set<TestFunctions> existing = testNameToFns.get(testFns.toString());
                if (existing == null) {
                    // Store test functions in the order defined in typesToTest
                    existing = new LinkedHashSet<TestFunctions>();
                    testNameToFns.put(testFns.toString(), existing);
                }
                existing.add(testFns);
            }
        }
        System.out.println("Starting test");
        compare(testNameToFns);
    }

    public static void compare(Map<String, Set<TestFunctions>> toProf) throws Exception {
        long deadStore = 0;
        for (Map.Entry<String, Set<TestFunctions>> testGroup: toProf.entrySet()) {
            System.out.println(testGroup.getKey() + ":");
            System.out.println("     uSecs: | Median | 90%tile | 99%tile |");
            for (TestFunctions fn: testGroup.getValue()) {
                ResultSet results = measure(fn);
                System.out.format(" %10s | %6d | %7d | %7d |", fn.createFn, results.getUSecMedian(),
                                  results.getUSec90thPercentile(), results.getUSec99thPercentile());
                deadStore += fn.deadStore;
                System.out.println();
            }
        }
        System.out.println();
        System.out.println("Dead store output (ignore): " + deadStore);
    }

    public static ResultSet measure(TestFunctions toMeasure) throws Exception {
        // Discard first half of results generated while cache is warming up
        double idxEndWarmUp = NUM_LOOPS_PER_RUN / 2.0;
        ResultSet results = new ResultSet((int)idxEndWarmUp);
        for (int j = 0; j < NUM_LOOPS_PER_RUN; j++) {
            toMeasure.setUp().run();
            long startTime = System.nanoTime();
            toMeasure.run().run();
            long endTime = System.nanoTime();
            toMeasure.tearDown().run();
            // Write every result so no difference between warm up and actual, but discard writes from first half
            double maskFirstHalf = j / idxEndWarmUp;
            int writePos = Math.min(Math.max(0, (int) Math.round(maskFirstHalf * (j - idxEndWarmUp))),
                                    results.uSecTimes.length - 1);
            long nanoDif = endTime - startTime;
            results.uSecTimes[writePos] = (float)(nanoDif / 1000.0);
        }
        return results;
    }

    private static ICollectionToTest[] typesToTest = {
            // Always run the array list test twice to see how repeatable the results are
            new ICollectionToTest<ArrayList>() {
                @Override
                public ArrayList createNewCollection() {
                    return new ArrayList<TestObject>();
                }
                public String toString() {
                    return "ErrorBar";
                }
            },
            new ICollectionToTest<ArrayList>() {
                @Override
                public ArrayList createNewCollection() {
                    return new ArrayList<TestObject>();
                }
                public String toString() {
                    return "ArrayList";
                }
            },
            new ICollectionToTest<LinkedArrayHashSet>() {
                @Override
                public LinkedArrayHashSet createNewCollection() {
                    return new LinkedArrayHashSet<TestObject>();
                }
                public String toString() {
                    return "LArrayHSet";
                }
            },
            new ICollectionToTest<LinkedList>() {
                @Override
                public LinkedList createNewCollection() {
                    return new LinkedList<TestObject>();
                }
                public String toString() {
                    return "LinkedList";
                }
            },
            new ICollectionToTest<HashSet>() {
                @Override
                public HashSet createNewCollection() {
                    return new HashSet<TestObject>();
                }
                public String toString() {
                    return "HashSet";
                }
            },
            new ICollectionToTest<LinkedHashSet>() {
                @Override
                public LinkedHashSet createNewCollection() {
                    return new LinkedHashSet<TestObject>();
                }
                public String toString() {
                    return "LkdHashSet";
                }
            },
            new ICollectionToTest<TreeSet>() {
                @Override
                public TreeSet createNewCollection() {
                    return new TreeSet<TestObject>();
                }
                public String toString() {
                    return "TreeSet";
                }
            }
    };

    private static Callable[] testCases = new Callable[]{
            new Callable<TestFunctions>(){
                public TestFunctions call() throws Exception {
                    return new TestReAddDouble();
                }
            },
            new Callable<TestFunctions>(){
                public TestFunctions call() throws Exception {
                    return new TestIteration();
                }
            },
            new Callable<TestFunctions>(){
                public TestFunctions call() throws Exception {
                    return new TestContainsAllToArray();
                }
            }
    };
}
