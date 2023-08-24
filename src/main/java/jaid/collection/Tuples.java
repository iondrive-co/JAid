package jaid.collection;

/**
 * Primitive specialised versions of tuples using java records, which is more efficient than the fastutil approach of
 * overriding a non-primitive base class. Comparators are not provided, as that would require a decision about the
 * order of elements for sorting of tuples larger than a singleton.
 */
public class Tuples {

    // -------------------------  Pairs ---------------------------------------------

    public record ByteBytePair(byte first, byte second) {}
    private static final ByteBytePair EMPTY_BYTE_BYTE_PAIR = new ByteBytePair((byte) 0, (byte) 0);
    public static ByteBytePair emptyByteBytePair() {
        return EMPTY_BYTE_BYTE_PAIR;
    }

    public record ByteShortPair(byte first, short second) {}
    private static final ByteShortPair EMPTY_BYTE_SHORT_PAIR = new ByteShortPair((byte) 0, (short) 0);
    public static ByteShortPair emptyByteShortPair() {
        return EMPTY_BYTE_SHORT_PAIR;
    }

    public record ByteIntPair(byte first, int second) {}
    private static final ByteIntPair EMPTY_BYTE_INT_PAIR = new ByteIntPair((byte) 0, 0);
    public static ByteIntPair emptyByteIntPair() {
        return EMPTY_BYTE_INT_PAIR;
    }

    public record ByteLongPair(byte first, long second) {}
    private static final ByteLongPair EMPTY_BYTE_LONG_PAIR = new ByteLongPair((byte) 0, 0L);
    public static ByteLongPair emptyByteLongPair() {
        return EMPTY_BYTE_LONG_PAIR;
    }

    public record ByteFloatPair(byte first, float second) {}
    private static final ByteFloatPair EMPTY_BYTE_FLOAT_PAIR = new ByteFloatPair((byte) 0, 0.0f);
    public static ByteFloatPair emptyByteFloatPair() {
        return EMPTY_BYTE_FLOAT_PAIR;
    }

    public record ByteDoublePair(byte first, double second) {}
    private static final ByteDoublePair EMPTY_BYTE_DOUBLE_PAIR = new ByteDoublePair((byte) 0, 0.0);
    public static ByteDoublePair emptyByteDoublePair() {
        return EMPTY_BYTE_DOUBLE_PAIR;
    }

    public record ByteAnyPair<T>(byte first, T second) {}
    private static final ByteAnyPair<?> EMPTY_BYTE_ANY_PAIR = new ByteAnyPair<>((byte) 0, null);
    @SuppressWarnings("unchecked")
    public static <T> ByteAnyPair<T> emptyByteAnyPair() {
        return (ByteAnyPair<T>) EMPTY_BYTE_ANY_PAIR;
    }

    public record ShortShortPair(short first, short second) {}
    private static final ShortShortPair EMPTY_SHORT_SHORT_PAIR = new ShortShortPair((short) 0, (short) 0);
    public static ShortShortPair emptyShortShortPair() {
        return EMPTY_SHORT_SHORT_PAIR;
    }

    public record ShortBytePair(short first, byte second) {}
    private static final ShortBytePair EMPTY_SHORT_BYTE_PAIR = new ShortBytePair((short) 0, (byte) 0);
    public static ShortBytePair emptyShortBytePair() {
        return EMPTY_SHORT_BYTE_PAIR;
    }

    public record ShortIntPair(short first, int second) {}
    private static final ShortIntPair EMPTY_SHORT_INT_PAIR = new ShortIntPair((short) 0, 0);
    public static ShortIntPair emptyShortIntPair() {
        return EMPTY_SHORT_INT_PAIR;
    }

    public record ShortLongPair(short first, long second) {}
    private static final ShortLongPair EMPTY_SHORT_LONG_PAIR = new ShortLongPair((short) 0, 0L);
    public static ShortLongPair emptyShortLongPair() {
        return EMPTY_SHORT_LONG_PAIR;
    }

    public record ShortFloatPair(short first, float second) {}
    private static final ShortFloatPair EMPTY_SHORT_FLOAT_PAIR = new ShortFloatPair((short) 0, 0.0f);
    public static ShortFloatPair emptyShortFloatPair() {
        return EMPTY_SHORT_FLOAT_PAIR;
    }

