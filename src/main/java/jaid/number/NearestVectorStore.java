package jaid.number;

import jaid.collection.BoundedPriorityQueue;
import jaid.collection.IVector;

import java.util.ArrayList;
import java.util.List;

/**
 * Buckets {@link jaid.collection.IVector}s by their similarity hash for efficient K-NN searches, with a small
 * search space this extremely simple implementation should be able to outperform more advanced vector DBs and indexes.
 * The search halting condition is determined by the closeness of the dot product to the query, so unless you want the
 * vectors magnitude to influence similarity all vectors should be normalised first so that the dot product becomes the
 * cosine similarity.
 * This is likely not suitable for sparse vectors, it is heavily dependent on the sim hash without any techniques like
 * minhash signatures.
 */
public class NearestVectorStore {
    private final List<IVector> vectors = new ArrayList<>();

    public void add(final IVector vector) {
        vectors.add(vector);
    }

    public boolean remove(final IVector vector) {
        return vectors.remove(vector);
    }

    public List<IVector> query(final IVector queryVector, final int k) {
        // Sort results by their dot product, dropping any that are too low
        final BoundedPriorityQueue pq = new BoundedPriorityQueue(k);
        vectors.forEach(v -> pq.add(v, v.dotProduct(queryVector)));
        return pq.toList();
    }
}
