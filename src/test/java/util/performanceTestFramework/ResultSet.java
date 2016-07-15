package util.performanceTestFramework;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Allows returning every result rather than averaging, don`t want to lose outliers
 * This is a trade off, it will impact cache lines but is likely preferable
 * to risking io context switches with using a memory mapped file
 */
public class ResultSet {
    public float[] uSecTimes;
    public ResultSet(int size) {
        uSecTimes = new float[size];
    }
    List<Integer> sortedUSecTimes = null;
    private void sortTimes() {
        if (sortedUSecTimes == null || sortedUSecTimes.size() != uSecTimes.length) {
            sortedUSecTimes = new ArrayList<Integer>();
            for (float f: uSecTimes) {
                sortedUSecTimes.add((int)f);
            }
            Collections.sort(sortedUSecTimes);
            if (sortedUSecTimes.size() != uSecTimes.length) throw new RuntimeException();
        }
    }

    public int getUSecMedian() {
        sortTimes();
        int maxIdx = sortedUSecTimes.size() - 1;
        return sortedUSecTimes.get(maxIdx / 2);
    }
    public int getUSec90thPercentile() {
        sortTimes();
        int maxIdx = sortedUSecTimes.size() - 1;
        int oneNineIdx = maxIdx - (int)(maxIdx / 9.0);
        return sortedUSecTimes.get(oneNineIdx);
    }
    public int getUSec99thPercentile() {
        sortTimes();
        double maxIdx = sortedUSecTimes.size() - 1;
        double OnePercentIdx = maxIdx / 99.0;
        int twoNinesIdx = (int)Math.rint(maxIdx - OnePercentIdx);
        return sortedUSecTimes.get(twoNinesIdx);
    }
}
