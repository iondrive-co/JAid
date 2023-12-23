package jaid.os;

import org.junit.jupiter.api.Test;

import javax.sound.sampled.AudioFormat;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

import static jaid.os.AudioUtil.normalizeAudio;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AudioUtilTest {

    @Test
    void testNormalizeAudio() {
        byte[] testAudioData = new byte[8];
        ByteBuffer.wrap(testAudioData).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().put(new short[]{1000, -1000, 1500, -1500});
        byte[] normalizedData = normalizeAudio(testAudioData, createTestAudioFormat());
        ShortBuffer sbuf = ByteBuffer.wrap(normalizedData).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer();
        short[] samples = new short[sbuf.capacity()];
        sbuf.get(samples);
        short expectedPeak = Short.MAX_VALUE;
        for (short sample : samples) {
            assertTrue(Math.abs(sample) <= expectedPeak);
        }
    }

    public static AudioFormat createTestAudioFormat() {
        float sampleRateHz = 44100.0f;
        int bitsPerSample = 16;
        int channels = 2;
        // default to PCM which is signed and little endian
        boolean signed = true;
        boolean bigEndian = false;
        return new AudioFormat(sampleRateHz, bitsPerSample, channels, signed, bigEndian);
    }
}