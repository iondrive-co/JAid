package jaid.collection;

import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static jaid.collection.CollectionUtil.binarySearch;
import static jaid.collection.CollectionUtil.searchRanges;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class CollectionUtilTest {

    @Test
    public void testBinarySearch() {
        // List to use for testing
        LongList list = new LongArrayList(Arrays.asList(1L, 3L, 3L, 3L, 5L, 8L, 9L));

        // Valid searches
        assertEquals(0, binarySearch(list, 1));    // key at start
        assertEquals(4, binarySearch(list, 5));    // key in the middle
        assertEquals(6, binarySearch(list, 9));    // key at end
        assertEquals(1, binarySearch(list, 3));    // key with duplicates

        // Invalid searches
        assertEquals(-1, binarySearch(list, 0));   // key less than all elements
        assertEquals(-8, binarySearch(list, 10));  // key greater than all elements
        assertEquals(-5, binarySearch(list, 4));   // key not present in list
    }

    @Test
    public void testSearchRanges() {
        // List of ranges to use for testing
        List<Tuples.IntIntReferenceTriple<String>> ranges = Arrays.asList(
                new Tuples.IntIntReferenceTriple<>(1, 5, "A"),
                new Tuples.IntIntReferenceTriple<>(6, 10, "B"),
                new Tuples.IntIntReferenceTriple<>(11, 15, "C")
        );

        // Valid searches
        assertEquals("A", searchRanges(1, ranges));  // key at start of a range
        assertEquals("A", searchRanges(5, ranges));  // key at end of a range
        assertEquals("B", searchRanges(8, ranges));  // key in the middle of a range

        // Invalid searches
        assertNull(searchRanges(0, ranges));   // key less than all ranges
        assertNull(searchRanges(16, ranges));  // key greater than all ranges
        assertNull(searchRanges(7, Collections.emptyList()));  // empty range list
        assertNull(searchRanges(7, null));     // null range list
    }
}