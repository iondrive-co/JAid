package jaid.collection;

import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleIterator;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import it.unimi.dsi.fastutil.floats.FloatArrayList;
import it.unimi.dsi.fastutil.floats.FloatIterator;
import it.unimi.dsi.fastutil.floats.FloatList;
import it.unimi.dsi.fastutil.longs.LongList;
import jaid.collection.Tuples.IntIntReferenceTriple;
import jaid.function.FloatSupplier;

import javax.annotation.CheckForNull;
import java.util.List;
import java.util.function.DoublePredicate;

import static com.google.common.base.Preconditions.checkNotNull;

public class CollectionUtil {

    /**
     * Searches a range of the specified list for the specified key using a binary search algorithm. The list must be
     * sorted prior to making this call, or the results are undefined. If the list contains multiple elements with the
     * specified time, then unlike the standard implementation this does a binary search for the first element at an
     * extra log(N) cost.
     *
     * @param valuesToSearch the sorted list to be searched.
     * @param key the time value to be searched for.
     * @return index of the search key, if it is contained in the list;
     * otherwise, <code>(-(<i>insertion point</i>) - 1)</code>. The <i>insertion point</i> is defined as the point at
     * which the value would be inserted into the list: the index of the first element greater than the key, or the
     * length of the list, if all elements in the list are less than the specified key. Note that this guarantees that
     * the return value will be &ge; 0 if and only if the key is found.
     */

    public static int binarySearch(final LongList valuesToSearch, final long key) {
        int from = 0;
        int to =  valuesToSearch.size();
        long midVal;
        to--;
        while (from <= to) {
            final int mid = (from + to) >>> 1;
            midVal = valuesToSearch.getLong(mid);
            if (midVal < key) {
                from = mid + 1;
            }
            else if (midVal > key) {
                to = mid - 1;
            }
            else if (from != mid) {
                to = mid;
            }
            else {
                return mid;
            }
        }
        return -(from + 1);
    }

    /**
     * Uses binary search to find the range containing the specified key, or null if none do. Each triple in the list is
     * a range, the first item is the start, the second the end, and the third the value. The list must not contain
     * any overlapping ranges and must be sorted ascending, otherwise the results are undefined.
     */
    @CheckForNull
    public static <T> T searchRanges(final int key, final List<IntIntReferenceTriple<T>> toSearch) {
        if (toSearch == null || toSearch.isEmpty()) {
            return null;
        }
        int low = 0;
        int high = toSearch.size() - 1;
        while (low <= high) {
            int mid = (low + high) >>> 1;
            // If the top of the range is less than the key, then the key is above it
            final IntIntReferenceTriple<T> range = toSearch.get(mid);
            if (Integer.compareUnsigned(toSearch.get(mid).second(), key) < 0) {
                low = mid + 1;
            // If the bottom of this range is less than the key, then the key is below it
            } else if (Integer.compareUnsigned(range.first(), key) > 0) {
                high = mid - 1;
            // Else the key is inside this range
            } else {
                return toSearch.get(mid).third();
            }
        }
        return null;
    }

    public static double[] extract(final DoubleIterator iterator, final int size) {
        final double[] clone = new double[size];
        checkNotNull(iterator);
        int i = 0;
        while (iterator.hasNext()) {
            clone[i++] = iterator.nextDouble();
        }
        return clone;
    }

    public static DoubleList extract(final DoubleIterator iterator, final DoublePredicate predicate) {
        final DoubleList clone = new DoubleArrayList();
        checkNotNull(iterator);
        while (iterator.hasNext()) {
            final double value = iterator.nextDouble();
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
            clone[i++] = iterator.nextFloat();
        }
        return clone;
    }

    public static FloatList extract(final FloatIterator iterator, final DoublePredicate predicate) {
        final FloatList clone = new FloatArrayList();
        checkNotNull(iterator);
        while (iterator.hasNext()) {
            final float value = iterator.nextFloat();
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
            clone.add(iterator.nextFloat());
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
        };
    }
}