    public record ShortDoublePair(short first, double second) {}
    private static final ShortDoublePair EMPTY_SHORT_DOUBLE_PAIR = new ShortDoublePair((short) 0, 0.0);
    public static ShortDoublePair emptyShortDoublePair() {
        return EMPTY_SHORT_DOUBLE_PAIR;
    }

    public record ShortAnyPair<T>(short first, T second) {}
    private static final ShortAnyPair<?> EMPTY_SHORT_ANY_PAIR = new ShortAnyPair<>((short) 0, null);
    @SuppressWarnings("unchecked")
    public static <T> ShortAnyPair<T> emptyShortAnyPair() {
        return (ShortAnyPair<T>) EMPTY_SHORT_ANY_PAIR;
    }

    public record IntBytePair(int first, byte second) {}
    private static final IntBytePair EMPTY_INT_BYTE_PAIR = new IntBytePair(0, (byte) 0);
    public static IntBytePair emptyIntBytePair() {
        return EMPTY_INT_BYTE_PAIR;
    }

    public record IntShortPair(int first, short second) {}
    private static final IntShortPair EMPTY_INT_SHORT_PAIR = new IntShortPair(0, (short) 0);
    public static IntShortPair emptyIntShortPair() {
        return EMPTY_INT_SHORT_PAIR;
    }

    public record IntIntPair(int first, int second){}
    private static final IntIntPair EMPTY_INT_INT_PAIR = new IntIntPair(0, 0);
    public static IntIntPair emptyIntIntPair() {
        return EMPTY_INT_INT_PAIR;
    }

    public record IntFloatPair(int first, float second) {}
    private static final IntFloatPair EMPTY_INT_FLOAT_PAIR = new IntFloatPair(0, 0.0f);
    public static IntFloatPair emptyIntFloatPair() {
        return EMPTY_INT_FLOAT_PAIR;
    }

    public record IntLongPair(int first, long second) {}
    private static final IntLongPair EMPTY_INT_LONG_PAIR = new IntLongPair(0, 0L);
    public static IntLongPair emptyIntLongPair() {
        return EMPTY_INT_LONG_PAIR;
    }

    public record IntDoublePair(int first, double second) {}
    private static final IntDoublePair EMPTY_INT_DOUBLE_PAIR = new IntDoublePair(0, 0.0);
    public static IntDoublePair emptyIntDoublePair() {
        return EMPTY_INT_DOUBLE_PAIR;
    }

    public record IntAnyPair<T>(long first, T second){}
    private static final IntAnyPair<?> EMPTY_INT_ANY_PAIR = new IntAnyPair<>(0, null);
    @SuppressWarnings("unchecked")
    public static <T> IntAnyPair<T> emptyIntAnyPair() {
        return (IntAnyPair<T>) EMPTY_INT_ANY_PAIR;
    }

    public record FloatFloatPair(float first, float second) {}
    private static final FloatFloatPair EMPTY_FLOAT_FLOAT_PAIR = new FloatFloatPair(0.0f, 0.0f);
    public static FloatFloatPair emptyFloatFloatPair() {
        return EMPTY_FLOAT_FLOAT_PAIR;
    }

    public record FloatBytePair(float first, byte second) {}
    private static final FloatBytePair EMPTY_FLOAT_BYTE_PAIR = new FloatBytePair(0.0f, (byte) 0);
    public static FloatBytePair emptyFloatBytePair() {
        return EMPTY_FLOAT_BYTE_PAIR;
    }

    public record FloatShortPair(float first, short second) {}
    private static final FloatShortPair EMPTY_FLOAT_SHORT_PAIR = new FloatShortPair(0.0f, (short) 0);
    public static FloatShortPair emptyFloatShortPair() {
        return EMPTY_FLOAT_SHORT_PAIR;
    }

    public record FloatIntPair(float first, int second) {}
    private static final FloatIntPair EMPTY_FLOAT_INT_PAIR = new FloatIntPair(0.0f, 0);
    public static FloatIntPair emptyFloatIntPair() {
        return EMPTY_FLOAT_INT_PAIR;
    }

