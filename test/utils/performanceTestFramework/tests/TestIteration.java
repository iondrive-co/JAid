package utils.performanceTestFramework.tests;

import utils.performanceTestFramework.TestFunctions;
import utils.performanceTestFramework.TestObject;

/**
 */
public class TestIteration extends TestFunctions {
    @Override
    public Runnable setUp() {
        return new Runnable() {
            @Override
            public void run() {
                TestIteration.super.setUp().run();
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
                for (TestObject data : toTest) {
                    deadStore += data.hashCode();
                }
            }
        };
    }
    @Override
    public String toString() {
        return "Test iterating through " + testData.size() + " items";
    }
}
