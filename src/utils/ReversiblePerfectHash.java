package utils;

/**
 * @param <T> A type of object which this class will produce hash values for. Each hash value can be used to lookup
 *           the instance that produced it.
 */
public interface ReversiblePerfectHash<T> {

    long hash(T toHash);

    T unHash(long toUnhash);
}