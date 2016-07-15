/*
 * (c)
 */
package util.collection;

import java.util.*;

/**
 * An unrolled linked list that maintains an index of its elements. This can be used as either a set or a list, for
 * example giving most of the speed of a list iteration while allowing O(1) containment checks.
 * <p>
 * This implementation reduces rehashing on smaller lists by maintaining up to 4 concurrent index arrays,
 * each twice the size of the last.
 * Caveats:
 * <ol>
 * <li>Assumes no null values (may not check for them).
 * <li>Does no checks for concurrent modification of the collection.
 * </ol>
 * @author Miles
 */
public class LinkedArrayHashSet<T> implements Set<T>, List<T> {

    /**
     * Literal for internal and external use. Anywhere the position of an item in a list
     * is requested, this literal represents the absence of a position.
     */
    public static final int NO_LIST_POS = -1;

   /**
     * Internal literal used to indicate that no index has been created for an item. This is different from
     * NO_LIST_POS to avoid the need to initialise the indexes to -1 when they are created.
     */
    private static final int NO_ELEM_IDX = 0;

    /**
     * Internal literal used to transform NO_LIST_POS to NO_ELEM_IDX (by adding) and visa versa (by subtracting).
     */
    private static final int SHIFT_TO_INTERNAL = NO_ELEM_IDX - NO_LIST_POS;

    /**
     * This collection can not contain more than this number (2 ^ 31 -1) of elements. This is a result
     * of the Collections contract on size() rather than any internal limitation, but also allows us to
     * implement numItemsStored and local indexes as integers.
     */
    public static final int MAX_SIZE = 0x7fffffff;
    
    /**
     * How many items are currently in this collection. The invariant that this is the last
     * free index in elementArrays is maintained.
     */
    private int numItemsStored;

    /**
     * Storage of all elements in this collection. Inner arrays are allocated in ALLOCATION_SIZE chunks
     * when required, up until SEGMENT_SIZE at which point a new outer array is created.
     * This design is conservative on allocating memory while keeping object references grouped in the
     * same cache line (content chasing can still occur for hash functions).
     * The content is always stored contiguously from index 0 to index numItemsStored - 1.
     */
    private Object[][] elementArrays;

    /**
     * The initial inner array allocation size of elementArrays, and how much the inner array is grown each time
     * the allocated space is filled. A power of two may improve speed of the mod operation.
     */
    private static final short ALLOCATION_SIZE = 32;

    /**
     * The maximum size that an inner elementArrays array can grow, once this is reached a new inner array is
     * allocated. Best performing value will depend on cache size, but should be kept as a power of two
     * to help with division speed.
     */
    private static final short SEGMENT_SIZE = 256;

    /**
     * Stores the position of every item in elementArrays in an inner array location uniquely determined by
     * the last n (where 2^n is the inner array size) bits of a integer generated from the items hashCode
     * method (run through another deterministic function to ensure lower order bits are as well distributed
     * as high order bits). When a collision occurs on a store, rather than chaining or open addressing, a
     * higher level array is used, with each higher level being twice the size of the last. This reduces
     * the worst case of put compared to a list as there is less chance of a rehash, at the cost of extra
     * work computing the indexes when modifying. Rehashes still occur as to limit the number of arrays to
     * search and keep related data in cache the total number of outer arrays is limited, when the limit is
     * reached a rehash of the smallest into the second smallest array occurs. Uses int rather than Integer
     * as this will be more compact for large arrays after the disadvantage of not being able to re-use the
     * Integers in the auto-box cache.
     */
    private int[][] elementIndices;

    /**
     * The size of the first elementIndices inner array, must be a power of two. This doubles for every
     * new inner array until Integer.MAX_VALUE. Due to the birthday paradox, collisions can be expected
     * well before INITIAL_INDEX_SIZE are allocated to an inner array.
     */
    private static final short INITIAL_INDEX_SIZE = 16;

    /**
     * The maximum number of index arrays, so for example with a size of n and first inner index size of 16
     * the first rehash will be forced when the nth inner array (size 16*2^n) has its first collision.
     */
    private static final short MAX_NUM_INDEX_ARRAYS = 4;

    /**
     * The largest size an inner array of elementIndices can take. 2^30 is the last power of 2 where
     * all values fit into an integer (2^31 doesnt quite fit which breaks the hash algorithm).
     */
    private static final int MAX_INDEX_SIZE = 1073741824;

    /**
     * Lone constructor for this class, nothing is customizable at runtime.
     */
    public LinkedArrayHashSet() {
        initStructures();
    }

