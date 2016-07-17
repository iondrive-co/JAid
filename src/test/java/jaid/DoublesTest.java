package jaid;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import jaid.number.Doubles;

/**
 */
public class DoublesTest {

    @Test
    public void isNan() {
        double[] vals1 = new double[]{-1, 0 , 1.1f};
        double[] vals2 = new double[]{-1, 0 , 1.1, Float.NEGATIVE_INFINITY};
        double[] vals3 = new double[]{-99999, Float.NaN};
        double[] vals4 = new double[]{Float.NaN};
        double[] vals5 = new double[]{};
        double[] vals6 = new double[]{Double.NaN};
        assertThat(Doubles.isNan(vals1)).isFalse();
        assertThat(Doubles.isNan(vals2)).isFalse();
        assertThat(Doubles.isNan(vals3)).isTrue();
        assertThat(Doubles.isNan(vals4)).isTrue();
        assertThat(Doubles.isNan(vals5)).isFalse();
        assertThat(Doubles.isNan(vals6)).isTrue();

    }
}
