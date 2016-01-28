package utils;


import java.io.Serializable;
import java.util.function.LongPredicate;

/**
 * Adds version identifiers and serialisation to a {@link ReversiblePerfectHash}.
 */
public abstract class AbstractRMPHash<T> implements ReversiblePerfectHash<T>, LongPredicate, Serializable {

    private static final long MASK_LOWER_7 = 0xFF00000000000000L;
    /**
     * Unique identifier for the type and version of the extender of this class. This values is included in all hashes
     * produced by this instance, and {@link #test} will only return true with a hash produced by this instance .
     */
    private final long typeId;

    protected AbstractRMPHash(final byte typeId) {
        this.typeId = (long)typeId << 56;
    }

    @Override
    public long hash(final T toHash) {
        return typeId | hashInternal(toHash);
    }

    protected abstract int hashInternal(final T toHash);

    @Override
    public boolean test(final long toTest) {
        return (toTest & MASK_LOWER_7) == typeId;
    }
}
