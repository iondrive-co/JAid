package jaid.number;

import it.unimi.dsi.fastutil.bytes.Byte2ReferenceArrayMap;
import it.unimi.dsi.fastutil.bytes.Byte2ReferenceMap;
import jaid.collection.BoundedPriorityQueue;
import jaid.collection.IVector;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.emptyList;

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
    private final Byte2ReferenceMap<List<IVector>> vectors = new Byte2ReferenceArrayMap<>();

    public void add(final IVector vector) {
        vectors.computeIfAbsent(vector.simBucket(), k -> new ArrayList<>()).add(vector);
    }

    public boolean remove(final IVector vector) {
        byte simBucket = vector.simBucket();
        List<IVector> vectorsAtHash = vectors.get(simBucket);
        if (vectorsAtHash != null) {
            boolean removed = vectorsAtHash.remove(vector);
            if (vectorsAtHash.isEmpty()) {
                vectors.remove(simBucket);
            }
            return removed;
        }
        return false;
    }

    public List<IVector> query(final IVector queryVector, final int k) {
        // Sort results by their dot product, dropping any that are too low
        final BoundedPriorityQueue pq = new BoundedPriorityQueue(k);
        vectors.getOrDefault(queryVector.simBucket(), emptyList()).forEach(v -> pq.add(v, v.dotProduct(queryVector)));
        // TODO better bucketing to work without this
        if (pq.size() < k) {
            vectors.values().forEach(l -> l.forEach(v -> pq.add(v, v.dotProduct(queryVector))));
        }
        return pq.toList();
    }
}
