package jaid.number;

import com.google.common.annotations.VisibleForTesting;
import jaid.collection.IVector;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.TreeMap;

/**
 * Maps similar {@link jaid.collection.IVector}s into the same hash bucket for efficient K-NN searches, with a small
 * search space this extremely simple implementation should be able to outperform more advanced vector DBs and indexes.
 * Similarity is determined by the dot product, so unless you want the vectors magnitude to influence similarity all
 * vectors should be normalised first so that the dot product becomes the cosine similarity.
 * This is likely not suitable for small (we only check the closest neighbour) or sparse (there are no minhash
 * signatures) vectors.
 */
public class NearestVectorStore {
    private final TreeMap<Integer, List<IVector>> vectorMap;

    public NearestVectorStore() {
        this.vectorMap = new TreeMap<>();
    }

    public void add(final IVector vector) {
        int hash = vector.simHash();
        vectorMap.computeIfAbsent(hash, k -> new ArrayList<>()).add(vector);
    }

    public boolean remove(final IVector vector) {
        int hash = vector.simHash();
        List<IVector> vectorsAtHash = vectorMap.get(hash);
        if (vectorsAtHash != null) {
            boolean removed = vectorsAtHash.remove(vector);
            if (vectorsAtHash.isEmpty()) {
                vectorMap.remove(hash);
            }
            return removed;
        }
        return false;
    }

    public List<IVector> query(final IVector queryVector, final int k) {
        final PriorityQueue<IVector> pq = new PriorityQueue<>(Comparator.comparingDouble(o -> -o.dotProduct(queryVector)));
        final int queryHash = queryVector.simHash();

        // Look for the exact and close hashes
        Integer floorKey = vectorMap.floorKey(queryHash);
        Integer ceilingKey = vectorMap.ceilingKey(queryHash);

        // Fetch buckets in the vicinity of the query hash
        getVectorsFromBucket(queryHash, pq, k);
        if (floorKey != null) getVectorsFromBucket(floorKey, pq, k);
        if (ceilingKey != null) getVectorsFromBucket(ceilingKey, pq, k);

        List<IVector> nearest = new ArrayList<>(k);
        while (!pq.isEmpty() && nearest.size() < k) {
            nearest.add(pq.poll());
        }
        return nearest;
    }

    @VisibleForTesting
    void getVectorsFromBucket(final Integer hashKey, final PriorityQueue<IVector> pq, final int k) {
        List<IVector> bucket = vectorMap.get(hashKey);
        if (bucket != null) {
            for (IVector vector : bucket) {
                pq.offer(vector);
                if (pq.size() > k) {
                    pq.poll(); // Remove the least similar vector
                }
            }
        }
    }
}
