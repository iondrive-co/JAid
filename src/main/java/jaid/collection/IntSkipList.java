package jaid.collection;


import static jaid.collection.IntSkipList.Constants.ALREADY_EXISTS;
import static jaid.collection.IntSkipList.Constants.AlreadyExistsException;
import static jaid.collection.IntSkipList.Constants.INIT_VAL;
import static jaid.collection.IntSkipList.Constants.MAX_LEVEL;
import static jaid.collection.IntSkipList.Constants.NO_POSITION;
import static jaid.collection.IntSkipList.Constants.SEGMENT_SIZE;

import jaid.random.GeometricRandom;

/**
 * Non threadsafe array backed skip list that handles up to around {@link java.lang.Integer#MAX_VALUE} entries.
 * <p>
 * TODO Being ported to version using unsafe and stack frames, this implementation is buggy and creates garbage.
 *
 */
public class IntSkipList {

    /**
     * Inner level - an array of up to {@link Constants#SEGMENT_SIZE} of stored ints, sorted ascending.
     * Middle level - all the inner segments that make up a level of the skip list
     * Outer level - each level of the skip list, ordered starting from sparsest and finishing on the full data level.
     */
    private final int[][] skipLists = new int[MAX_LEVEL][2];
    private int[][] data = new int[1][SEGMENT_SIZE];

    private boolean containsInitVal;

    private final GeometricRandom r = new GeometricRandom();

