package jaid.number;

import com.google.common.base.Preconditions;
import jaid.collection.FloatVector;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;

/**
 * Maps similar {@link jaid.collection.FloatVector}s into the same hash bucket for efficient K-NN searches.
 * Similarity is determined by the dot product, so unless you want the vectors magnitude to influence similarity all
 * vectors should be normalised first so that the dot product becomes the cosine similarity.
 * This is a very simple implementation and will have issues with sparse data etc.
 */
public class NearestVectorStore {
    private final List<HashMap<Integer, List<FloatVector>>> tables;
    private final int vectorDims;

    public NearestVectorStore(final int numBuckets, final int vectorDims) {
        this.vectorDims = vectorDims;
        this.tables = new ArrayList<>();
        for (int i = 0; i < numBuckets; i++) {
            tables.add(new HashMap<>());
        }
    }

    public void addVector(final FloatVector vector) {
        Preconditions.checkArgument(vector.contents.length == vectorDims);
        for (HashMap<Integer, List<FloatVector>> table : tables) {
            table.computeIfAbsent(vector.hashCode(), k -> new ArrayList<>()).add(vector);
        }
    }

    public List<FloatVector> query(final FloatVector queryVector, final int k) {
        Preconditions.checkArgument(queryVector.contents.length == vectorDims);
        final PriorityQueue<FloatVector> pq = new PriorityQueue<>(Comparator.comparingDouble(queryVector::dotProduct));
        for (final HashMap<Integer, List<FloatVector>> table : tables) {
            final List<FloatVector> bucket = table.get(queryVector.hashCode());
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
        return new ArrayList<>(pq);
    }
}
