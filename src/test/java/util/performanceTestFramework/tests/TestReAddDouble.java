package util.performanceTestFramework.tests;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import util.performanceTestFramework.TestFunctions;
import util.performanceTestFramework.TestObject;

/**
 */
public class TestReAddDouble extends TestFunctions {
    final int ADD_UNTIL = 1024;
    List<Collection<TestObject>> removeCollections;
    List<Collection<TestObject>> addCollections;
    @Override
    public Runnable setUp() {
        return new Runnable() {
            @Override
            public void run() {
                TestReAddDouble.super.setUp().run();
                // Create collections of size range [1,ADD_UNTIL) here rather than in measure loop
                removeCollections = new ArrayList<Collection<TestObject>>();
                for (int i = 1; i < ADD_UNTIL; i = i * 2) {
                    removeCollections.add(rangeClosedOpen(0, i));
                }
                // Create collections of size range [1,ADD_UNTIL] here rather than in measure loop
                addCollections = new ArrayList<Collection<TestObject>>();
                for (int i = 2; i <= ADD_UNTIL; i = i * 2) {
                    addCollections.add(rangeClosedOpen(0, i));
                }
            }
        };
    }
    @Override
    public Runnable run() {
        return new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < removeCollections.size(); i++) {
                    toTest.removeAll(removeCollections.get(i));
                    toTest.addAll(addCollections.get(i));
                }
            }
        };
    }
    @Override
    public String toString() {
        return "removeAll() last half, then addAll() double amount removed, until " + ADD_UNTIL;
    }
}
