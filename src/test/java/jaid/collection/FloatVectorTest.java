package jaid.collection;

import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static jaid.number.Maths.INTEGER_RANGE;
import static org.assertj.core.api.Assertions.assertThat;

public class FloatVectorTest {

    @Test
    public void testSimBucketBits() {
        // Testing different bits sizes.
        for (int bits = 1; bits <= 8; bits++) {
            Set<Integer> uniqueBuckets = new HashSet<>();
            int numberOfBuckets = (int) Math.pow(2, bits);
            long rangePerBucket = INTEGER_RANGE / numberOfBuckets;
            for (int i = 0; i < numberOfBuckets; i++) {
                // Pick the midpoint in the current range segment for the bucket.
                long midpoint = (long) Integer.MIN_VALUE + rangePerBucket * i + rangePerBucket / 2;
                // Ensure the midpoint is not out of the int range due to rounding on the edges.
                if (i == numberOfBuckets - 1) {
                    midpoint = Integer.MAX_VALUE;
                } else if (midpoint > Integer.MAX_VALUE) {
                    // Correct overflow by mirroring back into range.
                    midpoint = Integer.MAX_VALUE - (midpoint - Integer.MAX_VALUE);
                }
                // Cast midpoint to int, since we've taken care of overflows above.
                int hashValue = (int) midpoint;
                // Create a FloatVector instance and test simBucket for current bits, should map to i-th bucket.
                FloatVector testVector = new FloatVector(new float[0]);
                uniqueBuckets.add(testVector.simBucket(bits, hashValue));
            }
            // Verify we have the correct number of unique buckets for the bits size.
            assertThat(uniqueBuckets).hasSize(numberOfBuckets);
        }
    }
}