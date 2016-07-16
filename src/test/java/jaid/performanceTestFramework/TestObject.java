package jaid.performanceTestFramework;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 */
public class TestObject implements Comparable<TestObject> {
    static Random rand = new Random();
    static Set<Integer> alreadyAssignedIds = new HashSet<Integer>();
    // Randomly sized payload to simulate data in collection
    ArrayList<Object> payload;
    Integer uid;
    public TestObject() {
        // Assign an unused id to this impl. Brute force approach will slow down construction as more ids are allocated.
        for (uid = rand.nextInt(); !alreadyAssignedIds.add(uid); uid = rand.nextInt());
        int payloadSize = (int)Math.rint(rand.nextDouble() * 5);
        payload = new ArrayList<Object>(payloadSize);
        for (int i = 0; i < payloadSize; i++) {
            payload.add(new Object());
        }
    }
    @Override
    public boolean equals(Object o) {
        return uid.equals(((TestObject)o).uid);
    }
    @Override
    public int hashCode() {
        return uid;
    }
    @Override
    public int compareTo(TestObject o) {
        return uid.compareTo(o.uid);
    }
}
