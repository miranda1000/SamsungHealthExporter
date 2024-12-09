package com.miranda1000.samsunghealthexporter.entities;

import java.time.Instant;
import java.util.Comparator;

/**
 * Every data provided by SamsungHealth comes from one point in time.
 */
public class SamsungHealthData implements Comparable<SamsungHealthData> {
    /**
     * Time when the measure was taken
     * (UTC+0)
     */
    private final Instant time;

    public SamsungHealthData(Instant time) {
        this.time = time;
    }

    public Instant getTime() {
        return this.time;
    }

    @Override
    public int compareTo(SamsungHealthData that) {
        return this.time.compareTo(that.time);
    }
}