    /**
     * Set the arrays and indices to their initial size. If they already existed they are replaced.
     */
    private void initStructures() {
        elementArrays = new Object[1][ALLOCATION_SIZE];
        elementIndices = new int[1][INITIAL_INDEX_SIZE];    
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int size() {
        return numItemsStored;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEmpty() {
        return numItemsStored == 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean remove(final Object toRemove) {
        int indexRemovedFrom = setToNull(toRemove);
        if (indexRemovedFrom != NO_LIST_POS) {
            // numItemsStored was decremented in setToNull and now points to the last index position
            removeNullsFrom(indexRemovedFrom, numItemsStored);
            return true;
        }
        return false;
    }

    /**
     * Sets the specified object to null and updates its index, but does not fill the null space.
     * This is O(1).
     *
     * @param toRemove element to remove from collection
     * @return the position that the element was removed from.
     */
    private final int setToNull(final Object toRemove) {
        int removingHashcode = getHashFor(toRemove.hashCode());
        for (int outerIdxArrayPos = 0; outerIdxArrayPos < elementIndices.length; outerIdxArrayPos++) {
            int innerIdxArrayPos = getIndexOfItemAtLevel(outerIdxArrayPos, removingHashcode);
            int elementArraysPos = getElementPosAtIndices(outerIdxArrayPos, innerIdxArrayPos);
            // There may be no position yet, no need to search more index arrays in that case
            if (elementArraysPos == NO_LIST_POS) return NO_LIST_POS;
            int positionSegmentIndex = elementArraysPos / SEGMENT_SIZE;
            int positionElementIndex = elementArraysPos % SEGMENT_SIZE;
            Object atPosition = elementArrays[positionSegmentIndex][positionElementIndex];
            assert(atPosition != null);
            // If the element referred to by the hash at this level is the one we want to remove, o/w continue
            if (toRemove.equals(atPosition)) {
                elementArrays[positionSegmentIndex][positionElementIndex] = null;
                updateIndexWithPos(outerIdxArrayPos, innerIdxArrayPos, NO_LIST_POS);
                numItemsStored -= 1;
                return elementArraysPos;
            }
        }
        return NO_LIST_POS;
    }

    /**
     * Starting at the specified array position and moving towards the end of the arrays,
     * fill all null elements with the next non-null element in the array and updates
     * the indices of every non-null moved item. Assumes arrayStartPos references a null
     * and arrayEndPos is the last item to be moved backwards.
     */
    private void removeNullsFrom(final int arrayStartPos, final int arrayEndPos) {
        // Index for the position to copy data to.
        int fillToInner = arrayStartPos % SEGMENT_SIZE;
        int fillToOuter = arrayStartPos / SEGMENT_SIZE;
        // Last position to iterate to.
        final int finalPosInner = arrayEndPos % SEGMENT_SIZE;
        final int finalPosOuter = arrayEndPos / SEGMENT_SIZE;
        // Index for the position to copy data from.
        int fillFromInner = fillToInner + 1;
        int fillFromOuter = fillFromInner / SEGMENT_SIZE;
        int curNumNullFills = 1;
        // Iterate from starting point copying each non-null item back to fill the last fillToIndex
        while (fillFromOuter <= finalPosOuter && fillFromInner <= finalPosInner) {
            Object curItem = elementArrays[fillFromOuter][fillFromInner];
            if (curItem != null) {
                // Shift the item backwards by curNumNullFills in the elementArrays
                elementArrays[fillToOuter][fillToInner] = curItem;
                elementArrays[fillFromOuter][fillFromInner] = null;
                // Update index for every non-null curItem (null curItem indices were already removed in setToNull)
                final int curItemHash = getHashFor(curItem.hashCode());
                // Find items hashbucket and decrement the elementArray position it points to by curNumNullFills
                for (int outerIdxArrayPos = 0; outerIdxArrayPos < elementIndices.length; outerIdxArrayPos++) {
                    final int innerIdxArrayPos = getIndexOfItemAtLevel(outerIdxArrayPos, curItemHash);
                    final int elementPos = getElementPosAtIndices(outerIdxArrayPos, innerIdxArrayPos);
                    // If this is the element we are looking for
                    if (elementPos == (fillFromOuter * SEGMENT_SIZE) + fillFromInner) {
                        // Updates the index to the new location this item will occupy in elementArraysPos.
                        updateIndexWithPos(outerIdxArrayPos, innerIdxArrayPos, elementPos - curNumNullFills);
                        break;
                    }
                }
                // fillFrom may be more than 1 ahead of fillTo due to the presences of multiple nulls.
                // We need to fill each of these with later fillFrom items by incrementing by 1 the fillTo index.
                fillToInner = ++fillToInner % SEGMENT_SIZE;
                fillToOuter = fillToInner == 0 ? fillToOuter + 1 : fillToOuter;
            } else {
                curNumNullFills += 1;
            }
            fillFromInner = ++fillFromInner % SEGMENT_SIZE;
            fillFromOuter = fillFromInner == 0 ? fillFromOuter + 1 : fillFromOuter;
        }
    }

    /**
     * If toAdd is null or we already contain Integer.Max elements then return false (java collections do not
     * support more than this in size method)
     */
    @Override
    public boolean add(final T toAdd) {
        if (toAdd == null || numItemsStored >= MAX_SIZE) {
            return false;
        }
        int hashcode = getHashFor(toAdd.hashCode());
        int outerIdxArrayForHash = 0;
        for (; outerIdxArrayForHash < elementIndices.length; outerIdxArrayForHash++) {
            int elementArraysPos = getElementPosAtIndices(outerIdxArrayForHash,
                                                          getIndexOfItemAtLevel(outerIdxArrayForHash, hashcode));
            // No need to search more index arrays if there is no element here
            if (elementArraysPos == NO_LIST_POS) break;
            // If there is a match then toAdd is already in the array and we cannot proceed.
            assert(getObjectAt(elementArraysPos) != null);
            if (toAdd.equals(getObjectAt(elementArraysPos))) return false;
        }
        // The index into the current segment inner array
        int storeAt = numItemsStored;
        int innerArrayIndex = storeAt % SEGMENT_SIZE;
        int outerArrayIndex = storeAt / SEGMENT_SIZE;
        // If the inner index has wrapped around, allocate a new outer array SEGMENT_SIZE segment
        if (outerArrayIndex == elementArrays.length) {
            // A new elements array. The first ALLOCATION_SIZE reference space is preallocated in the new inner array
            Object[][] newElements = new Object[elementArrays.length+1][ALLOCATION_SIZE];
            // This is faster than a manual copy after about 20 segments. This only copies the outer array and
            // not the records themselves, so this will be fast.
            System.arraycopy(elementArrays, 0, newElements, 0, elementArrays.length);
            elementArrays = newElements;
        }
        // Otherwise if the inner array has filled up to its allocation size, reallocate a larger inner array section
        else if (storeAt > 0 && innerArrayIndex % ALLOCATION_SIZE == 0) {
            int numItemsInSegment = elementArrays[elementArrays.length-1].length;
            Object[] resized = new Object[numItemsInSegment + ALLOCATION_SIZE];
            System.arraycopy(elementArrays[elementArrays.length-1], 0, resized, 0, numItemsInSegment);
            elementArrays[elementArrays.length-1] = resized;
        }
        elementArrays[outerArrayIndex][innerArrayIndex] = toAdd;
        // Now we need to add indexToStoreAt to elementIndices
        // We already know the hashslots in every array before outerIdxArrayForHash are filled, so start from there
        // Note outerIdxArrayForHash is one above the last collision due to post increment
        addIndexFor(outerIdxArrayForHash, storeAt, hashcode);
        rehash();
        assert(get(numItemsStored) == toAdd);
        numItemsStored+=1;
        return true;
    }

    /**
     * Adds an index at or above the specified outer index array to the specified elementPos using the specified
     * elementHashcode to determine the position in the inner array to use. If the inner array does not have that
     * position available a larger inner array with that position free will be found and used, creating a new largest
     * index array if necessary. This is allowed to break the maximum number of index arrays invariant, this must be
     * restored by calling rehash() after a call to this method.
     *
     * @param outerIdxArrayPos first position in the outer index array of the inner index array where the element should
     *                         be added. If the element's hash slot is not available in the inner array, a larger outer
     *                         index will be used.
     * @param elementPos the position in the element array that an index should be created for.
     * @param elementHashcode of the element being indexed. This is used to determine the location of the index in the
     *                        inner array.
     */
    private void addIndexFor(int outerIdxArrayPos, int elementPos, int elementHashcode) {
        // Calculate for each idxArrayPos the idxPos that elementHashcode should occupy. Starts from idxArrayPos and
        // moves up through the remaining indices, finishing if a match is found or no more index arrays exist.
        int innerIdxArrayPos = NO_LIST_POS;
        while (outerIdxArrayPos < elementIndices.length &&
               NO_ELEM_IDX != elementIndices[outerIdxArrayPos][
               // Assign as well as use the hash bucket for the hashcode at this level
               innerIdxArrayPos = elementHashcode & (elementIndices[outerIdxArrayPos].length - 1)]) {
            outerIdxArrayPos++;
        }
        // If we have run out of index arrays then we need to add a new one.
        if (outerIdxArrayPos == elementIndices.length) {
            // New elementIndices, no preallocation of inner arrays as the old ones will be copied and the final created
            int[][] newIndices = new int[elementIndices.length + 1][];
            // This is faster than a manual copy after about 20 segments. This only copies the outer array and
            // not the inner hash arrays, so this will be fast.
            System.arraycopy(elementIndices, 0, newIndices, 0, elementIndices.length);
            elementIndices = newIndices;
            // The new inner array should be twice the size of the current largest inner array.
            int newSize = 2 * elementIndices[elementIndices.length-2].length;
            // Make sure it never exceeds the largest power of 2 that can be allocated
            newSize = newSize < MAX_INDEX_SIZE ? newSize : MAX_INDEX_SIZE;
            elementIndices[elementIndices.length-1] = new int[newSize];
            // Calculate the inner index that the element should use in the new array
            innerIdxArrayPos = elementHashcode & (elementIndices[outerIdxArrayPos].length - 1);
        }
        // Should be assigning to an empty index position
        assert(elementIndices[outerIdxArrayPos][innerIdxArrayPos] == NO_ELEM_IDX);
        // Change the index at the outer and inner index array position to the specified element position
        updateIndexWithPos(outerIdxArrayPos, innerIdxArrayPos, elementPos);
    }

    /**
     * If there are too many index arrays, this moves element indices from smaller to larger index arrays where they
     * can be accommodated and removes the smallest arrays.
     */
    private void rehash() {
        // Rehash if there is now too many indices (slowing down access). Continue until reduced to the max.
        while (elementIndices.length > MAX_NUM_INDEX_ARRAYS) {
            // Iterate through the first (smallest) index array, copying any active indices to later index arrays
            final int[] curSmallestIdxArray = elementIndices[0];
            for (int hashIdx = 0; hashIdx < curSmallestIdxArray.length; hashIdx++) {
                if (curSmallestIdxArray[hashIdx] != NO_ELEM_IDX) {
                    int elementPos = getElementPosAtIndices(0, hashIdx);
                    int fullHashCode = getObjectAt(elementPos).hashCode();
                    // Try and allocate, starting from the array above the one being rehashed and stopping at either
                    // the last index array, or if necessary creating a new index array which will cause another loop.
                    addIndexFor(1, elementPos, fullHashCode);
                }
            }
            // Now the iteration is complete, can remove the first index array and set the second to be the new first.
            int[][] newIndices = new int[elementIndices.length - 1][];
            System.arraycopy(elementIndices, 1, newIndices, 0, elementIndices.length - 1);
            elementIndices = newIndices;
            // If there are still too many index arrays, another loop will occur and remove the new first index array.
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean contains(Object toCheck) {
        return indexOf(toCheck) != NO_LIST_POS;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int indexOf(final Object toCheck) {
        if (toCheck == null) return NO_LIST_POS;
        int toCheckHash = getHashFor(toCheck.hashCode());
        for (int idxArrayNum = 0; idxArrayNum < elementIndices.length; idxArrayNum++) {
            int elementArraysPos = getElementPosAtIndices(idxArrayNum, getIndexOfItemAtLevel(idxArrayNum, toCheckHash));
            // There may be no position yet, no need to search more index arrays in that case
            if (elementArraysPos == NO_LIST_POS) {
                break;
            }
            else if (getObjectAt(elementArraysPos).equals(toCheck)) {
                return elementArraysPos;
            }
        }
        return NO_LIST_POS;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int lastIndexOf(Object toCheck) {
        // There is only one index for an item in a set
        return indexOf(toCheck);
    }
         
    /**
     * Simple function that spreads the specified hash's upper order bits
     * over its lower order bits. This is needed to reduce collisions in smaller indexes.
     */
    private static final int getHashFor(final int hash) {
        return hash ^ (hash >>> 16);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object[] toArray() {
        Object[] flattenedArray = new Object[numItemsStored];
        for (int i = 0, numCopied = 0; numCopied < numItemsStored; i++) {
            final int remainingNumItemsToCopy = numItemsStored - numCopied;
            final int numToCopyInThisArray = Math.min(elementArrays[i].length, remainingNumItemsToCopy);
            final int startReadingAtPos = 0;
            final int startWritingAtPos = numCopied;
            System.arraycopy(elementArrays[i], startReadingAtPos,
                             flattenedArray,   startWritingAtPos, numToCopyInThisArray);
            numCopied += numToCopyInThisArray;
        }
        return flattenedArray;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T[] toArray(T[] a) {
        int numCopied = 0;
        // a.length may be less than numItemsStored, List contract requires us to resize
        if (a.length < numItemsStored) {
            a = (T[])new Object[numItemsStored];
        }
        for (int i = 0; numCopied < numItemsStored; i++) {
            final int remainingNumItemsToCopy = numItemsStored - numCopied;
            final int numToCopyInThisArray = Math.min(elementArrays[i].length, remainingNumItemsToCopy);
            final int startReadingAtPos = 0;
            final int startWritingAtPos = numCopied;
            System.arraycopy(elementArrays[i], startReadingAtPos,
                             a, startWritingAtPos, numToCopyInThisArray);
            numCopied += numToCopyInThisArray;
        }
        for (; numCopied < a.length; numCopied++) {
            a[numCopied] = null;
        }
        return a;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean containsAll(Collection<?> c) {
        boolean containsAll = true;
        // Use c`s iterator. 
        for (Object cElem : c) {
            containsAll &= contains(cElem);
        }
        return containsAll;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean addAll(Collection<? extends T> c) {
        boolean addedAny = false;
        // Use c`s iterator. 
        for (T cElem : c) {
            addedAny |= add(cElem);
        }
        return addedAny;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean retainAll(Collection<?> c) {
        boolean modified = false;
        Iterator<T> it = iterator();
        while (it.hasNext()) {
            if (!c.contains(it.next())) {
                it.remove();
                modified = true;
            }
        }
        return modified;    
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean removeAll(Collection<?> c) {
        int lowestRemoval = numItemsStored;
        // numItemsStored is decremented in the loop. Keep the original last index to know where to clear indices.
        final int lastIndexedArrayPos = numItemsStored - 1;
        for (Object cElem: c) {
            int indexOfcElem = setToNull(cElem);
            if (indexOfcElem != NO_LIST_POS) {
                lowestRemoval = Math.min(lowestRemoval, indexOfcElem);
            }
        }
        removeNullsFrom(lowestRemoval, lastIndexedArrayPos);
        return lowestRemoval < numItemsStored;
    }

    /**
     * {@inheritDoc}
     * <p>
     *  This implementation reallocates the initial size, as the outer array
     *  size of the indices and elements will have to be reallocated on add anyway.
     *  This will not be as performant as an array list for repeated clear and add cycles.
     * </p>
     */
    @Override
    public void clear() {
        initStructures();
        numItemsStored = 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
        // Use c`s iterator.
        for (T cElem : c) {
            add(index++, cElem);
        }
        return true;
    }

   /**
     * {@inheritDoc}
     * <p>
     * Throws {@link ArrayIndexOutOfBoundsException} if the iteration has no next element
     */
    @Override
    public T get(int index) {
        return (T)getObjectAt(index);
    }

    /**
     * Apply the length of the specified index level as a mask (i.e. 15 -> & 1111) to get the
     * position that the element with the specified hashcode would have it were in this index array
     */
    private int getIndexOfItemAtLevel(int indexLevel, int itemHashcode) {
        return itemHashcode & (elementIndices[indexLevel].length - 1);
    }
    
    /**
     * The element position that the specified index position points to.
     */
    private int getElementPosAtIndices(int outerIdxPos, int innerIdxPos) {
        return elementIndices[outerIdxPos][innerIdxPos] - SHIFT_TO_INTERNAL;
    }
    
    /**
     * Changes the specified index array position to refer to the array set element at the specified array position.
     *
     * @param outerIdxPos
     * @param innerIdxPos
     * @param posInArray Either NO_LIST_POS or an index referring to a non-null element of this array set.
     */
    private void updateIndexWithPos(int outerIdxPos, final int innerIdxPos, final int posInArray) {
        boolean changingValue = (elementIndices[outerIdxPos][innerIdxPos] - SHIFT_TO_INTERNAL) != posInArray;
        if (changingValue) {
            setIndexTo(outerIdxPos,innerIdxPos, posInArray + SHIFT_TO_INTERNAL);
            // If an existing value was removed without a replacement, shift all later indices back by 1.
            if (posInArray == NO_LIST_POS) {
                for (outerIdxPos += 1; outerIdxPos < elementIndices.length &&
                                       elementIndices[outerIdxPos][innerIdxPos] != NO_ELEM_IDX; outerIdxPos++) {
                    assert(elementIndices[outerIdxPos - 1][innerIdxPos] == NO_ELEM_IDX);
                    setIndexTo(outerIdxPos - 1, innerIdxPos, elementIndices[outerIdxPos][innerIdxPos]);
                    setIndexTo(outerIdxPos, innerIdxPos, NO_ELEM_IDX);
                }
            }
        }
    }

    /**
     * Changes the specified index array position to the specified value.
     *
     * @param outerIdxPos
     * @param innerIdxPos
     * @param newIdx Either NO_ELEM_IDX or an index referring to a non-null element of this array set.
     */
    private void setIndexTo(int outerIdxPos, final int innerIdxPos, final int newIdx) {
        assert(newIdx == NO_ELEM_IDX || getObjectAt(newIdx - SHIFT_TO_INTERNAL) != null);
        elementIndices[outerIdxPos][innerIdxPos] = newIdx;
    }

    /**
     * Get the object at the specified index.
     *
     * {@link ArrayIndexOutOfBoundsException} if the index is not within the size of this collection.
     */
    private Object getObjectAt(int index) {
        return elementArrays[index/SEGMENT_SIZE][index%SEGMENT_SIZE];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T set(int index, T element) {
        if (index >= size()) {
            throw new IndexOutOfBoundsException("" + index);
        }
        int segment = index/SEGMENT_SIZE;
        int pos = index%SEGMENT_SIZE;
        T oldItem = (T)elementArrays[segment][pos];
        elementArrays[segment][pos] = element;
        int hashCode = element == null ? 0 : getHashFor(element.hashCode());
        addIndexFor(0, index, hashCode);
        rehash();
        return oldItem;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void add(final int index, final T element) {
        // Replace the old item at the index with this one, retaining the old. Also updates the index arrays
        T oldItemAtIndex = null;
        // If the index == size then adding to end of the list, otherwise if it is less then replacing an item
        int idxCmpToSize = Integer.compare(index, size());
        if (idxCmpToSize < 0) {
            oldItemAtIndex = set(index, element);
        } else if (idxCmpToSize == 0) {
            oldItemAtIndex = element;
        } else if (idxCmpToSize > 0) {
            throw new IndexOutOfBoundsException("" + index);
        }
        numItemsStored += 1;
        if (numItemsStored == SEGMENT_SIZE) {
            // A new elements array. Note that space for the first ALLOCATION_SIZE references is preallocated
            Object[][] newElements = new Object[elementArrays.length+1][ALLOCATION_SIZE];
            // This is faster than a manual copy after about 20 segments. This only copies the outer array and
            // not the records themselves, so this will be fast.
            System.arraycopy(elementArrays, 0, newElements, 0, elementArrays.length);
            elementArrays = newElements;
        }
        else if (numItemsStored == ALLOCATION_SIZE) {
            int numItemsInSegment = elementArrays[elementArrays.length-1].length;
            Object[] resized = new Object[numItemsInSegment + ALLOCATION_SIZE];
            System.arraycopy(elementArrays[elementArrays.length-1], 0, resized, 0, numItemsInSegment); 
            elementArrays[elementArrays.length-1] = resized;
        }
        // Since we don`t care about ordering, move any existing item to the end of the list. Also updates index arrays
        if (oldItemAtIndex != null) {
            set(numItemsStored-1, oldItemAtIndex);
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * Throws {@link ArrayIndexOutOfBoundsException} if the index is not within the size of this collection.
     */
    @Override
    public T remove(int index) {
        T existingItem = get(index);
        remove(existingItem);
        return existingItem;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Note the endpoints [from,to) (i.e. [from, to - 1]). Standard java implementation is for this method to return an
     * empty list when from == to (i.e. when a range from n to n - 1 is specified).
     * </p>
     */
    @Override
    public List<T> subList(int fromIndexInclusive, int toIndexExclusive) {
        if (fromIndexInclusive > toIndexExclusive) {
            throw new IndexOutOfBoundsException();
        }
        if (fromIndexInclusive == toIndexExclusive) {
            return Collections.emptyList();
        }
        return new SubList(fromIndexInclusive, toIndexExclusive - 1, 0, numItemsStored);
    }

    private class SubList implements List<T> {
        
        int size;
        int startIdx;
        int endIdx;

        /**
         * @param index1 Index of the first element referred to by this sublist
         * @param index2 Index of the last element referred to by this sublist
         * @param minIndex The smallest value allowed for an index of this sublist. Used to normalise index1 and index2.
         * @param maxIndex The largest values allowed for an index of this sublist. Used to normalise index1 and index2.
         */
        SubList(int index1, int index2, int minIndex, int maxIndex) {
            // index1, normalised to between min and max, is the start
            startIdx = Math.min(Math.max(index1, minIndex), maxIndex);
            // index2, normalised to between min and max, is the end
            endIdx = Math.min(Math.max(index2, minIndex), maxIndex);
            size = endIdx - startIdx + 1;
        }

        @Override
        public int size() {
            return size;
        }

        @Override
        public boolean isEmpty() {
            return size == 0;
        }
        
        @Override
        public int indexOf(Object toCheck) {
            int outerIndex = LinkedArrayHashSet.this.indexOf(toCheck); // 3
            if (outerIndex > endIdx || outerIndex < startIdx) {
                outerIndex = NO_LIST_POS;
            } else {
                outerIndex -= startIdx;
            }
            return outerIndex;
        }

        @Override
        public int lastIndexOf(Object o) {
            // Does not allow duplicates
            return indexOf(o);
        }
        
        @Override
        public boolean contains(Object o) {
            return indexOf(o) != NO_LIST_POS;
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            boolean containsAll = true;
            for (Object cElem : c) {
                containsAll &= contains(cElem);
            }
            return containsAll;
        }
        
       /**
        * {@inheritDoc}
        * <p>
        * No Exception is thrown if the index is out of bounds of this sub-array. An ArrayIndexOutOfBounds will be
        * thrown if the index is not in the range of the backing list.
        */
        @Override
        public T get(int relativeIndex) {
            int absoluteIndex = relativeIndex + startIdx;
            return LinkedArrayHashSet.this.get(absoluteIndex);
        }

       /**
        * {@inheritDoc}
        * <p>
        * No Exception is thrown if the index is out of bounds of this sub-array. An ArrayIndexOutOfBounds will be
        * thrown if the index is not in the range of the backing list.
        */
        @Override
        public T set(int relativeIndex, T element) {
            int absoluteIndex = relativeIndex + startIdx;
            return LinkedArrayHashSet.this.set(absoluteIndex, element);
        }

        @Override
        public boolean add(T e) {
            // Adds e to the end of this sub-list in the top level list
            LinkedArrayHashSet.this.add(endIdx, e);
            return true;
        }

        @Override
        public boolean addAll(Collection<? extends T> c) {
            boolean anyAdded = false;
            for (T cElem : c) {
                anyAdded |= add(cElem);
            }
            return anyAdded;
        }
        
       /**
        * {@inheritDoc}
        * <p>
        * No Exception is thrown if the index is out of bounds of this sub-array. An ArrayIndexOutOfBounds will be
        * thrown if the index is not in the range of the backing list.
        */
        @Override
        public void add(int relativeIndex, T element) {
            int absoluteIndex = relativeIndex + startIdx;
            LinkedArrayHashSet.this.add(absoluteIndex, element);
        }
        
       /**
        * {@inheritDoc}
        * <p>
        * No Exception is thrown if the index is out of bounds of this sub-array. An ArrayIndexOutOfBounds will be
        * thrown if the index is not in the range of the backing list.
        */
        @Override
        public boolean addAll(int relativeIndex, Collection<? extends T> c) {
            int absoluteIndex = relativeIndex + startIdx;
            for (T cElem : c) {
                LinkedArrayHashSet.this.add(absoluteIndex, cElem);
            }
            return true;
        }
        
        @Override
        public boolean remove(Object o) {
            int indexOf = LinkedArrayHashSet.this.indexOf(o);
            boolean removeableFromSublist = indexOf >= startIdx && indexOf <= endIdx;
            if (removeableFromSublist) {
                LinkedArrayHashSet.this.remove(indexOf);
            }
            return removeableFromSublist;
        }
        
       /**
        * {@inheritDoc}
        * <p>
        * No Exception is thrown if the index is out of bounds of this sub-array. An ArrayIndexOutOfBounds will be
        * thrown if the index is not in the range of the backing list.
        */
        @Override
        public T remove(int relativeIndex) {
            int absoluteIndex = relativeIndex + startIdx;
           return LinkedArrayHashSet.this.remove(absoluteIndex);
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            boolean anyRemoved = false;
            for (Object cElem : c) {
                anyRemoved |= remove(cElem);
            }
            return anyRemoved;        
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            boolean modified = false;
            Iterator<T> it = iterator();
            while (it.hasNext()) {
                if (!c.contains(it.next())) {
                    it.remove();
                    modified = true;
                }
            }
            return modified;          
        }

        @Override
        public void clear() {
            for (int i = startIdx; i <= endIdx; i++) {
                setToNull(LinkedArrayHashSet.this.get(i));
            }
            removeNullsFrom(startIdx, endIdx);
        }

        @Override
        public Iterator<T> iterator() {
            return listIterator(0);
        }
        
        @Override
        public ListIterator<T> listIterator() {
            return listIterator(0);
        }
        
        @Override
        public ListIterator<T> listIterator(int index) {
            return new ArraySetIterator(startIdx + index) {
                @Override
                public boolean hasNext() {
                    return cursor < endIdx;
                }
            };        
        }

        @Override
        public Object[] toArray() {
            Object[] flattenedArray = new Object[size];
            int startReadingAtPos = startIdx % SEGMENT_SIZE;
            for (int i = startIdx / SEGMENT_SIZE, numCopied = 0; numCopied < size; i++) {
                final int remainingNumItemsToCopy = size - numCopied;
                final int startWritingAtPos = numCopied;
                final int numToCopyInThisArray = Math.min(elementArrays[i].length, remainingNumItemsToCopy);
                System.arraycopy(elementArrays[i], startReadingAtPos,
                                 flattenedArray, startWritingAtPos, numToCopyInThisArray);
                numCopied += numToCopyInThisArray;
                startReadingAtPos += numCopied % SEGMENT_SIZE;
            }
            return flattenedArray;
        }

        @Override
        public <T> T[] toArray(T[] flattenedArray) {
            int numCopied = 0;
            // a.length may be less than numItemsStored, List contract requires us to resize
            if (flattenedArray.length < size) {
                flattenedArray = (T[])new Object[size];
            }
            final int startReadingAtPos = startIdx % SEGMENT_SIZE;
            for (int i = startIdx / SEGMENT_SIZE; numCopied < size; i++) {
                final int remainingNumItemsToCopy = size - numCopied;
                final int startWritingAtPos = numCopied;
                final int numToCopyInThisArray = Math.min(elementArrays[i].length, remainingNumItemsToCopy);
                System.arraycopy(elementArrays[i], startReadingAtPos,
                                 flattenedArray, startWritingAtPos, numToCopyInThisArray);
                numCopied += numToCopyInThisArray;
            }
            for (; numCopied < flattenedArray.length; numCopied++) {
                flattenedArray[numCopied] = null;
            }
            return flattenedArray;
        }

        @Override
        public List<T> subList(int fromIndexInclusive, int toIndexExclusive) {
            if (fromIndexInclusive > toIndexExclusive) {
                throw new IndexOutOfBoundsException();
            }
            if (fromIndexInclusive == toIndexExclusive) {
                return Collections.emptyList();
            }
            return new SubList(startIdx + fromIndexInclusive, startIdx + toIndexExclusive - 1, startIdx, endIdx);
        }  
    }
    
    @Override
    public Spliterator<T> spliterator() {
        return Spliterators.spliterator(this, Spliterator.ORDERED);
    }
    
    @Override
    public Iterator<T> iterator() {
        return listIterator();
    }
    
    @Override
    public ListIterator<T> listIterator() {
        return listIterator(0); 
    }

    @Override
    public ListIterator<T> listIterator(int index) {
        return new ArraySetIterator(index);
    }
    
    private class ArraySetIterator implements ListIterator<T> {
        
        /**
         * Index of next element to return.
         */
        protected int cursor;
        ArraySetIterator(int startPos) {
           cursor = startPos;
        }

       /**
        * {@inheritDoc}
        */
        @Override
        public boolean hasNext() {
            return cursor != numItemsStored;
        }

       /**
        * {@inheritDoc}
        * <p>
        * No concurrent modification check is done.
        * <p>
        * Throws {@link ArrayIndexOutOfBoundsException} if the iteration has no next element
        */
        @Override
        public T next() {
            return get(cursor++);
        }

        @Override
        public boolean hasPrevious() {
            return cursor != 0;
        }

       /**
        * {@inheritDoc}
        * <p>
        * No concurrent modification check is done.
        * <p>
        * Throws {@link ArrayIndexOutOfBoundsException} if the iteration has no next element
        */
        @Override
        public T previous() {
            return get(--cursor);
        }

        @Override
        public int nextIndex() {
            return cursor;
        }

        @Override
        public int previousIndex() {
            return cursor - 1;
        }

       /**
        * {@inheritDoc}
        * <p>
        * No concurrent modification check is done.
        * <p>
        * Throws {@link ArrayIndexOutOfBoundsException}if the iteration has no next element
        */
        @Override
        public void remove() {
            LinkedArrayHashSet.this.remove(cursor--);
        }

       /**
        * {@inheritDoc}
        * <p>
        * No concurrent modification check is done.
        * <p>
        * Throws {@link ArrayIndexOutOfBoundsException} if the iteration has no next element
        */
        @Override
        public void set(T e) {
            LinkedArrayHashSet.this.set(cursor, e);
        }

       /**
        * {@inheritDoc}
        * <p>
        * No concurrent modification check is done.
        * <p>
        * Throws {@link ArrayIndexOutOfBoundsException} if the iteration has no next element
        */
        @Override
        public void add(T e) {
            LinkedArrayHashSet.this.add(cursor++, e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return Arrays.toString(elementArrays);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof LinkedHashSet))
            return false;

        ListIterator<T> e1 = listIterator();
        ListIterator<?> e2 = ((List<?>) o).listIterator();
        while (e1.hasNext() && e2.hasNext()) {
            T o1 = e1.next();
            Object o2 = e2.next();
            if (!(o1==null ? o2==null : o1.equals(o2)))
                return false;
        }
        return !(e1.hasNext() || e2.hasNext());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int hashCode = 1;
        for (T e : this)
            hashCode = 31 * hashCode + (e==null ? 0 : e.hashCode());
        return hashCode;
    }
}
