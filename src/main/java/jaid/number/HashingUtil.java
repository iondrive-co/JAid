package jaid.number;

import com.google.common.base.Preconditions;

public class HashingUtil {

    /**
     * Reduces the input hash down to 2^outputBits possible values
     * @param inputBits number of bits from the long input hash to use, i.e. if 16 then throw away the upper 48 bits
     */
    public static int compressHash(byte outputBits, long hash, byte inputBits) {
        Preconditions.checkArgument(outputBits > 0);
        Preconditions.checkArgument(outputBits <= 32);
        Preconditions.checkArgument(inputBits > 0);
        Preconditions.checkArgument(inputBits <= 64);
        // When the number of input bits equals the number of output bits, no compression is needed.
        if (outputBits >= inputBits) {
            return (int) hash;
        }
        // Calculate how many values each bucket represents
        long numberOfBuckets = 1L << outputBits;
        long segmentSize;
        if (inputBits == 64) {
            // For 64-bit input, we handle it as an unsigned long division.
            // For outputBits = 1, segmentSize should be half of the long range.
            segmentSize = Long.divideUnsigned(-1L, numberOfBuckets); // -1L is the same as 0xFFFFFFFFFFFFFFFF, the max unsigned long
        } else {
            // For any other number of bits, segmentSize can be determined normally.
            long relevantBitsMask = (1L << inputBits) - 1;
            segmentSize = (1L << inputBits) / numberOfBuckets; // This will never be zero if inputBits < 64
            hash &= relevantBitsMask;
        }
        // Divide the hash by the segment size to determine its bucket
        int bucket = (int) Long.divideUnsigned(hash, segmentSize);
        // Ensure the bucket number fits within the number of output buckets
        return bucket & ((1 << outputBits) - 1);
    }
}
