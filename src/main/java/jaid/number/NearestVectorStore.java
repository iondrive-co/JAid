package jaid.number;

import jaid.collection.IVector;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.TreeMap;

/**
 * Sorts {@link jaid.collection.IVector}s by their similarity hash for efficient K-NN searches, with a small
 * search space this extremely simple implementation should be able to outperform more advanced vector DBs and indexes.
 * The search halting condition is determined by the closeness of the dot product to the query, so unless you want the
 * vectors magnitude to influence similarity all vectors should be normalised first so that the dot product becomes the
 * cosine similarity.
 * This is likely not suitable for sparse vectors, it is heavily dependent on the sim hash without any techniques like
 * minhash signatures.
 */
public class NearestVectorStore {
    private final TreeMap<Integer, List<IVector>> vectorMap = new TreeMap<>();

    public void add(final IVector vector) {
        vectorMap.computeIfAbsent(vector.simHash(), k -> new ArrayList<>()).add(vector);
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

    /**
     * Search nearby similarity hashes for the k nearest neighbours of queryVector, stopping when k are found and tbe
     * dot product of the next set of results with queryVector starts to decline.
     */
    public List<IVector> query(final IVector queryVector, final int k) {
        // Sort results by their dot product, dropping any that are too low
        final PriorityQueue<IVector> pq = new PriorityQueue<>(Comparator.comparingDouble(o -> -o.dotProduct(queryVector)));
        boolean iteratingUp = true;
        boolean iteratingDown = true;
        int lastUpHash = queryVector.simHash();
        int lastDownHash = lastUpHash;
        // Start with any matches with exactly the same sim hash, and set the current best dot product result
        double lastDotProductUp = addCandidates(vectorMap.get(lastUpHash), pq, k, queryVector, -Double.MAX_VALUE);
        double lastDotProductDown = lastDotProductUp;
        while (iteratingUp || iteratingDown) {
            if (iteratingUp) {
                final Map.Entry<Integer, List<IVector>> ceilingEntry = vectorMap.higherEntry(lastUpHash);
                if (ceilingEntry == null) {
                    // We have reached the top of the map
                    iteratingUp = false;
                } else {
                    lastUpHash = ceilingEntry.getKey();
                    final double highestDotProduct =
                            addCandidates(ceilingEntry.getValue(), pq, k, queryVector, lastDotProductUp);
                    iteratingUp = highestDotProduct > lastDotProductUp;
                    lastDotProductUp = highestDotProduct;
                }
            }
            if (iteratingDown) {
                final Map.Entry<Integer, List<IVector>> floorEntry = vectorMap.floorEntry(lastDownHash);
                if (floorEntry == null) {
                    // We have reached the bottom of the map
                    iteratingDown = false;
                } else {
                    lastDownHash = floorEntry.getKey();
                    final double highestDotProduct =
                            addCandidates(floorEntry.getValue(), pq, k, queryVector, lastDotProductDown);
                    iteratingDown = highestDotProduct > lastDotProductDown;
                    lastDotProductDown = highestDotProduct;
                }
            }
        }
        final List<IVector> nearest = new ArrayList<>(k);
        while (!pq.isEmpty()) {
            nearest.add(0, pq.poll()); // Insert at the beginning to reverse the order
        }
        return nearest;
    }

    private static double addCandidates(final List<IVector> candidates, final PriorityQueue<IVector> pq, final int k,
                                        final IVector queryVector, double previousHighestDotProduct) {
        double highestDotProduct = -Double.MAX_VALUE;
        if (candidates != null && !candidates.isEmpty()) {
            for (final IVector vector : candidates) {
                highestDotProduct = Math.max(highestDotProduct, vector.dotProduct(queryVector));
                if (highestDotProduct > previousHighestDotProduct) {
                    pq.offer(vector);
                    if (pq.size() > k) {
                        pq.poll(); // Remove the least similar vector
                    }
                }
            }
        }
        return highestDotProduct;
    }
}
