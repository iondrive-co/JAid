package jaid.measure;

import java.util.concurrent.TimeUnit;

import org.apache.commons.math3.util.Precision;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 */
public class ProgressReport {

    private static Logger log = LoggerFactory.getLogger(ProgressReport.class);

    final long startTime;
    final double totalNum;
    final int progressInc;
    final String name;
    int i;

    public ProgressReport(final int totalNum, final String name) {
        this.startTime = System.currentTimeMillis();
        this.totalNum = totalNum;
        this.name = name;
        this.progressInc = Math.max(1, Math.round(totalNum / 100));
    }

    public void doneOne() {
        if (++i % progressInc == 0) {
            final double progress = i / totalNum;
            final long curTime = System.currentTimeMillis();
            final double estimatedTotalTime = (curTime - startTime) / progress;
            final double eta = TimeUnit.MILLISECONDS.toSeconds(Math.round(estimatedTotalTime * (1 - progress))) / 60.0;
            log.info(name + " progress: " + Precision.round(progress * 100, 2) + "% -> ETA: " + Precision.round(eta, 2) + " mins");
        }
    }

    public void doneOne(final String message) {
        if (++i % progressInc == 0) {
            final double progress = i / totalNum;
            final long curTime = System.currentTimeMillis();
            final double estimatedTotalTime = (curTime - startTime) / progress;
            final double eta = TimeUnit.MILLISECONDS.toSeconds(Math.round(estimatedTotalTime * (1 - progress))) / 60.0;
            log.info(name + " progress: " + Precision.round(progress * 100, 2) + "% -> ETA: " + Precision.round(eta, 2) + " mins " + message);
        }
    }

}
