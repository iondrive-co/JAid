package jaid.collection;

import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleIterator;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import it.unimi.dsi.fastutil.floats.FloatArrayList;
import it.unimi.dsi.fastutil.floats.FloatIterator;
import it.unimi.dsi.fastutil.floats.FloatList;
import it.unimi.dsi.fastutil.longs.LongList;
import jaid.function.FloatSupplier;

import java.util.function.DoublePredicate;

import static com.google.common.base.Preconditions.checkNotNull;

public class Collections {

    public static double[] extract(final DoubleIterator iterator, final int size) {
        final double[] clone = new double[size];
        checkNotNull(iterator);
        int i = 0;
        while (iterator.hasNext()) {
            clone[i++] = iterator.next();
        }
        return clone;
    }

    public static DoubleList extract(final DoubleIterator iterator, final DoublePredicate predicate) {
        final DoubleList clone = new DoubleArrayList();
        checkNotNull(iterator);
        while (iterator.hasNext()) {
            final double value = iterator.next();
            if (predicate.test(value)) {
                clone.add(value);
            }
        }
        return clone;
    }

    public static float[] extract(final FloatIterator iterator, final int size) {
        final float[] clone = new float[size];
        checkNotNull(iterator);
        int i = 0;
        while (iterator.hasNext()) {
            clone[i++] = iterator.next();
        }
        return clone;
    }

    public static FloatList extract(final FloatIterator iterator, final DoublePredicate predicate) {
        final FloatList clone = new FloatArrayList();
        checkNotNull(iterator);
        while (iterator.hasNext()) {
            final float value = iterator.next();
            if (predicate.test(value)) {
                clone.add(value);
            }
        }
        return clone;
    }

    public static FloatList extract(final FloatIterator iterator) {
        final FloatList clone = new FloatArrayList();
        checkNotNull(iterator);
        while (iterator.hasNext()) {
            clone.add(iterator.next());
        }
        return clone;
    }

    public static FloatIterator toIterator(final FloatSupplier floatSupplier) {
        return new FloatIterator() {
            float nextFloat = floatSupplier.getAsFloat();
            @Override
            public float nextFloat() {
                float thisFloat = nextFloat;
                nextFloat = floatSupplier.getAsFloat();
                return thisFloat;
            }

            @Override
            public int skip(final int n) {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean hasNext() {
                return !Float.isNaN(nextFloat);
            }

            @Override
            public Float next() {
                return nextFloat();
            }
        };
    }

    /**
     * Searches a range of the specified list for the fx value at the specified time using a binary search algorithm.
     * The list must be sorted prior to making this call. If it is not sorted, the results are undefined. If the list
     * contains multiple elements with the specified time, then
     *
     * <b>unlike the standard implementation this does a binary search for the first element at an extra log(N) cost.</b>
     *
     * @param valuesToSearch
     *            the sorted list to be searched.
     * @param key
     *            the time value to be searched for.
     * @return index of the search key, if it is contained in the list;
     *         otherwise, <code>(-(<i>insertion point</i>) - 1)</code>. The
     *         <i>insertion point</i> is defined as the the point at which the
     *         value would be inserted into the list: the index of the first
     *         element greater than the key, or the length of the list, if all
     *         elements in the list are less than the specified key. Note that
     *         this guarantees that the return value will be &ge; 0 if and only
     *         if the key is found.
     */

    public static int binarySearch(final LongList valuesToSearch, final long key) {
        int from = 0;
        int to =  valuesToSearch.size();
        long midVal;
        to--;
        while (from <= to) {
            final int mid = (from + to) >>> 1;
            midVal = valuesToSearch.get(mid);
            if (midVal < key)
                from = mid + 1;
            else if (midVal > key)
                to = mid - 1;
            else if (from != mid)
                to = mid;
            else
                return mid;
        }
        return -(from + 1);
    }
}
