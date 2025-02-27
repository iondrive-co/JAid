package jaid.collection;


import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class DoublesVectorTest {

    @Test
    public void testMeanSquaredError() {
        DoublesVector v1 = new DoublesVector(new double[]{1.0, 2.0, 3.0});
        DoublesVector v2 = new DoublesVector(new double[]{4.0, 5.0, 6.0});
        double mse = v1.meanSquaredError(v2);
        // MSE = ((4-1)² + (5-2)² + (6-3)²) / 3 = (9 + 9 + 9) / 3 = 9
        assertThat(mse).isEqualTo(9.0);
    }

    @Test
    public void testMeanSquaredErrorZero() {
        DoublesVector v1 = new DoublesVector(new double[]{1.0, 2.0, 3.0});
        DoublesVector v2 = new DoublesVector(new double[]{1.0, 2.0, 3.0});
        double mse = v1.meanSquaredError(v2);
        assertThat(mse).isZero();
    }

    @Test
    public void testMeanSquaredErrorDifferentDimensions() {
        DoublesVector v1 = new DoublesVector(new double[]{1.0, 2.0});
        DoublesVector v2 = new DoublesVector(new double[]{1.0, 2.0, 3.0});
        assertThatThrownBy(() -> v1.meanSquaredError(v2))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("dimensions");
    }

    @Test
    public void testDistance() {
        DoublesVector v1 = new DoublesVector(new double[]{1.0, 2.0, 3.0});
        DoublesVector v2 = new DoublesVector(new double[]{4.0, 5.0, 6.0});
        double distance = v1.distance(v2);
        // Distance = √((4-1)² + (5-2)² + (6-3)²) = √(9 + 9 + 9) = √27 = 3√3
        assertThat(distance).isEqualTo(Math.sqrt(27));
    }

    @Test
    public void testDistanceZero() {
        DoublesVector v1 = new DoublesVector(new double[]{1.0, 2.0, 3.0});
        DoublesVector v2 = new DoublesVector(new double[]{1.0, 2.0, 3.0});
        double distance = v1.distance(v2);
        assertThat(distance).isZero();
    }

    @Test
    public void testDistance2D() {
        DoublesVector v1 = new DoublesVector(new double[]{0.0, 0.0});
        DoublesVector v2 = new DoublesVector(new double[]{3.0, 4.0});
        double distance = v1.distance(v2);
        assertThat(distance).isEqualTo(5.0); // Pythagorean triple 3-4-5
    }

    @Test
    public void testDistanceDifferentDimensions() {
        DoublesVector v1 = new DoublesVector(new double[]{1.0, 2.0});
        DoublesVector v2 = new DoublesVector(new double[]{1.0, 2.0, 3.0});
        assertThatThrownBy(() -> v1.distance(v2))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("dimensions");
    }

    @Test
    public void testConsistencyBetweenMSEAndDistance() {
        DoublesVector v1 = new DoublesVector(new double[]{1.0, 2.0, 3.0, 4.0});
        DoublesVector v2 = new DoublesVector(new double[]{5.0, 6.0, 7.0, 8.0});
        double mse = v1.meanSquaredError(v2);
        double distance = v1.distance(v2);
        // sqrt(mse * dimension) should equal distance
        assertThat(Math.sqrt(mse * 4)).isEqualTo(distance);
    }

    @Test
    public void testScale() {
        double[] values = {1.0, 2.0, 3.0};
        DoublesVector vector = new DoublesVector(values);
        float scaleFactor = 2.5f;
        DoublesVector scaled = vector.scale(scaleFactor);
        assertThat(scaled.contents()).containsExactly(2.5, 5.0, 7.5);
        // Original vector should remain unchanged
        assertThat(vector.contents()).containsExactly(1.0, 2.0, 3.0);
    }

    @Test
    public void testScaleZero() {
        double[] values = {1.0, 2.0, 3.0};
        DoublesVector vector = new DoublesVector(values);
        DoublesVector scaled = vector.scale(0f);
        assertThat(scaled.contents()).containsOnly(0.0);
    }

    @Test
    public void testScaleNegative() {
        double[] values = {1.0, 2.0, 3.0};
        DoublesVector vector = new DoublesVector(values);
        DoublesVector scaled = vector.scale(-1.0f);
        assertThat(scaled.contents()).containsExactly(-1.0, -2.0, -3.0);
    }

    @Test
    public void testMagnitude() {
        // Given
        double[] values = {3.0, 4.0};
        DoublesVector vector = new DoublesVector(values);
        double magnitude = vector.magnitude();
        assertThat(magnitude).isEqualTo(5.0);
    }

    @Test
    public void testMagnitudeZeroVector() {
        double[] values = {0.0, 0.0, 0.0};
        DoublesVector vector = new DoublesVector(values);
        double magnitude = vector.magnitude();
        assertThat(magnitude).isZero();
    }

    @Test
    public void testMagnitudeNDimensional() {
        double[] values = {2.0, 3.0, 6.0, 8.0};
        DoublesVector vector = new DoublesVector(values);
        double magnitude = vector.magnitude();
        double expected = Math.sqrt(2*2 + 3*3 + 6*6 + 8*8); // √(4 + 9 + 36 + 64) = √113
        assertThat(magnitude).isEqualTo(expected);
    }
}