    public record FloatLongPair(float first, long second) {}
    private static final FloatLongPair EMPTY_FLOAT_LONG_PAIR = new FloatLongPair(0.0f, 0L);
    public static FloatLongPair emptyFloatLongPair() {
        return EMPTY_FLOAT_LONG_PAIR;
    }

    public record FloatDoublePair(float first, double second) {}
    private static final FloatDoublePair EMPTY_FLOAT_DOUBLE_PAIR = new FloatDoublePair(0.0f, 0.0);
    public static FloatDoublePair emptyFloatDoublePair() {
        return EMPTY_FLOAT_DOUBLE_PAIR;
    }

    public record FloatAnyPair<T>(float first, T second) {}
    private static final FloatAnyPair<?> EMPTY_FLOAT_ANY_PAIR = new FloatAnyPair<>(0.0f, null);
    @SuppressWarnings("unchecked")
    public static <T> FloatAnyPair<T> emptyFloatAnyPair() {
        return (FloatAnyPair<T>) EMPTY_FLOAT_ANY_PAIR;
    }

    public record LongBytePair(long first, byte second){}
    private static final LongBytePair EMPTY_LONG_BYTE_PAIR = new LongBytePair(0, (byte) 0);
    public static LongBytePair emptyLongBytePair() {
        return EMPTY_LONG_BYTE_PAIR;
    }

    public record LongShortPair(long first, short second){}
    private static final LongShortPair EMPTY_LONG_SHORT_PAIR = new LongShortPair(0, (short) 0);
    public static LongShortPair emptyLongShortPair() {
        return EMPTY_LONG_SHORT_PAIR;
    }

    public record LongIntPair(long first, int second){}
    private static final LongIntPair EMPTY_LONG_INT_PAIR = new LongIntPair(0, 0);
    public static LongIntPair emptyLongIntPair() {
        return EMPTY_LONG_INT_PAIR;
    }

    public record LongFloatPair(long first, float second){}
    private static final LongFloatPair EMPTY_LONG_FLOAT_PAIR = new LongFloatPair(0, 0.0f);
    public static LongFloatPair emptyLongFloatPair() {
        return EMPTY_LONG_FLOAT_PAIR;
    }

    public record LongLongPair(long first, long second){}
    private static final LongLongPair EMPTY_LONG_LONG_PAIR = new LongLongPair(0, 0);
    public static LongLongPair emptyLongLongPair() {
        return EMPTY_LONG_LONG_PAIR;
    }


    public record LongDoublePair(long first, double second){}
    private static final LongDoublePair EMPTY_LONG_DOUBLE_PAIR = new LongDoublePair(0, 0.0d);
    public static LongDoublePair emptyLongDoublePair() {
        return EMPTY_LONG_DOUBLE_PAIR;
    }

    public record LongAnyPair<T>(long first, T second){}
    private static final LongAnyPair<?> EMPTY_LONG_ANY_PAIR = new LongAnyPair<>(0, null);
    @SuppressWarnings("unchecked")
    public static <T> LongAnyPair<T> emptyLongAnyPair() {
        return (LongAnyPair<T>) EMPTY_LONG_ANY_PAIR;
    }

    public record DoubleDoublePair(double first, double second) {}
    private static final DoubleDoublePair EMPTY_DOUBLE_DOUBLE_PAIR = new DoubleDoublePair(0.0, 0.0);
    public static DoubleDoublePair emptyDoubleDoublePair() {
        return EMPTY_DOUBLE_DOUBLE_PAIR;
    }

    public record DoubleBytePair(double first, byte second) {}
    private static final DoubleBytePair EMPTY_DOUBLE_BYTE_PAIR = new DoubleBytePair(0.0, (byte) 0);
    public static DoubleBytePair emptyDoubleBytePair() {
        return EMPTY_DOUBLE_BYTE_PAIR;
    }

    public record DoubleShortPair(double first, short second) {}
    private static final DoubleShortPair EMPTY_DOUBLE_SHORT_PAIR = new DoubleShortPair(0.0, (short) 0);
    public static DoubleShortPair emptyDoubleShortPair() {
        return EMPTY_DOUBLE_SHORT_PAIR;
    }

