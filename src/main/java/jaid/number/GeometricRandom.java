/*
 * (c)
 */
package jaid.number;

import java.security.SecureRandom;
import java.util.Random;

/**
 * Fast, threadsafe, low quality random values drawn from a geometric distribution with success probability 0.5.
 */
public class GeometricRandom {

    /**
     * Source of entropy for this instance. Created using 64+ bits of information via {@link SecureRandom}, updated
     * using an XORShift algorithm prior to each use, and allows more entropy to be injected via {@link #injectEntropy}.
     * There is no concurrency protection for writes which could result in word tearing, and updates are not published
     * between threads, both of which may reduce the apparent global entropy when an instance is shared between threads.
     */
    private long pool;

    /**
     * Construct using SecureRandom to obtain initial entropy, this uses /dev/random for seed information on linux
     * platforms which may block here until sufficient entropy is available.
     */
    public GeometricRandom() {
        final Random r = new SecureRandom();
        // Ensure we never seed 0.
        while (pool == 0) {
            pool = r.nextLong();
        }
    }

    /**
     * @return a positive random byte in the range [0, 32) corresponding to the number of successful trials in a
     * p=0.5 geometric distribution (expect 0+ 100% of the time, 1+ 50% of the time, 2+ 25% of the time...).
     */
    public byte nextByte() {
        // XORShift the pool value to create a random int for this method.
        pool ^= (pool << 21);
        pool ^= (pool >>> 35);
        pool ^= (pool << 4);
        // Uses the number of consecutive lower order 0 bits in pool, from the penultimate bit to 32. Note that
        // XORShift never produces 0, so the maximum value will be 31.
        final int value = (int) pool;
        return (byte) Integer.numberOfTrailingZeros(value);
    }

    /**
     * Calling this periodically helps avoid cycles of the same results from {@link #nextByte}.
     */
    public void injectEntropy() {
        do {
            pool ^= System.nanoTime();
        } while (pool == 0);
    }
}


