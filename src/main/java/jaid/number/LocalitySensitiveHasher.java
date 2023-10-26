package jaid.number;

import com.google.common.base.Preconditions;
import jaid.collection.FloatVector;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Maps similar {@link jaid.collection.FloatVector}s into the same hash bucket for efficient K-NN searches.
 * This is a very simple implementation and will have issues with sparse data etc.
 */
public class LocalitySensitiveHasher {
    private final List<HashMap<Integer, List<FloatVector>>> tables;
    private final int numBuckets;
    private final int vectorDims;

    public LocalitySensitiveHasher(final int numBuckets, final int vectorDims) {
        this.numBuckets = numBuckets;
        this.vectorDims = vectorDims;
        this.tables = new ArrayList<>();
        for (int i = 0; i < numBuckets; i++) {
            tables.add(new HashMap<>());
        }
    }

    public void addVector(final FloatVector vector) {
        Preconditions.checkArgument(vector.contents.length == vectorDims);
        for (HashMap<Integer, List<FloatVector>> table : tables) {
            int hash = hash(vector);
            table.computeIfAbsent(hash, k -> new ArrayList<>()).add(vector);
        }
    }

    public List<FloatVector> query(final FloatVector queryVector, final int k) {
        Preconditions.checkArgument(queryVector.contents.length == vectorDims);
        final PriorityQueue<FloatVector> pq = new PriorityQueue<>(Comparator.comparingDouble(queryVector::dotProduct));
        for (final HashMap<Integer, List<FloatVector>> table : tables) {
            final int hash = hash(queryVector);
            final List<FloatVector> bucket = table.get(hash);
            if (bucket != null) {
                for (FloatVector neighbor : bucket) {
                    pq.offer(neighbor);
                    if (pq.size() > k) {
                        // Remove the least similar vector
                        pq.poll();
                    }
                }
            }
        }
        List<FloatVector> topKNeighbors = new ArrayList<>(pq);
        return topKNeighbors;
    }

    private int hash(final FloatVector vector) {
        int hash = 0;
        for (int i = 0; i < vectorDims; i++) {
            if (vector.contents[i] > ThreadLocalRandom.current().nextFloat()) {
                hash |= 1 << i;
            }
        }
        return hash;
    }
}
