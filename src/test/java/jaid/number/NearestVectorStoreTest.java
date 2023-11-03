package jaid.number;

import jaid.collection.DoubleVector;
import jaid.collection.FloatVector;
import jaid.collection.IVector;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static jaid.collection.IVectorTestUtil.generateFixedVector;
import static jaid.collection.IVectorTestUtil.generateRandomVector;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NearestVectorStoreTest {

    @Test
    void testGetIdentical() {
        NearestVectorStore store = new NearestVectorStore();

        FloatVector v1 = new FloatVector(new float[]{-0.1f, -0.2f, 0.3f});
        FloatVector v2 = new FloatVector(new float[]{-0.4f, 0.5f, -0.6f});
        FloatVector v3 = new FloatVector(new float[]{-0.7f, -0.8f, 0.9f});
        FloatVector queryVector = new FloatVector(new float[]{-0.1f, -0.2f, 0.3f});

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
        NearestVectorStore store = new NearestVectorStore();

        FloatVector v1 = new FloatVector(new float[]{0.1f, 0.2f, 0.3f});
        FloatVector v2 = new FloatVector(new float[]{0.4f, 0.5f, 0.6f});
        FloatVector v3 = new FloatVector(new float[]{0.7f, 0.8f, 0.9f});
        FloatVector queryVector = new FloatVector(new float[]{0.65f, 0.75f, 0.85f});

        store.add(v1);
        store.add(v2);
        store.add(v3);

        List<IVector> results = store.query(queryVector, 2);

        // Check if two closest vectors are returned
        assertEquals(2, results.size());
        assertTrue(results.contains(v2));
        assertTrue(results.contains(v3));
    }

    @Test
    void queryDistribution() {
        final Random random = new Random(4444);
        for (int vectorDims = 5; vectorDims < 8; vectorDims++) {
            final FloatVector v1 = generateRandomVector(vectorDims, random);
            final FloatVector v2 = generateRandomVector(vectorDims, random);
            final FloatVector v3 = generateRandomVector(vectorDims, random);
            final FloatVector v4 = v3.minus(generateFixedVector(vectorDims, 0.001f));
            final Map<String, Integer> histogram = new HashMap<>();
            for (int run = 0; run < 1000; run++) {
                final NearestVectorStore store = new NearestVectorStore();
                store.add(v1);
                store.add(v2);
                store.add(v3);
                store.add(v4);
                final FloatVector queryVector = v4.minus(generateFixedVector(vectorDims, 0.001f));
                final List<IVector> neighbors = store.query(queryVector, 2);
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
}