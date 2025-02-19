package jaid.collection;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BoundedPriorityQueueTest {

    private static final Random RANDOM = new Random(4444);

    @Test
    public void topK() {
        BoundedPriorityQueue bpq = new BoundedPriorityQueue(2);
        FloatsVector v1 = new FloatsVector(new float[] {1, 2, 3});
        FloatsVector v2 = new FloatsVector(new float[] {4, 5, 6});
        FloatsVector v3 = new FloatsVector(new float[] {7, 8, 9});
        FloatsVector queryVector = new FloatsVector(new float[] {1, 0, 0});

        double dot1 = v1.dotProduct(queryVector);
        double dot2 = v2.dotProduct(queryVector);
        double dot3 = v3.dotProduct(queryVector);
        bpq.add(v1, dot1);
        bpq.add(v2, dot2);
        bpq.add(v3, dot3);

        List<IVector> topK = bpq.toList();
        assertEquals(2, topK.size());
        // Assert that the vectors are indeed the top k with highest dot products
        assertTrue(topK.contains(v2) && topK.contains(v3));
    }

    @Test
    public void identicalVectorFound() {
        BoundedPriorityQueue bpq = new BoundedPriorityQueue(3);
        FloatsVector v = randomVector(3, -1, 1);
        double dotProduct = v.dotProduct(v);

        bpq.add(v, dotProduct);
        bpq.add(v, dotProduct);

        List<IVector> topK = bpq.toList();
        assertThat(topK).containsExactly(v, v);
    }

    @Test
    public void negativeDotProducts() {
        BoundedPriorityQueue bpq = new BoundedPriorityQueue(3);
        FloatsVector v1 = new FloatsVector(new float[] {-0.1f, -0.2f, -0.3f});
        FloatsVector v2 = new FloatsVector(new float[] {-0.4f, -0.5f, -0.6f});
        FloatsVector queryVector = new FloatsVector(new float[] {1f, 1f, 1f});

        bpq.add(v1, v1.dotProduct(queryVector));
        bpq.add(v2, v2.dotProduct(queryVector));

        List<IVector> results = bpq.toList();
        assertEquals(2, results.size(), "There should be two vectors with negative dot products.");
    }

    @Test
    public void fractionalNumbers() {
        BoundedPriorityQueue bpq = new BoundedPriorityQueue(2);
        FloatsVector v1 = new FloatsVector(new float[] {0.5f, 0.25f, 0.125f});
        FloatsVector v2 = new FloatsVector(new float[] {0.6f, 0.3f, 0.9f});

        bpq.add(v1, v1.dotProduct(new FloatsVector(new float[] {2f, 2f, 2f})));
        bpq.add(v2, v2.dotProduct(new FloatsVector(new float[] {2f, 2f, 2f})));

        List<IVector> results = bpq.toList();
        assertTrue(results.size() <= 2 && results.contains(v2));
    }

    @Test
    public void differentDimensions() {
        BoundedPriorityQueue bpq = new BoundedPriorityQueue(3);
        FloatsVector v1 = new FloatsVector(new float[] {0.1f, 0.2f});
        FloatsVector v2 = new FloatsVector(new float[] {0.1f, 0.2f, 0.3f});
        FloatsVector queryVector = new FloatsVector(new float[] {1f, 1f});

        bpq.add(v1, v1.dotProduct(queryVector));
        // Dot product does not make sense for different dimensions
        assertThrows(IllegalArgumentException.class, () -> bpq.add(v2, v2.dotProduct(queryVector)));
    }

    private FloatsVector randomVector(int dimensions, float rangeStart, float rangeEnd) {
        float[] values = new float[dimensions];
        for (int i = 0; i < dimensions; i++) {
            values[i] = rangeStart + (rangeEnd - rangeStart) * RANDOM.nextFloat();
        }
        return new FloatsVector(values);
    }
}