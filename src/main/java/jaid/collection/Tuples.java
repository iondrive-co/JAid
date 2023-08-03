package jaid.collection;

/**
 * Primitive specialised versions of tuples using java records, which is more efficient than the fastutil approach of
 * overriding a non-primitive base class. Comparators are not provided, as that would require a decision about the
 * order of elements for sorting of tuples larger than a singleton.
 */
public class Tuples {

    public record IntIntPair(int key, int value){}
    private static final IntIntPair EMPTY_INT_INT_PAIR = new IntIntPair(0, 0);
    public static IntIntPair emptyIntIntPair() {
        return EMPTY_INT_INT_PAIR;
    }

    public record IntLongPair(int key, long value){}
    private static final IntLongPair EMPTY_INT_LONG_PAIR = new IntLongPair(0, 0);
    public static IntLongPair emptyIntLongPair() {
        return EMPTY_INT_LONG_PAIR;
    }


    public record IntReferencePair<T>(long key, T value){}
    private static final IntReferencePair<?> EMPTY_INT_REFERENCE_PAIR = new IntReferencePair<>(0, null);
    @SuppressWarnings("unchecked")
    public static <T> IntReferencePair<T> emptyIntReferencePair() {
        return (IntReferencePair<T>) EMPTY_INT_REFERENCE_PAIR;
    }

    public record LongIntPair(long key, int value){}
    private static final LongIntPair EMPTY_LONG_INT_PAIR = new LongIntPair(0, 0);
    public static LongIntPair emptyLongIntPair() {
        return EMPTY_LONG_INT_PAIR;
    }

    public record LongLongPair(long key, long value){}
    private static final LongLongPair EMPTY_LONG_LONG_PAIR = new LongLongPair(0, 0);
    public static LongLongPair emptyLongLongPair() {
        return EMPTY_LONG_LONG_PAIR;
    }

    public record LongReferencePair<T>(long key, T value){}
    private static final LongReferencePair<?> EMPTY_LONG_REFERENCE_PAIR = new LongReferencePair<>(0, null);
    @SuppressWarnings("unchecked")
    public static <T> LongReferencePair<T> emptyLongReferencePair() {
        return (LongReferencePair<T>) EMPTY_LONG_REFERENCE_PAIR;
    }
}
