/*
 * (c)
 */
package jaid.number;

import org.junit.jupiter.api.Test;

import java.util.NavigableMap;
import java.util.TreeMap;

import static org.junit.Assert.assertEquals;

/**
 * Unit test for {@link GeometricRandom}.
 */
public class GeometricRandomTest {
    @Test
    public void nextByte() {
        final NavigableMap<Integer, Integer> out = generateNRandoms(10000000, 10000001);

        final int highestValue = out.lastKey();
        assertEquals("Should have around 25 levels", 25, highestValue, 10);
        // Skip the sparse levels since there is not enough samples to draw a conclusion.
        int lastLevel = 11;
        for (int level = 10; level >= 0; level--) {
            assertEquals("Level " + level + " should contain around twice as many results as its predecessor",
                    2, (double)out.get(level) / out.get(lastLevel), 0.4);
            lastLevel = level;
        }
    }

    @Test
    public void nextByteWithReseed() {
        final NavigableMap<Integer, Integer> out = generateNRandoms(10000000, 1000);

        final int highestValue = out.lastKey();
        assertEquals("Should have around 25 levels", 25, highestValue, 10);
        // Skip the sparse levels since there is not enough samples to draw a conclusion.
        int lastLevel = 11;
        for (int level = 10; level >= 0; level--) {
            assertEquals("Level " + level + " should contain around twice as many results as its predecessor",
                    2, (double)out.get(level) / out.get(lastLevel), 0.4);
            lastLevel = level;
        }
    }

    private NavigableMap<Integer, Integer> generateNRandoms(final int numRuns, final int reseedEvery) {
        final GeometricRandom gen = new GeometricRandom();
        final NavigableMap<Integer, Integer> out = new TreeMap<>();
        for (int run = 0; run < numRuns; run++) {
            final int result = gen.nextByte();
            if (run % reseedEvery == 0) {
                gen.injectEntropy();
            }
            Integer curNum = out.get(result);
            if (curNum == null) {
                curNum = 0;
            }
            curNum++;
            out.put(result, curNum);
        }
        return out;
    }
}

