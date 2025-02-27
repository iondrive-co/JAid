package jaid.number;

import com.google.common.annotations.VisibleForTesting;
import it.unimi.dsi.fastutil.ints.Int2ByteAVLTreeMap;
import it.unimi.dsi.fastutil.ints.Int2ByteMap;
import it.unimi.dsi.fastutil.ints.Int2ReferenceArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ReferenceMap;
import jaid.collection.BoundedPriorityQueue;
import jaid.collection.IVector;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    /**
     * When to break {@link #vectors} into more buckets, mapped to the bits to use for the bucket size (i.e. 2^4)
     */
    private static final Map<Integer, Byte> DEFAULT_BUCKET_THRESHOLDS = Map.of(
            10_000, (byte)0,
            100_000, (byte)4,
            Integer.MAX_VALUE, (byte)16);
    private final Int2ByteAVLTreeMap thresholds;
    private final Int2ReferenceMap<List<IVector>> vectors = new Int2ReferenceArrayMap<>();
    private int size;
    private byte bucketSizeExponent;

    public NearestVectorStore() {
        this(DEFAULT_BUCKET_THRESHOLDS);
    }

    @VisibleForTesting
    NearestVectorStore(final Map<Integer, Byte> thresholds) {
        // Need to ensure this is sorted ascending
        this.thresholds = new Int2ByteAVLTreeMap(thresholds);
        updateBuckets();
    }

    public void add(final IVector vector) {
        size += 1;
        updateBuckets();
        vectors.computeIfAbsent(vector.getSimHashBucket(bucketSizeExponent), k -> new ArrayList<>()).add(vector);
    }

    public boolean remove(final IVector vector) {
        int simBucket = vector.getSimHashBucket(bucketSizeExponent);
        List<IVector> vectorsAtHash = vectors.get(simBucket);
        if (vectorsAtHash != null) {
            boolean removed = vectorsAtHash.remove(vector);

            if (vectorsAtHash.isEmpty()) {
                vectors.remove(simBucket);
            }
            size -= 1;
            updateBuckets();
            return removed;
        }
        return false;
    }

    public List<IVector> query(final IVector queryVector, final int k) {
        // Sort results by their dot product, dropping any that are too low
        final BoundedPriorityQueue pq = new BoundedPriorityQueue(k);
        vectors.getOrDefault(queryVector.getSimHashBucket(bucketSizeExponent), emptyList()).forEach(v -> pq.add(v, v.dotProduct(queryVector)));
        return pq.toList();
    }

    public byte getBucketSizeExponent() {
        return bucketSizeExponent;
    }

    public int size() {
        return size;
    }

    public void clear() {
        vectors.clear();
        size = 0;
        updateBuckets();
    }

    @VisibleForTesting
    protected void updateBuckets() {
        // thresholds are sorted ascending, so find the first one that applies
        final byte oldBucketSizeExponent = bucketSizeExponent;
        for (final Int2ByteMap.Entry entry : thresholds.int2ByteEntrySet()) {
            if (size < entry.getIntKey()) {
                bucketSizeExponent = entry.getByteValue();
                break;
            }
        }
        if (oldBucketSizeExponent != bucketSizeExponent) {
            final Int2ReferenceMap<List<IVector>> newVectors = new Int2ReferenceArrayMap<>();
            for (final List<IVector> vectorList : vectors.values()) {
                for (final IVector vector : vectorList) {
                    newVectors.computeIfAbsent(vector.getSimHashBucket(bucketSizeExponent), k -> new ArrayList<>()).add(vector);
                }
            }
            vectors.clear();
            vectors.putAll(newVectors);
        }
    }
}
