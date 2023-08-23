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

    public record ByteReferencePair<T>(byte first, T second) {}
    private static final ByteReferencePair<?> EMPTY_BYTE_REFERENCE_PAIR = new ByteReferencePair<>((byte) 0, null);
    @SuppressWarnings("unchecked")
    public static <T> ByteReferencePair<T> emptyByteReferencePair() {
        return (ByteReferencePair<T>) EMPTY_BYTE_REFERENCE_PAIR;
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

    public record ShortReferencePair<T>(short first, T second) {}
    private static final ShortReferencePair<?> EMPTY_SHORT_REFERENCE_PAIR = new ShortReferencePair<>((short) 0, null);
    @SuppressWarnings("unchecked")
    public static <T> ShortReferencePair<T> emptyShortReferencePair() {
        return (ShortReferencePair<T>) EMPTY_SHORT_REFERENCE_PAIR;
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

    public record IntReferencePair<T>(long first, T second){}
    private static final IntReferencePair<?> EMPTY_INT_REFERENCE_PAIR = new IntReferencePair<>(0, null);
    @SuppressWarnings("unchecked")
    public static <T> IntReferencePair<T> emptyIntReferencePair() {
        return (IntReferencePair<T>) EMPTY_INT_REFERENCE_PAIR;
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

    public record FloatReferencePair<T>(float first, T second) {}
    private static final FloatReferencePair<?> EMPTY_FLOAT_REFERENCE_PAIR = new FloatReferencePair<>(0.0f, null);
    @SuppressWarnings("unchecked")
    public static <T> FloatReferencePair<T> emptyFloatReferencePair() {
        return (FloatReferencePair<T>) EMPTY_FLOAT_REFERENCE_PAIR;
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

    public record LongReferencePair<T>(long first, T second){}
    private static final LongReferencePair<?> EMPTY_LONG_REFERENCE_PAIR = new LongReferencePair<>(0, null);
    @SuppressWarnings("unchecked")
    public static <T> LongReferencePair<T> emptyLongReferencePair() {
        return (LongReferencePair<T>) EMPTY_LONG_REFERENCE_PAIR;
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

    public record DoubleReferencePair<T>(double first, T second) {}
    private static final DoubleReferencePair<?> EMPTY_DOUBLE_REFERENCE_PAIR = new DoubleReferencePair<>(0.0, null);
    @SuppressWarnings("unchecked")
    public static <T> DoubleReferencePair<T> emptyDoubleReferencePair() {
        return (DoubleReferencePair<T>) EMPTY_DOUBLE_REFERENCE_PAIR;
    }

    // -------------------------------- Triples ----------------------------------------

    public record IntIntByteTriple(int first, int second, byte third) {}
    private static final IntIntByteTriple EMPTY_INT_INT_BYTE_TRIPLE = new IntIntByteTriple(0, 0, (byte) 0);
    public static IntIntByteTriple emptyIntIntByteTriple() {
        return EMPTY_INT_INT_BYTE_TRIPLE;
    }

    public record IntIntShortTriple(int first, int second, short third) {}
    private static final IntIntShortTriple EMPTY_INT_INT_SHORT_TRIPLE = new IntIntShortTriple(0, 0, (short) 0);
    public static IntIntShortTriple emptyIntIntShortTriple() {
        return EMPTY_INT_INT_SHORT_TRIPLE;
    }

    public record IntIntIntTriple(int first, int second, int third){}
    private static final IntIntIntTriple EMPTY_INT_INT_INT_TRIPLE = new IntIntIntTriple(0, 0, 0);
    public static IntIntIntTriple emptyIntIntIntTriple() {
        return EMPTY_INT_INT_INT_TRIPLE;
    }

    public record IntIntFloatTriple(int first, int second, float third) {}
    private static final IntIntFloatTriple EMPTY_INT_INT_FLOAT_TRIPLE = new IntIntFloatTriple(0, 0, 0.0f);
    public static IntIntFloatTriple emptyIntIntFloatTriple() {
        return EMPTY_INT_INT_FLOAT_TRIPLE;
    }

    public record IntIntLongTriple(int first, int second, long third) {}
    private static final IntIntLongTriple EMPTY_INT_INT_LONG_TRIPLE = new IntIntLongTriple(0, 0, 0L);
    public static IntIntLongTriple emptyIntIntLongTriple() {
        return EMPTY_INT_INT_LONG_TRIPLE;
    }

    public record IntIntDoubleTriple(int first, int second, double third) {}
    private static final IntIntDoubleTriple EMPTY_INT_INT_DOUBLE_TRIPLE = new IntIntDoubleTriple(0, 0, 0.0);
    public static IntIntDoubleTriple emptyIntIntDoubleTriple() {
        return EMPTY_INT_INT_DOUBLE_TRIPLE;
    }

    public record IntIntReferenceTriple<T>(int first, int second, T third){}
    private static final IntIntReferenceTriple<?> EMPTY_INT_INT_REFERENCE_TRIPLE = new IntIntReferenceTriple<>(0, 0, null);
    @SuppressWarnings("unchecked")
    public static <T> IntIntReferenceTriple<T> emptyIntIntReferenceTriple() {
        return (IntIntReferenceTriple<T>)EMPTY_INT_INT_REFERENCE_TRIPLE;
    }

    public record LongLongByteTriple(long first, long second, byte third) {}
    private static final LongLongByteTriple EMPTY_LONG_LONG_BYTE_TRIPLE = new LongLongByteTriple(0L, 0L, (byte) 0);
    public static LongLongByteTriple emptyLongLongByteTriple() {
        return EMPTY_LONG_LONG_BYTE_TRIPLE;
    }

    public record LongLongShortTriple(long first, long second, short third) {}
    private static final LongLongShortTriple EMPTY_LONG_LONG_SHORT_TRIPLE = new LongLongShortTriple(0L, 0L, (short) 0);
    public static LongLongShortTriple emptyLongLongShortTriple() {
        return EMPTY_LONG_LONG_SHORT_TRIPLE;
    }

    public record LongLongIntTriple(long first, long second, int third) {}
    private static final LongLongIntTriple EMPTY_LONG_LONG_INT_TRIPLE = new LongLongIntTriple(0L, 0L, 0);
    public static LongLongIntTriple emptyLongLongIntTriple() {
        return EMPTY_LONG_LONG_INT_TRIPLE;
    }

    public record LongLongFloatTriple(long first, long second, float third) {}
    private static final LongLongFloatTriple EMPTY_LONG_LONG_FLOAT_TRIPLE = new LongLongFloatTriple(0L, 0L, 0.0f);
    public static LongLongFloatTriple emptyLongLongFloatTriple() {
        return EMPTY_LONG_LONG_FLOAT_TRIPLE;
    }

    public record LongLongLongTriple(long first, long second, long third){}
    private static final LongLongLongTriple EMPTY_LONG_LONG_LONG_TRIPLE = new LongLongLongTriple(0, 0, 0);
    public static LongLongLongTriple emptyLongLongLongTriple() {
        return EMPTY_LONG_LONG_LONG_TRIPLE;
    }

    public record LongLongDoubleTriple(long first, long second, double third) {}
    private static final LongLongDoubleTriple EMPTY_LONG_LONG_DOUBLE_TRIPLE = new LongLongDoubleTriple(0L, 0L, 0.0);
    public static LongLongDoubleTriple emptyLongLongDoubleTriple() {
        return EMPTY_LONG_LONG_DOUBLE_TRIPLE;
    }

    public record LongLongReferenceTriple<T>(long first, long second, T third) {}
    private static final LongLongReferenceTriple<?> EMPTY_LONG_LONG_REFERENCE_TRIPLE = new LongLongReferenceTriple<>(0L, 0L, null);
    @SuppressWarnings("unchecked")
    public static <T> LongLongReferenceTriple<T> emptyLongLongReferenceTriple() {
        return (LongLongReferenceTriple<T>) EMPTY_LONG_LONG_REFERENCE_TRIPLE;
    }
}