    public record DoubleIntPair(double first, int second) {}
    private static final DoubleIntPair EMPTY_DOUBLE_INT_PAIR = new DoubleIntPair(0.0, 0);
    public static DoubleIntPair emptyDoubleIntPair() {
        return EMPTY_DOUBLE_INT_PAIR;
    }

    public record DoubleLongPair(double first, long second) {}
    private static final DoubleLongPair EMPTY_DOUBLE_LONG_PAIR = new DoubleLongPair(0.0, 0L);
    public static DoubleLongPair emptyDoubleLongPair() {
        return EMPTY_DOUBLE_LONG_PAIR;
    }

    public record DoubleFloatPair(double first, float second) {}
    private static final DoubleFloatPair EMPTY_DOUBLE_FLOAT_PAIR = new DoubleFloatPair(0.0, 0.0f);
    public static DoubleFloatPair emptyDoubleFloatPair() {
        return EMPTY_DOUBLE_FLOAT_PAIR;
    }

    public record DoubleAnyPair<T>(double first, T second) {}
    private static final DoubleAnyPair<?> EMPTY_DOUBLE_ANY_PAIR = new DoubleAnyPair<>(0.0, null);
    @SuppressWarnings("unchecked")
    public static <T> DoubleAnyPair<T> emptyDoubleAnyPair() {
        return (DoubleAnyPair<T>) EMPTY_DOUBLE_ANY_PAIR;
    }

    public record AnyAnyPair<T, U>(T first, U second) {}
    private static final AnyAnyPair<?, ?> EMPTY_ANY_ANY_PAIR = new AnyAnyPair<>(null, null);
    @SuppressWarnings("unchecked")
    public static <T, U> AnyAnyPair<T, U> emptyAnyAnyPair() {
        return (AnyAnyPair<T, U>) EMPTY_ANY_ANY_PAIR;
    }

    public record AnyBytePair<T>(T first, byte second) {}
    private static final AnyBytePair<?> EMPTY_ANY_BYTE_PAIR = new AnyBytePair<>(null, (byte) 0);
    @SuppressWarnings("unchecked")
    public static <T> AnyBytePair<T> emptyAnyBytePair() {
        return (AnyBytePair<T>) EMPTY_ANY_BYTE_PAIR;
    }

    public record AnyShortPair<T>(T first, short second) {}
    private static final AnyShortPair<?> EMPTY_ANY_SHORT_PAIR = new AnyShortPair<>(null, (short) 0);
    @SuppressWarnings("unchecked")
    public static <T> AnyShortPair<T> emptyAnyShortPair() {
        return (AnyShortPair<T>) EMPTY_ANY_SHORT_PAIR;
    }

    public record AnyIntPair<T>(T first, int second) {}
    private static final AnyIntPair<?> EMPTY_ANY_INT_PAIR = new AnyIntPair<>(null, 0);
    @SuppressWarnings("unchecked")
    public static <T> AnyIntPair<T> emptyAnyIntPair() {
        return (AnyIntPair<T>) EMPTY_ANY_INT_PAIR;
    }

    public record AnyLongPair<T>(T first, long second) {}
    private static final AnyLongPair<?> EMPTY_ANY_LONG_PAIR = new AnyLongPair<>(null, 0L);
    @SuppressWarnings("unchecked")
    public static <T> AnyLongPair<T> emptyAnyLongPair() {
        return (AnyLongPair<T>) EMPTY_ANY_LONG_PAIR;
    }

    public record AnyFloatPair<T>(T first, float second) {}
    private static final AnyFloatPair<?> EMPTY_ANY_FLOAT_PAIR = new AnyFloatPair<>(null, 0.0f);
    @SuppressWarnings("unchecked")
    public static <T> AnyFloatPair<T> emptyAnyFloatPair() {
        return (AnyFloatPair<T>) EMPTY_ANY_FLOAT_PAIR;
    }

    public record AnyDoublePair<T>(T first, double second) {}
    private static final AnyDoublePair<?> EMPTY_ANY_DOUBLE_PAIR = new AnyDoublePair<>(null, 0.0);
    @SuppressWarnings("unchecked")
    public static <T> AnyDoublePair<T> emptyAnyDoublePair() {
        return (AnyDoublePair<T>) EMPTY_ANY_DOUBLE_PAIR;
    }

