package jaid.number;

import jaid.collection.FloatVector;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

class LocalitySensitiveHasherTest {

    @Test
    void query() {
        Map<String, Integer> histogram = new HashMap<>();
        FloatVector v1 = new FloatVector(new float[]{0.1f, 0.2f, 0.3f});
        FloatVector v2 = new FloatVector(new float[]{0.4f, 0.5f, 0.6f});
        FloatVector v3 = new FloatVector(new float[]{0.7f, 0.8f, 0.9f});
        FloatVector v4 = new FloatVector(new float[]{0.9f, 0.8f, 0.7f});
        for (int run = 0; run < 1000; run++) {
            LocalitySensitiveHasher lsh = new LocalitySensitiveHasher(10, 3);
            lsh.addVector(v1);
            lsh.addVector(v2);
            lsh.addVector(v3);
            lsh.addVector(v4);
            FloatVector queryVector = new FloatVector(new float[]{0.9f, 0.8f, 0.7f});
            List<FloatVector> neighbors = lsh.query(queryVector, 2);
            for (FloatVector neighbor : neighbors) {
                String key = neighbor.toString();
                histogram.put(key, histogram.getOrDefault(key, 0) + 1);
            }
        }
        // After 1000 runs, we expect to see v3 and v4 more often than v1 and v2 for the query.
        assertTrue(histogram.getOrDefault(v3.toString(), 0) > histogram.getOrDefault(v1.toString(), 0));
        assertTrue(histogram.getOrDefault(v4.toString(), 0) > histogram.getOrDefault(v2.toString(), 0));
    }
}