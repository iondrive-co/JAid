package jaid.number;

import jaid.collection.DoubleVector;
import jaid.collection.FloatVector;
import jaid.collection.IVector;
import org.junit.After;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static jaid.collection.IVectorTestUtil.generateFixedVector;
import static jaid.collection.IVectorTestUtil.generateRandomVector;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NearestVectorStoreTest {

    private static final Random RANDOM = new Random(4444);
    private static final int LOWER_THRESHOLD = 5;
    private static final byte LOWER_SECTION_BITS = 0;
    private static final int UPPER_THRESHOLD = 10;
    private static final byte MIDDLE_SECTION_BITS = 4;
    private static final byte UPPER_SECTION_BITS = 16;
    private static final Map<Integer, Byte> THRESHOLDS = Map.of(LOWER_THRESHOLD, LOWER_SECTION_BITS,
            UPPER_THRESHOLD, MIDDLE_SECTION_BITS, Integer.MAX_VALUE, UPPER_SECTION_BITS);

    private final NearestVectorStore store = new NearestVectorStore(THRESHOLDS);

    @After
    void after() {
        store.clear();
    }

    @Test
    void testGetIdentical() {
        FloatVector v1 = new FloatVector(new float[]{-0.1f, -0.2f, 0.3f}).normalize();
        FloatVector v2 = new FloatVector(new float[]{-0.4f, 0.5f, -0.6f}).normalize();
        FloatVector v3 = new FloatVector(new float[]{-0.7f, -0.8f, 0.9f}).normalize();
        FloatVector queryVector = new FloatVector(new float[]{-0.1f, -0.2f, 0.3f}).normalize();

        store.add(v1);
        store.add(v2);
        store.add(v3);

        List<IVector> results = store.query(queryVector, 1);

        // Check if the identical vector is returned
        assertEquals(1, results.size());
        assertSame(v1, results.get(0));
    }

    @Test
    void testAdjacentVectorsReturned() {
        FloatVector v1 = new FloatVector(new float[]{0.1f, 0.2f, 0.3f}).normalize();
        FloatVector v2 = new FloatVector(new float[]{0.6f, 0.7f, 0.8f}).normalize();
        FloatVector v3 = v2.plus(new FloatVector(new float[]{0.001f, 0.001f, 0.001f})).normalize();
        FloatVector queryVector = v2.plus(new FloatVector(new float[]{0.0005f, 0.0005f, 0.0005f})).normalize();

        store.add(v1);
        store.add(v2);
        store.add(v3);

        List<IVector> results = store.query(queryVector, 2);

        // Check if two closest vectors are returned
        Assertions.assertEquals(2, results.size());
        assertTrue(results.contains(v2));
        assertTrue(results.contains(v3));
    }

    @Test
    void queryDistribution() {
        for (int vectorDims = 3; vectorDims < 200; vectorDims++) {
            final FloatVector v1 = generateFixedVector(vectorDims, -0.7f);
            final FloatVector v2 = v1.plus(generateFixedVector(vectorDims, 0.001f));
            final FloatVector v3 = v2.minus(generateFixedVector(vectorDims, 0.1f));
            final FloatVector v4 = v3.minus(generateFixedVector(vectorDims, 0.001f));
            final FloatVector queryVector = v4.minus(generateFixedVector(vectorDims, 0.001f));
            final Map<String, Integer> histogram = new HashMap<>();
            for (int run = 0; run < 1000; run++) {
                // Test with a lot of buckets
                final NearestVectorStore store = new NearestVectorStore(Map.of(Integer.MAX_VALUE, (byte)8));
                store.add(v1);
                store.add(v2);
                store.add(v3);
                store.add(v4);
                final List<IVector> neighbors = store.query(queryVector, 2);
                assertThat(neighbors).hasSize(2);
                for (final IVector neighbor : neighbors) {
                    String key = neighbor.toString();
                    histogram.put(key, histogram.getOrDefault(key, 0) + 1);
                }
            }
            // After 1000 runs, we expect to see v3 and v4 more often than v1 and v2 for the query.
            assertTrue(histogram.getOrDefault(v3.toString(), 0) >
                    histogram.getOrDefault(v1.toString(), 0), "Failed with dims " + vectorDims);
            assertTrue(histogram.getOrDefault(v4.toString(), 0) >
                    histogram.getOrDefault(v2.toString(), 0), "Failed with dims " + vectorDims);
        }
    }

    @Test
    public void addAndRemove() {
        IVector vector = new DoubleVector(new double[]{-1, 0, 0.5, 2});
        NearestVectorStore store = new NearestVectorStore();

        store.add(vector);

        // Verify vector is added
        assertTrue(store.query(vector, 1).contains(vector));

        // Now remove the vector
        assertTrue(store.remove(vector));

        // Verify the vector is no longer in the store
        assertFalse(store.query(vector, 1).contains(vector));

        // Ensure removal returns false if the vector is not found
        assertFalse(store.remove(vector));
    }

    @Test
    void testDynamicUniversePowerChange() {
        assertThat(store.getBucketSizeExponent()).isEqualTo(LOWER_SECTION_BITS);

        // Add vectors to reach the threshold for bucket power increase
        for (int i = 0; i < LOWER_THRESHOLD; i++) {
            store.add(generateRandomVector(10, RANDOM));
        }
        assertThat(store.getBucketSizeExponent()).isEqualTo(MIDDLE_SECTION_BITS);

        FloatVector queryVector = generateRandomVector(10, RANDOM);
        store.add(queryVector);
        List<IVector> results = store.query(queryVector, 1);
        assertThat(results).contains(queryVector);

        for (int i = 0; i < UPPER_THRESHOLD; i++) {
            store.add(generateRandomVector(10, RANDOM));
        }
        assertThat(store.getBucketSizeExponent()).isEqualTo(UPPER_SECTION_BITS);

        store.clear();
        assertThat(store.getBucketSizeExponent()).isEqualTo((byte)0);
  }
}