    // -------------------------------- Triples ----------------------------------------

    public record IntIntIntTriple(int first, int second, int third) {}
    private static final IntIntIntTriple EMPTY_INT_INT_INT_TRIPLE = new IntIntIntTriple(0, 0, 0);
    public static IntIntIntTriple emptyIntIntIntTriple() {
        return EMPTY_INT_INT_INT_TRIPLE;
    }

    public record IntIntLongTriple(int first, int second, long third) {}
    private static final IntIntLongTriple EMPTY_INT_INT_LONG_TRIPLE = new IntIntLongTriple(0, 0, 0L);
    public static IntIntLongTriple emptyIntIntLongTriple() {
        return EMPTY_INT_INT_LONG_TRIPLE;
    }

    public record IntIntAnyTriple<T>(int first, int second, T third) {}
    private static final IntIntAnyTriple<?> EMPTY_INT_INT_ANY_TRIPLE = new IntIntAnyTriple<>(0, 0, null);
    @SuppressWarnings("unchecked")
    public static <T> IntIntAnyTriple<T> emptyIntIntAnyTriple() {
        return (IntIntAnyTriple<T>) EMPTY_INT_INT_ANY_TRIPLE;
    }

    public record IntLongIntTriple(int first, long second, int third) {}
    private static final IntLongIntTriple EMPTY_INT_LONG_INT_TRIPLE = new IntLongIntTriple(0, 0L, 0);
    public static IntLongIntTriple emptyIntLongIntTriple() {
        return EMPTY_INT_LONG_INT_TRIPLE;
    }

    public record IntLongLongTriple(int first, long second, long third) {}
    private static final IntLongLongTriple EMPTY_INT_LONG_LONG_TRIPLE = new IntLongLongTriple(0, 0L, 0L);
    public static IntLongLongTriple emptyIntLongLongTriple() {
        return EMPTY_INT_LONG_LONG_TRIPLE;
    }

    public record IntLongAnyTriple<T>(int first, long second, T third) {}
    private static final IntLongAnyTriple<?> EMPTY_INT_LONG_ANY_TRIPLE = new IntLongAnyTriple<>(0, 0L, null);
    @SuppressWarnings("unchecked")
    public static <T> IntLongAnyTriple<T> emptyIntLongAnyTriple() {
        return (IntLongAnyTriple<T>) EMPTY_INT_LONG_ANY_TRIPLE;
    }

    public record IntAnyIntTriple<T>(int first, T second, int third) {}
    private static final IntAnyIntTriple<?> EMPTY_INT_ANY_INT_TRIPLE = new IntAnyIntTriple<>(0, null, 0);
    @SuppressWarnings("unchecked")
    public static <T> IntAnyIntTriple<T> emptyIntAnyIntTriple() {
        return (IntAnyIntTriple<T>) EMPTY_INT_ANY_INT_TRIPLE;
    }

    public record IntAnyLongTriple<T>(int first, T second, long third) {}
    private static final IntAnyLongTriple<?> EMPTY_INT_ANY_LONG_TRIPLE = new IntAnyLongTriple<>(0, null, 0L);
    @SuppressWarnings("unchecked")
    public static <T> IntAnyLongTriple<T> emptyIntAnyLongTriple() {
        return (IntAnyLongTriple<T>) EMPTY_INT_ANY_LONG_TRIPLE;
    }

    public record IntAnyAnyTriple<T, U>(int first, T second, U third) {}
    private static final IntAnyAnyTriple<?, ?> EMPTY_INT_ANY_ANY_TRIPLE = new IntAnyAnyTriple<>(0, null, null);
    @SuppressWarnings("unchecked")
    public static <T, U> IntAnyAnyTriple<T, U> emptyIntAnyAnyTriple() {
        return (IntAnyAnyTriple<T, U>) EMPTY_INT_ANY_ANY_TRIPLE;
    }

