package jaid.number;

import org.junit.jupiter.api.Test;

import static jaid.number.Maths.isNan;
import static org.assertj.core.api.Assertions.assertThat;


public class MathsTest {

    @Test
    public void isNanTest() {
        double[] vals1 = new double[]{-1, 0 , 1.1f};
        double[] vals2 = new double[]{-1, 0 , 1.1, Float.NEGATIVE_INFINITY};
        double[] vals3 = new double[]{-99999, Float.NaN};
        double[] vals4 = new double[]{Float.NaN};
        double[] vals5 = new double[]{};
        double[] vals6 = new double[]{Double.NaN};
        assertThat(isNan(vals1)).isFalse();
        assertThat(isNan(vals2)).isFalse();
        assertThat(isNan(vals3)).isTrue();
        assertThat(isNan(vals4)).isTrue();
        assertThat(isNan(vals5)).isFalse();
        assertThat(isNan(vals6)).isTrue();

    }
}
