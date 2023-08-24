package jaid.collection;

import it.unimi.dsi.fastutil.longs.LongList;
import jaid.collection.Tuples.IntIntPair;

import java.util.List;

public class CollectionUtil {

    /**
     * Searches a range of the specified list for the specified key using a binary search algorithm. The list must be
     * sorted prior to making this call, or the results are undefined. If the list contains multiple elements with the
     * specified time, then unlike the standard implementation this does a binary search for the first element at an
     * extra log(N) cost.
     *
     * @return index of the key if it is contained in the list, otherwise <code>(-(<i>insertion point</i>) - 1)</code>.
     * The <i>insertion point</i> is defined as the point at which the value would be inserted into the list: the index
     * of the first element greater than the key, or the length of the list, if all elements in the list are less than
     * the specified key. Note that this guarantees that the return value will be <= 0 if and only if the key is found.
     * @throws NullPointerException if rangesToSearch is null
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
     * Uses binary search to find the range containing the specified key, or < 0 if none do. The list must not contain
     * any overlapping ranges and must be sorted ascending, otherwise the results are undefined.
     *
     * @return index of the key if it is contained in the list, otherwise <code>(-(<i>insertion point</i>) - 1)</code>.
     * The <i>insertion point</i> is defined as the point at which the value would be inserted into the list: the index
     * of the first element greater than the key, or the length of the list, if all elements in the list are less than
     * the specified key. Note that this guarantees that the return value will be <= 0 if and only if the key is found.
     * @throws NullPointerException if rangesToSearch is null
     */
    public static int searchRanges(final List<IntIntPair> rangesToSearch, final int key) {
        int low = 0;
        int high = rangesToSearch.size() - 1;
        while (low <= high) {
            int mid = (low + high) >>> 1;
            // If the top of the range is less than the key, then the key is above it
            final IntIntPair range = rangesToSearch.get(mid);
            if (Integer.compareUnsigned(rangesToSearch.get(mid).second(), key) < 0) {
                low = mid + 1;
            // If the bottom of this range is less than the key, then the key is below it
            } else if (Integer.compareUnsigned(range.first(), key) > 0) {
                high = mid - 1;
            // Else the key is inside this range
            } else {
                return mid;
            }
        }
        return -(low + 1);
    }
}
