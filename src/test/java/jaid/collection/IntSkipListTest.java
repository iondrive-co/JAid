package jaid.collection;

import org.junit.Test;

import java.util.Iterator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class IntSkipListTest {

    @Test
    public void testAddAndRemove() {
        IntSkipList list = new IntSkipList();
        assertThat(list.isEmpty()).isTrue();

        list.add(5);
        list.add(10);
        assertThat(list.contains(5)).isTrue();
        assertThat(list.contains(10)).isTrue();
        assertThat(list.contains(15)).isFalse();
        assertThat(list.contains(-1)).isFalse();
        assertThat(list.contains(0)).isFalse();
        assertThat(list.isEmpty()).isFalse();
        assertThat(list.size()).isEqualTo(2);
        assertArrayEquals(new int[]{5, 10}, list.toArray());

        list.remove(5);
        assertThat(list.contains(5)).isFalse();
        assertThat(list.contains(10)).isTrue();
        assertThat(list.size()).isEqualTo(1);
        assertArrayEquals(new int[]{10}, list.toArray());
        list.remove(10);
        assertThat(list.contains(5)).isFalse();
        assertThat(list.contains(10)).isFalse();
        assertThat(list.size()).isEqualTo(0);
        assertThat(list.isEmpty()).isTrue();
    }

    @Test
    public void testPollFirstAndPollLast() {
        IntSkipList list = new IntSkipList();
        list.add(5);
        list.add(3);
        list.add(7);

        assertThat(list.pollFirst()).isEqualTo(3);
        assertThat(list.pollLast()).isEqualTo(7);
        assertThat(list.size()).isEqualTo(1);

        assertThat(list.pollFirst()).isEqualTo(5);
        assertThat(list.pollFirst()).isNull();
    }

    @Test
    public void testIteratorAndNavigationMethods() {
        IntSkipList list = new IntSkipList();
        list.add(5);
        list.add(3);
        list.add(7);

        Iterator<Integer> iterator = list.iterator();
        assertTrue(iterator.hasNext());
        assertEquals(Integer.valueOf(3), iterator.next());
        assertTrue(iterator.hasNext());
        assertEquals(Integer.valueOf(5), iterator.next());
        assertTrue(iterator.hasNext());
        assertEquals(Integer.valueOf(7), iterator.next());
        assertFalse(iterator.hasNext());

        assertEquals(Integer.valueOf(5), list.lower(7));
        assertEquals(Integer.valueOf(5), list.floor(5));
        assertEquals(Integer.valueOf(7), list.floor(7));
        assertEquals(Integer.valueOf(5), list.ceiling(5));
        assertEquals(Integer.valueOf(7), list.higher(5));
    }

    @Test
    public void testLargeList() {
        final IntSkipList list = new IntSkipList();
        for (int i = 0; i < 1_000_000; i++) {
            list.add(i);
        }
        assertThat(list.size()).isEqualTo(1_000_000);
        assertThat(list.pollLast()).isEqualTo(999_999);
        assertThat(list.pollFirst()).isEqualTo(0);
        assertThat(list.size()).isEqualTo(999_998);
        for (int i = 0; i < 1_000_000; i++) {
            if (i % 2 == 0) {
                list.remove(i);
            }
        }
        assertThat(list.size()).isEqualTo(499_999);
        assertThat(list.pollFirst()).isEqualTo(1);
    }
}