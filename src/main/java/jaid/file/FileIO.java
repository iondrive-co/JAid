package jaid.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

import static java.lang.Integer.MAX_VALUE;
import static java.nio.channels.FileChannel.MapMode.READ_WRITE;

/**
 * Helper methods for file reading and writing
 */
public class FileIO {

    public static byte[] readBytes(final File file) {
        byte[] barray = null;
        try {
            FileChannel fChannel = new FileInputStream(file).getChannel();
            barray = new byte[(int) file.length()];
            ByteBuffer bb = ByteBuffer.wrap(barray);
            bb.order(ByteOrder.LITTLE_ENDIAN);
            fChannel.read(bb);
            fChannel.close();
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
        return barray;
    }

    public static void writeBytes(final String fileName, final byte[] bytes) {
        try {
            final File out = new File(fileName);
            out.getParentFile().mkdirs();
            out.createNewFile();
            final RandomAccessFile outputFile = new RandomAccessFile(fileName, "rw");
            FileChannel channel = outputFile.getChannel();
            MappedByteBuffer buffer = channel.map(READ_WRITE, 0, Math.min(bytes.length, MAX_VALUE));
            buffer.put(bytes);
            channel.close();
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }
}
