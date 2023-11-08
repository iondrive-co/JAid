package jaid.number;

import static jaid.number.Maths.INTEGER_RANGE;

public class HashingUtil {

    /**
     * Reduces the input hash down to 2^outputBits possible values
     * @param inputBits number of bits from the long input hash to use, i.e. if 16 then throw away the upper 48 bits
     */
    public static int compressHash(int outputBits, long hash, byte inputBits) {
        int buckets = 1 << outputBits;
        long segmentSize = INTEGER_RANGE / buckets;
        long relevantBits = hash - (1L << inputBits);
        return (int)(relevantBits / segmentSize);
    }
}
