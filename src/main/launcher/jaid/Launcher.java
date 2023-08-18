package jaid;

import jaid.collection.Tuples;

/**
 * This class currently only exists to test the jlink bundling in the build. The plan is to turn this into a full blown
 * launcher framework in the future.
 */
public class Launcher {

    public static void main(final String... args) {
        final Tuples.IntIntPair testPair = new Tuples.IntIntPair(1, 2);
        System.out.println(testPair);
    }
}
