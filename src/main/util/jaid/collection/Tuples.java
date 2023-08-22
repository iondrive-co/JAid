package jaid.collection;

/**
 * Primitive specialised versions of tuples using java records, which is more efficient than the fastutil approach of
 * overriding a non-primitive base class. Comparators are not provided, as that would require a decision about the
 * order of elements for sorting of tuples larger than a singleton.
 */
public class Tuples {

    // Pairs

    public record IntIntPair(int first, int second){}
    private static final IntIntPair EMPTY_INT_INT_PAIR = new IntIntPair(0, 0);
    public static IntIntPair emptyIntIntPair() {
        return EMPTY_INT_INT_PAIR;
    }

    public record IntLongPair(int first, long second){}
    private static final IntLongPair EMPTY_INT_LONG_PAIR = new IntLongPair(0, 0);
    public static IntLongPair emptyIntLongPair() {
        return EMPTY_INT_LONG_PAIR;
    }


    public record IntReferencePair<T>(long first, T second){}
    private static final IntReferencePair<?> EMPTY_INT_REFERENCE_PAIR = new IntReferencePair<>(0, null);
    @SuppressWarnings("unchecked")
    public static <T> IntReferencePair<T> emptyIntReferencePair() {
        return (IntReferencePair<T>) EMPTY_INT_REFERENCE_PAIR;
    }

    public record LongIntPair(long first, int second){}
    private static final LongIntPair EMPTY_LONG_INT_PAIR = new LongIntPair(0, 0);
    public static LongIntPair emptyLongIntPair() {
        return EMPTY_LONG_INT_PAIR;
    }

    public record LongLongPair(long first, long second){}
    private static final LongLongPair EMPTY_LONG_LONG_PAIR = new LongLongPair(0, 0);
    public static LongLongPair emptyLongLongPair() {
        return EMPTY_LONG_LONG_PAIR;
    }

    public record LongReferencePair<T>(long first, T second){}
    private static final LongReferencePair<?> EMPTY_LONG_REFERENCE_PAIR = new LongReferencePair<>(0, null);
    @SuppressWarnings("unchecked")
    public static <T> LongReferencePair<T> emptyLongReferencePair() {
        return (LongReferencePair<T>) EMPTY_LONG_REFERENCE_PAIR;
    }

    // Triples

    public record IntIntIntTriple(int first, int second, int third){}
    private static final IntIntIntTriple EMPTY_INT_INT_INT_TRIPLE = new IntIntIntTriple(0, 0, 0);
    public static IntIntIntTriple emptyIntIntIntTriple() {
        return EMPTY_INT_INT_INT_TRIPLE;
    }

    public record LongLongLongTriple(long first, long second, long third){}
    private static final LongLongLongTriple EMPTY_LONG_LONG_LONG_TRIPLE = new LongLongLongTriple(0, 0, 0);
    public static LongLongLongTriple emptyLongLongLongTriple() {
        return EMPTY_LONG_LONG_LONG_TRIPLE;
    }
}