    public record LongIntIntTriple(long first, int second, int third) {}
    private static final LongIntIntTriple EMPTY_LONG_INT_INT_TRIPLE = new LongIntIntTriple(0L, 0, 0);
    public static LongIntIntTriple emptyLongIntIntTriple() {
        return EMPTY_LONG_INT_INT_TRIPLE;
    }

    public record LongIntLongTriple(long first, int second, long third) {}
    private static final LongIntLongTriple EMPTY_LONG_INT_LONG_TRIPLE = new LongIntLongTriple(0L, 0, 0L);
    public static LongIntLongTriple emptyLongIntLongTriple() {
        return EMPTY_LONG_INT_LONG_TRIPLE;
    }

    public record LongIntAnyTriple<T>(long first, int second, T third) {}
    private static final LongIntAnyTriple<?> EMPTY_LONG_INT_ANY_TRIPLE = new LongIntAnyTriple<>(0L, 0, null);
    @SuppressWarnings("unchecked")
    public static <T> LongIntAnyTriple<T> emptyLongIntAnyTriple() {
        return (LongIntAnyTriple<T>) EMPTY_LONG_INT_ANY_TRIPLE;
    }

    public record LongLongIntTriple(long first, long second, int third) {}
    private static final LongLongIntTriple EMPTY_LONG_LONG_INT_TRIPLE = new LongLongIntTriple(0L, 0L, 0);
    public static LongLongIntTriple emptyLongLongIntTriple() {
        return EMPTY_LONG_LONG_INT_TRIPLE;
    }

    public record LongLongLongTriple(long first, long second, long third) {}
    private static final LongLongLongTriple EMPTY_LONG_LONG_LONG_TRIPLE = new LongLongLongTriple(0L, 0L, 0L);
    public static LongLongLongTriple emptyLongLongLongTriple() {
        return EMPTY_LONG_LONG_LONG_TRIPLE;
    }

    public record LongLongAnyTriple<T>(long first, long second, T third) {}
    private static final LongLongAnyTriple<?> EMPTY_LONG_LONG_ANY_TRIPLE = new LongLongAnyTriple<>(0L, 0L, null);
    @SuppressWarnings("unchecked")
    public static <T> LongLongAnyTriple<T> emptyLongLongAnyTriple() {
        return (LongLongAnyTriple<T>) EMPTY_LONG_LONG_ANY_TRIPLE;
    }

    public record LongAnyIntTriple<T>(long first, T second, int third) {}
    private static final LongAnyIntTriple<?> EMPTY_LONG_ANY_INT_TRIPLE = new LongAnyIntTriple<>(0L, null, 0);
    @SuppressWarnings("unchecked")
    public static <T> LongAnyIntTriple<T> emptyLongAnyIntTriple() {
        return (LongAnyIntTriple<T>) EMPTY_LONG_ANY_INT_TRIPLE;
    }

    public record LongAnyLongTriple<T>(long first, T second, long third) {}
    private static final LongAnyLongTriple<?> EMPTY_LONG_ANY_LONG_TRIPLE = new LongAnyLongTriple<>(0L, null, 0L);
    @SuppressWarnings("unchecked")
    public static <T> LongAnyLongTriple<T> emptyLongAnyLongTriple() {
        return (LongAnyLongTriple<T>) EMPTY_LONG_ANY_LONG_TRIPLE;
    }

    public record LongAnyAnyTriple<T, U>(long first, T second, U third) {}
    private static final LongAnyAnyTriple<?, ?> EMPTY_LONG_ANY_ANY_TRIPLE = new LongAnyAnyTriple<>(0L, null, null);
    @SuppressWarnings("unchecked")
    public static <T, U> LongAnyAnyTriple<T, U> emptyLongAnyAnyTriple() {
        return (LongAnyAnyTriple<T, U>) EMPTY_LONG_ANY_ANY_TRIPLE;
    }

    public record AnyIntIntTriple<T>(T first, int second, int third) {}
    private static final AnyIntIntTriple<?> EMPTY_ANY_INT_INT_TRIPLE = new AnyIntIntTriple<>(null, 0, 0);
    @SuppressWarnings("unchecked")
    public static <T> AnyIntIntTriple<T> emptyAnyIntIntTriple() {
        return (AnyIntIntTriple<T>) EMPTY_ANY_INT_INT_TRIPLE;
    }

