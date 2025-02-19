package jaid.collection;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DoublesVectorTest {

    @Test
    public void distance() {
        final double[] v1 = new double[]{ 0.02, 0.1 };
        final double[] v2 = new double[]{ 0.01, 0.1 };

        assertEquals(DoublesVector.distance(v1, v2), 5.0E-5);
    }
}