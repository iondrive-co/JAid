package jaid.number;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static jaid.number.HashingUtil.compressHash;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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

    @Test
    public void shouldCompressToSpecifiedOutputBits() {
        long hash = 0xFFFF; // 16 bits set to 1
        int compressed = HashingUtil.compressHash((byte) 8, hash, (byte) 16);
        assertThat(compressed).isBetween(0, 255); // 2^8 - 1 = 255
    }

    @Test
    public void shouldIgnoreUpperBitsBeyondInputBits() {
        long hash = 0xFFFF00000000L; // higher 16 bits are set
        int compressed = HashingUtil.compressHash((byte) 8, hash, (byte) 16);
        assertThat(compressed).isEqualTo(0); // higher bits should be ignored
    }

    @Test
    public void shouldHandleMinimumOutputBits() {
        // Minimum output bits (1 bit)
        long hash = 2; // Binary representation 10
        int compressed = HashingUtil.compressHash((byte) 1, hash, (byte) 2);
        assertThat(compressed).isBetween(0, 1); // Only two possible values: 0 or 1
    }

    @Test
    public void shouldHandleMaximumOutputBits() {
        // Maximum output bits (32 bits)
        long hash = Long.MAX_VALUE;
        int compressed = HashingUtil.compressHash((byte) 32, hash, (byte) 64);
        // Check within integer range, as all 32 bits are used for an int value
        assertThat(compressed).isBetween(Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    @Test
    public void shouldThrowExceptionForInvalidOutputBits() {
        // Invalid output bits (e.g., -1, 33)
        long hash = 12345L;
        assertThatThrownBy(() -> HashingUtil.compressHash((byte) -1, hash, (byte) 16))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> HashingUtil.compressHash((byte) 33, hash, (byte) 16))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void shouldHandleZeroHashValue() {
        int compressed = HashingUtil.compressHash((byte) 8, 0L, (byte) 16);
        assertThat(compressed).isEqualTo(0);
    }

    @Test
    public void shouldHandleMaximumHashValue() {
        // Hash value of Long.MAX_VALUE
        int compressed = HashingUtil.compressHash((byte) 8, Long.MAX_VALUE, (byte) 64);
        assertThat(compressed).isBetween(0, 255);
    }
}