    public record AnyIntLongTriple<T>(T first, int second, long third) {}
    private static final AnyIntLongTriple<?> EMPTY_ANY_INT_LONG_TRIPLE = new AnyIntLongTriple<>(null, 0, 0L);
    @SuppressWarnings("unchecked")
    public static <T> AnyIntLongTriple<T> emptyAnyIntLongTriple() {
        return (AnyIntLongTriple<T>) EMPTY_ANY_INT_LONG_TRIPLE;
    }

    public record AnyIntAnyTriple<T, U>(T first, int second, U third) {}
    private static final AnyIntAnyTriple<?, ?> EMPTY_ANY_INT_ANY_TRIPLE = new AnyIntAnyTriple<>(null, 0, null);
    @SuppressWarnings("unchecked")
    public static <T, U> AnyIntAnyTriple<T, U> emptyAnyIntAnyTriple() {
        return (AnyIntAnyTriple<T, U>) EMPTY_ANY_INT_ANY_TRIPLE;
    }

    public record AnyLongIntTriple<T>(T first, long second, int third) {}
    private static final AnyLongIntTriple<?> EMPTY_ANY_LONG_INT_TRIPLE = new AnyLongIntTriple<>(null, 0L, 0);
    @SuppressWarnings("unchecked")
    public static <T> AnyLongIntTriple<T> emptyAnyLongIntTriple() {
        return (AnyLongIntTriple<T>) EMPTY_ANY_LONG_INT_TRIPLE;
    }

    public record AnyLongLongTriple<T>(T first, long second, long third) {}
    private static final AnyLongLongTriple<?> EMPTY_ANY_LONG_LONG_TRIPLE = new AnyLongLongTriple<>(null, 0L, 0L);
    @SuppressWarnings("unchecked")
    public static <T> AnyLongLongTriple<T> emptyAnyLongLongTriple() {
        return (AnyLongLongTriple<T>) EMPTY_ANY_LONG_LONG_TRIPLE;
    }

    public record AnyLongAnyTriple<T, U>(T first, long second, U third) {}
    private static final AnyLongAnyTriple<?, ?> EMPTY_ANY_LONG_ANY_TRIPLE = new AnyLongAnyTriple<>(null, 0L, null);
    @SuppressWarnings("unchecked")
    public static <T, U> AnyLongAnyTriple<T, U> emptyAnyLongAnyTriple() {
        return (AnyLongAnyTriple<T, U>) EMPTY_ANY_LONG_ANY_TRIPLE;
    }

    public record AnyAnyIntTriple<T, U>(T first, U second, int third) {}
    private static final AnyAnyIntTriple<?, ?> EMPTY_ANY_ANY_INT_TRIPLE = new AnyAnyIntTriple<>(null, null, 0);
    @SuppressWarnings("unchecked")
    public static <T, U> AnyAnyIntTriple<T, U> emptyAnyAnyIntTriple() {
        return (AnyAnyIntTriple<T, U>) EMPTY_ANY_ANY_INT_TRIPLE;
    }

    public record AnyAnyLongTriple<T, U>(T first, U second, long third) {}
    private static final AnyAnyLongTriple<?, ?> EMPTY_ANY_ANY_LONG_TRIPLE = new AnyAnyLongTriple<>(null, null, 0L);
    @SuppressWarnings("unchecked")
    public static <T, U> AnyAnyLongTriple<T, U> emptyAnyAnyLongTriple() {
        return (AnyAnyLongTriple<T, U>) EMPTY_ANY_ANY_LONG_TRIPLE;
    }

    public record AnyAnyAnyTriple<T, U, V>(T first, U second, V third) {}
    private static final AnyAnyAnyTriple<?, ?, ?> EMPTY_ANY_ANY_ANY_TRIPLE = new AnyAnyAnyTriple<>(null, null, null);
    @SuppressWarnings("unchecked")
    public static <T, U, V> AnyAnyAnyTriple<T, U, V> emptyAnyAnyAnyTriple() {
        return (AnyAnyAnyTriple<T, U, V>) EMPTY_ANY_ANY_ANY_TRIPLE;
    }
}
