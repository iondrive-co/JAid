package jaid.collection;

import java.util.Random;

public class IVectorTestUtil {

    public static FloatsVector generateRandomVector(final int dims, final Random random) {
        float[] components = new float[dims];
        for (int i = 0; i < dims; i++) {
            components[i] = random.nextFloat() * 2 - 1;
        }
        return new FloatsVector(components);
    }

    public static FloatsVector generateFixedVector(final int dims, final float value) {
        float[] components = new float[dims];
        for (int i = 0; i < dims; i++) {
            components[i] = value;
        }
        return new FloatsVector(components);
    }
}
