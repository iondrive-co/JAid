package jaid.number;

import it.unimi.dsi.fastutil.ints.Int2ReferenceArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ReferenceMap;
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
    private final Int2ReferenceMap<List<IVector>> vectors = new Int2ReferenceArrayMap<>();
    // TODO adjust this dynamically as the map grows and shrinks - this will require rebucketing the whole map when it changes
    private final int universePower = 4;

    public void add(final IVector vector) {
        vectors.computeIfAbsent(vector.simBucket(universePower), k -> new ArrayList<>()).add(vector);
    }

    public boolean remove(final IVector vector) {
        int simBucket = vector.simBucket(universePower);
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
        vectors.getOrDefault(queryVector.simBucket(universePower), emptyList()).forEach(v -> pq.add(v, v.dotProduct(queryVector)));
        return pq.toList();
    }
}
