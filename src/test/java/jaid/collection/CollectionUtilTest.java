package jaid.collection;

import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import jaid.collection.Tuples.IntIntPair;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static jaid.collection.CollectionUtil.binarySearch;
import static jaid.collection.CollectionUtil.searchRanges;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CollectionUtilTest {

    @Test
    public void testBinarySearch() {
        // Base test cases
        LongList baseList = new LongArrayList(Arrays.asList(1L, 3L, 3L, 3L, 5L, 8L, 9L));
        assertEquals(0, binarySearch(baseList, 1));    // key at start
        assertEquals(4, binarySearch(baseList, 5));    // key in the middle
        assertEquals(6, binarySearch(baseList, 9));    // key at end
        assertEquals(1, binarySearch(baseList, 3));    // key with duplicates
        assertEquals(-1, binarySearch(baseList, 0));   // key less than all elements
        assertEquals(-8, binarySearch(baseList, 10));  // key greater than all elements
        assertEquals(-5, binarySearch(baseList, 4));   // key not present in list

        // Empty list
        LongList emptyList = new LongArrayList();
        assertEquals(-1, binarySearch(emptyList, 5)); // key in an empty list

        // Single element in the list
        LongList singleElementList = new LongArrayList(Arrays.asList(5L));
        assertEquals(0, binarySearch(singleElementList, 5)); // matching key
        assertEquals(-1, binarySearch(singleElementList, 3)); // key less than the only element
        assertEquals(-2, binarySearch(singleElementList, 8)); // key greater than the only element

        // All elements are the same
        LongList sameElementsList = new LongArrayList(Arrays.asList(5L, 5L, 5L, 5L));
        assertEquals(0, binarySearch(sameElementsList, 5)); // matching key
        assertEquals(-1, binarySearch(sameElementsList, 3)); // key less than all elements
        assertEquals(-5, binarySearch(sameElementsList, 6)); // key greater than all elements

        // Negative numbers
        LongList negativeList = new LongArrayList(Arrays.asList(-8L, -5L, -3L, 0L, 1L, 4L));
        assertEquals(0, binarySearch(negativeList, -8)); // key at start, negative
        assertEquals(3, binarySearch(negativeList, 0));  // key in the middle, zero
        assertEquals(-1, binarySearch(negativeList, -9)); // key less than all elements, negative
        assertEquals(-7, binarySearch(negativeList, 5));  // key greater than all elements

        // Insertion points
        LongList insertionList = new LongArrayList(Arrays.asList(2L, 4L, 6L, 8L));
        assertEquals(-1, binarySearch(insertionList, 1)); // insert at start
        assertEquals(-3, binarySearch(insertionList, 5)); // insert in the middle
        assertEquals(-5, binarySearch(insertionList, 9)); // insert at end
    }

    @Test
    public void testSearchRanges() {
        // Base test cases with non-overlapping ranges
        List<IntIntPair> ranges = Arrays.asList(
                new IntIntPair(1, 5), // Range 0
                new IntIntPair(6, 10), // Range 1
                new IntIntPair(11, 15), // Range 2
                new IntIntPair(16, 20) // Range 3
        );

        // Inside ranges
        assertEquals(0, searchRanges(ranges, 2));   // inside first range
        assertEquals(2, searchRanges(ranges, 12));  // inside third range
        assertEquals(3, searchRanges(ranges, 18));  // inside last range

        // Between ranges
        assertEquals(1, searchRanges(ranges, 6));  // just at the start of second range
        assertEquals(2, searchRanges(ranges, 11)); // just at the start of third range

        // Outside ranges
        assertEquals(-1, searchRanges(ranges, 0));   // below all ranges
        assertEquals(-5, searchRanges(ranges, 21));  // above all ranges

        // Empty list
        List<IntIntPair> emptyRanges = Collections.emptyList();
        assertEquals(-1, searchRanges(emptyRanges, 5)); // any key in an empty list

        // Single range
        List<IntIntPair> singleRange = Collections.singletonList(new IntIntPair(10, 15));
        assertEquals(0, searchRanges(singleRange, 11)); // inside the range
        assertEquals(-1, searchRanges(singleRange, 9));  // below the range
        assertEquals(-2, searchRanges(singleRange, 16)); // above the range
    }
}