package jaid.collection;


import jaid.number.GeometricRandom;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Non threadsafe sorted collection of ints
 */
public class IntSkipList {

    /**
     * The value should not be higher than the maximum value produced by {@link GeometricRandom#nextByte}.
     */
    private static final int MAX_LEVEL = 32;

    /**
     * Special random number source that generates numbers with the geometric probabilities required for a skip list.
     */
    private final GeometricRandom random = new GeometricRandom();

    /**
     * The head node is the starting point and has the lowest possible int value.
     */
    private final Node head = new Node(Integer.MIN_VALUE, new Node[MAX_LEVEL]);

    /**
     * The forward array holds references to next nodes in the skiplist for every level.
     */
    private record Node(int value, Node[] forward) { }

    /**
     * Current level of the skiplist.
     */
    private int level;

    /**
     * The number of elements in the collection
     */
    private int size = 0;

    /**
     * Check if this collection contains the specified value
     */
    public boolean contains(int toFind) {
        // Start from the head node and highest level, and move right in the level as long as the next node's value is
        // less than the target value. If the next node's value is greater, move down a level. Repeat until the target
        // value is found, or we have traversed to a level where the next node's value is greater than the target.
        Node x = head;
        for (int i = level; i >= 0; i--) {
            while (x.forward[i] != null && x.forward[i].value < toFind) {
                x = x.forward[i];
            }
        }
        x = x.forward[0];
        return x != null && x.value == toFind;
    }

    /**
     * Adds the element with the specified value to this collection
     */
    public void add(int value) {
        Node[] update = new Node[MAX_LEVEL];
        Node x = head;
        for (int i = level; i >= 0; i--) {
            while (x.forward[i] != null && x.forward[i].value < value) {
                x = x.forward[i];
            }
            update[i] = x;  // store the node that may need to be updated at level i
        }
        x = x.forward[0];

        // check if a node with the same value already exists
        if (x == null || x.value != value) {
            // determine level of the new node
            int newLevel = random.nextByte();
            // if new level is greater than current level of skiplist, initialize update value with head
            if (newLevel > level) {
                for (int i = level + 1; i <= newLevel; i++) {
                    update[i] = head;
                }
                // update the current level of the skiplist
                level = newLevel;
            }
            // create and insert the new node
            x = new Node(value, new Node[MAX_LEVEL]);
            for (int i = 0; i <= newLevel; i++) {
                x.forward[i] = update[i].forward[i];
                update[i].forward[i] = x;
            }
            size++;
        }
    }

    /**
     * Removes the element with the specified value from this collection
     */
    public void remove(int value) {
        Node[] update = new Node[MAX_LEVEL];
        Node x = head;
        for (int i = level; i >= 0; i--) {
            while (x.forward[i] != null && x.forward[i].value < value) {
                x = x.forward[i];
            }
            update[i] = x;
        }
        x = x.forward[0];

        if (x != null && x.value == value) {
            for (int i = 0; i <= level; i++) {
                if (update[i].forward[i] != x) {
                    break;
                }
                update[i].forward[i] = x.forward[i];
            }

            while (level > 0 && head.forward[level] == null) {
                level--;
            }
            size--;
        }
    }

    /**
     * Returns the greatest element in this list strictly less than the given element,
     * or {@code null} if there is no such element.
     */
    public Integer lower(int value) {
        Node x = head;
        for (int i = level; i >= 0; i--) {
            while (x.forward[i] != null && x.forward[i].value < value) {
                x = x.forward[i];
            }
        }
        return (x != head) ? x.value : null;
    }

    /**
     * Returns the greatest element in this set less than or equal to the given element,
     * or {@code null} if there is no such element.
     */
    public Integer floor(int value) {
        Node x = head;
        for (int i = level; i >= 0; i--) {
            while (x.forward[i] != null && x.forward[i].value <= value) {
                x = x.forward[i];
            }
        }
        return (x != head) ? x.value : null;
    }

    /**
     * Returns the least element in this set greater than or equal to the given element,
     * or {@code null} if there is no such element.
     */
    public Integer ceiling(int value) {
        Node x = head;
        for (int i = level; i >= 0; i--) {
            while (x.forward[i] != null && x.forward[i].value < value) {
                x = x.forward[i];
            }
        }
        return (x.forward[0] != null) ? x.forward[0].value : null;
    }

    /**
     * Returns the least element in this set strictly greater than the given element,
     * or {@code null} if there is no such element.
     */
    public Integer higher(int value) {
        Node x = head;
        for (int i = level; i >= 0; i--) {
            while (x.forward[i] != null && x.forward[i].value <= value) {
                x = x.forward[i];
            }
        }
        return (x.forward[0] != null) ? x.forward[0].value : null;
    }

    /**
     * Removes and returns the first (smallest) element of the collection, or null if the collection is empty.
     */
    public Integer pollFirst() {
        if (isEmpty()) {
            return null;
        }
        int first = head.forward[0].value;
        remove(first);
        return first;
    }

    /**
     * Removes and returns the last (largest) element of the collection, or null if the collection is empty.
     */
    public Integer pollLast() {
        if (isEmpty()) {
            return null;
        }
        Node x = head;
        for (int i = level; i >= 0; i--) {
            while (x.forward[i] != null) {
                x = x.forward[i];
            }
        }
        int last = x.value;
        remove(last);
        return last;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public void clear() {
        Arrays.fill(head.forward, null);
        level = 0;
        size = 0;
    }

    public int[] toArray() {
        int[] arr = new int[size];
        Node x = head.forward[0];
        for (int i = 0; x != null; i++) {
            arr[i] = x.value;
            x = x.forward[0];
        }
        return arr;
    }

    public Iterator<Integer> iterator() {
        return new Iterator<>() {
            private Node current = head;

            @Override
            public boolean hasNext() {
                return current.forward[0] != null;
            }

            @Override
            public Integer next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                current = current.forward[0];
                return current.value;
            }
        };
    }
}