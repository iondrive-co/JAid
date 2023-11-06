package jaid.collection;

import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class FloatVectorTest {

    class MockFloatVector implements IVector {
        private int[] presetHashCounts;

        public MockFloatVector(int[] presetHashCounts) {
            this.presetHashCounts = presetHashCounts;
        }

        @Override
        public double dotProduct(IVector comparedTo) {
            return 0;
        }

        @Override
        public <T extends IVector> T minus(T operand) {
            return null;
        }

        @Override
        public <T extends IVector> T normalize() {
            return null;
        }

        @Override
        public <T extends IVector> T plus(T operand) {
            return null;
        }

        @Override
        public int[] simHashCounts() {
            // Return the preset hash counts for testing
            return this.presetHashCounts;
        }
    }

    @Test
    public void testSimBucketRange() {
        int maxPow = 5;
        for (int pow = 1; pow <= maxPow; pow++) {
            int segments = 1 << pow;
            int segmentSize = 32 / segments;
            int[] hashCounts = new int[32];

            // Generate hashCounts that produce a unique sum for each segment
            for (int i = 0; i < segments; i++) {
                // Set the hash count for each segment to guarantee the expected bucket outcome
                for (int j = 0; j < segmentSize; j++) {
                    hashCounts[i * segmentSize + j] = (i % 2 == 0) ? 1 : -1; // Alternate to ensure distribution across buckets
                }
            }
            // Test the expected number of unique buckets with these counts
            MockFloatVector mockVector = new MockFloatVector(hashCounts);
            Set<Integer> buckets = new HashSet<>();
            for (int i = 0; i < segments; i++) {
                // Set one index in each segment to flip the simulated hash count, influencing the bucket result
                for (int j = 0; j < segmentSize; j++) {
                    hashCounts[i * segmentSize + j] *= -1;
                }
                buckets.add(mockVector.simBucket(pow));
                // Reset for the next iteration
                for (int j = 0; j < segmentSize; j++) {
                    hashCounts[i * segmentSize + j] *= -1;
                }
            }
            assertThat(buckets.size())
                    .as("Expected %d unique buckets for power %d, but got %d.", segments, pow, buckets.size())
                    .isEqualTo(segments);
        }
    }
}