    // TODO getPositionOf
    public boolean contains(final int toCheck) {
        int startAt = 0;
        if (toCheck == INIT_VAL) {
            return containsInitVal;
        }
        int endBefore = skipLists[0].length;
        for (final int[] level : skipLists) {
            int startingLowerLevelPos = 0;
            for (int i = startAt; i < endBefore; i+=2) {
                final int element = level[i];
                // Gone past the start position for the next level, should start from the previous element, if any
                if (element > toCheck) {
                    startAt = startingLowerLevelPos;
                    endBefore = level[i + 1];
                    break;
                } else if (element == toCheck) {
                    return true;
                } else {
                    startingLowerLevelPos = level[i + 1];
                }
            }
        }
        for (int i = startAt; i < endBefore; i++) {
            for (int element: data[i]) {
                if (element == toCheck) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean add(int toAdd) {
        if (toAdd == INIT_VAL) {
            boolean containedInitValue = containsInitVal;
            containsInitVal = true;
            return !containedInitValue;
        }
        // Determine how many promotions should occur if the element can be added
        final int numPromotions = r.nextByte();
        try {
            insertAndPromote(0, numPromotions, 0, toAdd);
            return true;
        } catch (AlreadyExistsException e) {
            // Return false if toAdd is already present
            return false;
        }
    }

    /**
     * @return the segment number that was updated at this level, or NO_POSITION if nothing was updated
     */
    private int insertAndPromote(final int level, int promotionLevel, final int startingSegmentPos, int toAdd)
            throws AlreadyExistsException {
        int[] levelData = skipLists[level];
        // The segment to start the search at the next level. Set to 0 if no element in this level is larger than toAdd
        int startNextAt = 0;
        // Keep track of the last segment used on this level so that we can promote to it later.
        int segmentPos = startingSegmentPos;
        for (; segmentPos < levelData.length; segmentPos+=2) {
            final int element = levelData[segmentPos];
            if (element == INIT_VAL) {
                // Use the previous startNextAt if any.
                break;
            }
            startNextAt = levelData[segmentPos + 1];
            if (element > toAdd) {
                // Use this startNextAt
                break;
            } else if (element == toAdd) {
                throw ALREADY_EXISTS;
            }
        }
        final int updatedSegmentNum;
        int updatedVal = NO_POSITION;
        // Handle the final level case, otherwise recurse further.
        if (level == skipLists.length - 1) {
            // TODO uneccessary to store next level pos in bottom array
            updatedSegmentNum = bottomLevelInsert(startNextAt, toAdd);
            if (updatedSegmentNum != NO_POSITION) {
                updatedVal = data[updatedSegmentNum][0];
            }
        } else {
            updatedSegmentNum = insertAndPromote(level + 1, promotionLevel, startNextAt, toAdd);
            if (updatedSegmentNum != NO_POSITION) {
                updatedVal = skipLists[level+1][updatedSegmentNum];
            }
        }
        final int curSize = levelData.length;
        // Find the segment on this level with pos GTE updatedSegment to insert the promotion value at
        for (; segmentPos < curSize && levelData[segmentPos+1] < updatedSegmentNum; segmentPos+=2) {
        }
        final boolean alreadyHasSegmentNum = segmentPos < curSize && levelData[segmentPos+1] == updatedSegmentNum;
        // If we are at a level that is taking promotions from lower levels, or if this level had a previous promotion
        // from the same segment num, then insert the promoted segment
        final int lastPromotionLevel = (MAX_LEVEL - 1) - promotionLevel;
        if (alreadyHasSegmentNum || (level >= lastPromotionLevel && updatedSegmentNum != NO_POSITION)) {
            // If there is not enough space in this level, or we need to insert between two existing segments in the
            // level, then resize the level to avoid shifting anything.
            if (segmentPos == curSize || levelData[segmentPos+1] != updatedSegmentNum) {
                levelData = new int[curSize + 2];
                System.arraycopy(skipLists[level], 0, levelData, 0, segmentPos);
                System.arraycopy(skipLists[level], segmentPos, levelData, segmentPos + 2, curSize - segmentPos);
                skipLists[level] = levelData;
            }
            // Whether or not this was resized, segmentPos is now the correct location for the insert
            levelData[segmentPos] = updatedVal;
            levelData[segmentPos+1] = updatedSegmentNum;
            return segmentPos;
        }
        return NO_POSITION;
    }

    /**
     * @return the segment number of any segment whose first item was updated, otherwise NO_POSITION.
     */
    private int bottomLevelInsert(int segmentNum, int toAdd) throws AlreadyExistsException {
        int[] segmentToInsertIn = data[segmentNum];
        int firstUpdated = NO_POSITION;
        if (segmentToInsertIn[0] == INIT_VAL || segmentToInsertIn[0] > toAdd) {
            firstUpdated = segmentNum;
        }
        boolean unaddedVal = true;
        for (int posInSegment = 0; posInSegment < segmentToInsertIn.length; posInSegment++) {
            final int curVal = segmentToInsertIn[posInSegment];
            // If we have reached a value greater than toAdd then insert and swap
            if (curVal > toAdd) {
                segmentToInsertIn[posInSegment] = toAdd;
                // On the next loop, this curVal will be swapped for the next one
                toAdd = curVal;
                // If an empty spot is encountered then use this location to insert, and break as no shifting required
            } else if (curVal == INIT_VAL) {
                segmentToInsertIn[posInSegment] = toAdd;
                unaddedVal = false;
                break;
            } else if (curVal == toAdd) {
                throw ALREADY_EXISTS;
            }
        }
        // If a value was evicted from the segment, or if the segment is full and toAdd is larger than anything in the
        // segment, then the current toAdd value should be added to the first position in the next segment
        if (unaddedVal) {
            segmentNum += 1;
            // If there is a next segment with room, insert the evicted item in the first position, and move the rest up
            if (segmentNum < data.length && data[segmentNum][SEGMENT_SIZE - 1] == INIT_VAL) {
                segmentToInsertIn = data[segmentNum];
                if (segmentToInsertIn[0] == INIT_VAL || segmentToInsertIn[0] > toAdd) {
                    firstUpdated = segmentNum;
                }
                for (int posInSegment = 1; posInSegment < segmentToInsertIn.length; posInSegment++) {
                    final int curVal = segmentToInsertIn[posInSegment];
                    if (curVal > toAdd) {
                        segmentToInsertIn[posInSegment] = toAdd;
                        toAdd = curVal;
                    } else if (curVal == INIT_VAL) {
                        segmentToInsertIn[posInSegment] = toAdd;
                        break;
                    }
                }
            } else {
                firstUpdated = segmentNum;
                // Add a new segment to handle the overflow
                final int[][] largerData = new int[data.length + 1][];
                System.arraycopy(data, 0, largerData, 0, segmentNum);
                largerData[segmentNum] = new int[SEGMENT_SIZE];
                largerData[segmentNum][0] = toAdd;
                System.arraycopy(data, segmentNum, largerData, segmentNum + 1, data.length - segmentNum);
                data = largerData;
            }
        }
        return firstUpdated;
    }

    static class Constants {
        /**
         * Defining this allows the construction of the outer array in advance, which reduces the number of operations
         * required on an add. The value should not be higher than the maximum value produced by
         * {@link jaid.random}.
         */
        static final int MAX_LEVEL = 32;
        static final int SEGMENT_SIZE = 256;
        static final int NO_POSITION = -1;
        /**
         * Since there is no null int type, use the initialisation value as a no-element type. This requires
         * a separate boolean for the state of this value.
         */
        static final int INIT_VAL = 0;

        /**
         * Allows control to return to the top level of an add routine without needing to check return vals.
         */
        static final class AlreadyExistsException extends Exception {}
        static final AlreadyExistsException ALREADY_EXISTS = new AlreadyExistsException();
    }
}