package jaid.number;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static jaid.number.HashingUtil.compressHash;
import static org.assertj.core.api.Assertions.assertThat;

class HashingUtilTest {

    @Test
    void testCompressHash() {
        for (byte bits = 1; bits <= 16; bits++) {
            for (byte inputBits = 16; inputBits <= 64; inputBits += 16) {
                final Set<Integer> uniqueBuckets = new HashSet<>();
                final int numberOfBuckets = (int) Math.pow(2, bits);
                final long rangePerBucket = inputBits == 64 ? (Long.MAX_VALUE >>> (bits - 1)) : (1L << inputBits) / numberOfBuckets;
                for (long i = 0; i < numberOfBuckets; i++) {
                    long hashValue;
                    if (inputBits == 64) {
                        hashValue = (i << (64 - bits)); // Assign hashValue directly based on loop index i for full range
                    } else {
                        hashValue = (i * rangePerBucket + rangePerBucket / 2) & ((1L << inputBits) - 1); // Midpoint calculation for inputBits < 64
                    }
                    uniqueBuckets.add(compressHash(bits, hashValue, inputBits));
                }
                assertThat(uniqueBuckets).as("Output bits: " + bits + " input bits " + inputBits)
                        .hasSize(numberOfBuckets);
            }
        }
    }
}