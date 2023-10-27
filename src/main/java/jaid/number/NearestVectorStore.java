package jaid.number;

import jaid.collection.IVector;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

/**
 * Maps similar {@link jaid.collection.IVector}s into the same hash bucket for efficient K-NN searches.
 * Similarity is determined by the dot product, so unless you want the vectors magnitude to influence similarity all
 * vectors should be normalised first so that the dot product becomes the cosine similarity.
 * All vectors must be the same size, bucket size defaults to 1/10th the vector size, this might not be suitable for
 * sparse vectors which would also benefit from a more sophisticated implementation incorporating minhash signatures.
 */
public class NearestVectorStore {

    private static final float BUCKET_SIZING_FACTOR = 0.1f;
    private final List<HashMap<Integer, List<IVector>>> tables;

    public NearestVectorStore(final int vectorDims) {
        this.tables = new ArrayList<>();
        final int numBuckets = Math.max(8, Math.round(vectorDims * BUCKET_SIZING_FACTOR));
        for (int i = 0; i < numBuckets; i++) {
            tables.add(new HashMap<>());
        }
    }

    public void addVector(final IVector vector) {
        for (final HashMap<Integer, List<IVector>> table : tables) {
            table.computeIfAbsent(vector.simHash(), k -> new ArrayList<>()).add(vector);
        }
    }

    public List<IVector> query(final IVector queryVector, final int k) {
        final PriorityQueue<IVector> pq = new PriorityQueue<>(Comparator.comparingDouble(queryVector::dotProduct));
        final Set<IVector> seen = new HashSet<>();
        for (final HashMap<Integer, List<IVector>> table : tables) {
            final List<IVector> bucket = table.get(queryVector.simHash());
            if (bucket != null) {
                for (final IVector neighbor : bucket) {
                    // If we have not already seen neighbour
                    if (seen.add(neighbor)) {
                        pq.offer(neighbor);
                        if (pq.size() > k) {
                            // Remove the least similar vector
                            pq.poll();
                        }
                    }
                }
            }
        }
        return new ArrayList<>(pq);
    }
}
