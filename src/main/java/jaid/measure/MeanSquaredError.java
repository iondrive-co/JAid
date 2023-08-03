package jaid.measure;

public class MeanSquaredError {

    /**
     * Calculate the mean squared error.
     * for vectors v1 and v2 of length I calculate
     * MSE = 1/I * Sum[ ( v1_i - v2_i )^2 ]
     */
    public double distance(double[] vector1, double[] vector2) {
        if( vector1.length == vector2.length ) {
            double sum = 0;
            for( int i = 0; i < vector1.length; i++ ) {
                sum += Math.pow( vector1[ i ] - vector2[ i ] , 2 );
            }
            return ( sum / vector1.length );
        } else {
            throw new IllegalArgumentException();
        }
    }
}
