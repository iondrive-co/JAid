package utils.performanceTestFramework.tests;

import utils.performanceTestFramework.TestFunctions;
import utils.performanceTestFramework.TestObject;

import java.util.Arrays;

/**
 */
public class TestContainsAllToArray extends TestFunctions {
    @Override
    public Runnable setUp() {
        return new Runnable() {
            @Override
            public void run() {
                TestContainsAllToArray.super.setUp().run();
                for(TestObject testItem: testData) {
                    toTest.add(testItem);
                }
            }
        };
    }
    @Override
    public Runnable run() {
        return new Runnable() {
            @Override
            public void run() {
                deadStore += (toTest.containsAll(Arrays.asList(toTest.toArray()))) ? 1 : 0;
            }
        };
    }
    @Override
    public String toString() {
        return "Test containsAll() wrapping the results of this collection (of size " +
                testData.size() + ")`s toArray() method";
    }
}
