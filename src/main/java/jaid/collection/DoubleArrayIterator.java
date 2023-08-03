package jaid.collection;

import it.unimi.dsi.fastutil.doubles.DoubleIterator;

import java.lang.reflect.Array;
import java.util.NoSuchElementException;

/**
 */
public class DoubleArrayIterator implements DoubleIterator {

    /** The array to iterate over */
    final Object array;
    /** The start index to loop from */
    final int startIndex;
    /** The end index to loop to */
    final int endIndex;
    /** The current iterator index */
    int index = 0;

    // Constructors
    // ----------------------------------------------------------------------
    /**
     * Constructs an ArrayIterator that will iterate over the values in the
     * specified array.
     *
     * @param array the array to iterate over.
     * @throws IllegalArgumentException if <code>array</code> is not an array.
     * @throws NullPointerException if <code>array</code> is <code>null</code>
     */
    public DoubleArrayIterator(final Object array) {
        this(array, 0);
    }

    /**
     * Constructs an ArrayIterator that will iterate over the values in the
     * specified array from a specific start index.
     *
     * @param array  the array to iterate over.
     * @param startIndex  the index to start iterating at.
     * @throws IllegalArgumentException if <code>array</code> is not an array.
     * @throws NullPointerException if <code>array</code> is <code>null</code>
     * @throws IndexOutOfBoundsException if the index is invalid
     */
    public DoubleArrayIterator(final Object array, final int startIndex) {
        this(array, startIndex, Array.getLength(array));
    }

    /**
     * Construct an ArrayIterator that will iterate over a range of values
     * in the specified array.
     *
     * @param array  the array to iterate over.
     * @param startIndex  the index to start iterating at.
     * @param endIndex  the index to finish iterating at.
     * @throws IllegalArgumentException if <code>array</code> is not an array.
     * @throws NullPointerException if <code>array</code> is <code>null</code>
     * @throws IndexOutOfBoundsException if either index is invalid
     */
    public DoubleArrayIterator(final Object array, final int startIndex, final int endIndex) {
        super();

        this.array = array;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.index = startIndex;

        final int len = Array.getLength(array);
        checkBound(startIndex, len, "start");
        checkBound(endIndex, len, "end");
        if (endIndex < startIndex) {
            throw new IllegalArgumentException("End index must not be less than start index.");
        }
    }


    /**
     * Checks whether the index is valid or not.
     *
     * @param bound  the index to check
     * @param len  the length of the array
     * @param type  the index type (for error messages)
     * @throws IndexOutOfBoundsException if the index is invalid
     */
    protected void checkBound(final int bound, final int len, final String type ) {
        if (bound > len) {
            throw new ArrayIndexOutOfBoundsException(
                    "Attempt to make an ArrayIterator that " + type +
                            "s beyond the end of the array. "
            );
        }
        if (bound < 0) {
            throw new ArrayIndexOutOfBoundsException(
                    "Attempt to make an ArrayIterator that " + type +
                            "s before the start of the array. "
            );
        }
    }

    /**
     * Returns the next element in the array.
     *
     * @return the next element in the array
     * @throws NoSuchElementException if all the elements in the array
     *  have already been returned
     */
    @Override
    public double nextDouble() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        return (double)Array.get(array, index++);
    }

    @Override
    public int skip(final int n) {
        int i = n;
        while (i-- != 0 && hasNext())
            nextDouble();
        return n - i - 1;
    }

    // Iterator interface
    //-----------------------------------------------------------------------
    /**
     * Returns true if there are more elements to return from the array.
     *
     * @return true if there is a next element to return
     */
    @Override
    public boolean hasNext() {
        return index < endIndex;
    }

    /**
     * Returns the next element in the array.
     *
     * @return the next element in the array
     * @throws NoSuchElementException if all the elements in the array
     *  have already been returned
     */
    @Override
    public Double next() {
        return nextDouble();
    }
}
