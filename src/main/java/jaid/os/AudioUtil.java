package jaid.os;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;


/**
 * Note that java audio libraries do not support mp3, you will need to add library:
 * implementation 'com.googlecode.soundlibs:mp3spi:1.9.5.4'
 */
public class AudioUtil {

    public static byte[] normalizeAudio(final String filePath) throws UnsupportedAudioFileException, IOException {
        final File file = new File(filePath);
        try (final AudioInputStream ais = AudioSystem.getAudioInputStream(file)) {
            final AudioFormat format = ais.getFormat();
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            final byte[] buffer = new byte[1024];
            int read;
            while ((read = ais.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            final byte[] audioBytes = out.toByteArray();
            return normalizeAudio(audioBytes, format);
        }
    }

    public static byte[] normalizeAudio(final byte[] audioBytes, final AudioFormat format) {
        final int bytesPerSample = format.getFrameSize() / format.getChannels();
        final boolean is16Bit = format.getSampleSizeInBits() == 16;
        final boolean isBigEndian = format.isBigEndian();

        final ByteBuffer buffer = ByteBuffer.wrap(audioBytes).order(
                isBigEndian ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN);
        final ByteBuffer normalizedBuffer = ByteBuffer.allocate(audioBytes.length).order(ByteOrder.LITTLE_ENDIAN);
        final double peak = findPeak(buffer, is16Bit, bytesPerSample);

        buffer.position(0);
        normalizeBuffer(buffer, normalizedBuffer, peak, is16Bit, bytesPerSample);
        return normalizedBuffer.array();
    }

    private static double findPeak(final ByteBuffer buffer, final boolean is16Bit, final int bytesPerSample) {
        double peak = 0;
        while (buffer.hasRemaining()) {
            final double sample = is16Bit ? buffer.getShort() : buffer.get();
            peak = Math.max(peak, Math.abs(sample));
            if (bytesPerSample > 2) {
                buffer.position(buffer.position() + bytesPerSample - 2);
            }
        }
        return peak;
    }

    private static void normalizeBuffer(final ByteBuffer original, final ByteBuffer normalized, final double peak,
                                        final boolean is16Bit, final int bytesPerSample) {
        final double normalizationFactor = is16Bit ? Short.MAX_VALUE / peak : Byte.MAX_VALUE / peak;
        original.rewind();
        while (original.hasRemaining()) {
            double sample = is16Bit ? original.getShort() : original.get();
            sample *= normalizationFactor;
            if (is16Bit) {
                normalized.putShort((short) sample);
            } else {
                normalized.put((byte) sample);
            }
            if (bytesPerSample > 2) {
                original.position(original.position() + bytesPerSample - 2);
                normalized.position(normalized.position() + bytesPerSample - 2);
            }
        }
    }

    /**
     * Allow manual testing of normalisation via running from command line
     */
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: java AudioFileUtil <inputFilePath>");
            System.exit(1);
        }

        try {
            final String inputFilePath = args[0];
            final byte[] normalizedBytes = normalizeAudio(inputFilePath);
            final AudioInputStream normalizedAIS = createAudioInputStream(normalizedBytes, inputFilePath);
            saveAsWav(normalizedAIS, inputFilePath);
        } catch (UnsupportedAudioFileException | IOException e) {
            e.printStackTrace();
        }
    }

    private static void saveAsWav(AudioInputStream ais, String originalFilePath) throws IOException {
        String outputFilePath = originalFilePath.substring(0, originalFilePath.lastIndexOf('.')) + "-normalized.wav";
        File outputFile = new File(outputFilePath);
        AudioSystem.write(ais, AudioFileFormat.Type.WAVE, outputFile);
        System.out.println("Normalized audio saved successfully to " + outputFilePath);
    }

    private static AudioInputStream createAudioInputStream(byte[] audioBytes, String originalFilePath) throws UnsupportedAudioFileException, IOException {
        try (AudioInputStream ais = AudioSystem.getAudioInputStream(new File(originalFilePath))) {
            AudioFormat format = ais.getFormat();
            ByteArrayInputStream bais = new ByteArrayInputStream(audioBytes);
            return new AudioInputStream(bais, format, audioBytes.length / format.getFrameSize());
        }
    }
}
