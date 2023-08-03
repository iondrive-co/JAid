package jaid.measure;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class MeanSquaredErrorTest {

    @Test
    public void distance() {
        final double[] v1 = new double[]{ 0.02, 0.1 };
        final double[] v2 = new double[]{ 0.01, 0.1 };

        MeanSquaredError error = new MeanSquaredError();
        assertThat(error.distance(v1, v2)).isEqualTo(5.0E-5);
    }
}