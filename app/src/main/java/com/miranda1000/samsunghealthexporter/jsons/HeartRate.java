package com.miranda1000.samsunghealthexporter.jsons;

/**
 * The heart rate data is provided on `jsons/com.samsung.shealth.tracker.heart_rate`
 */
public class HeartRate {
    /**
     * BPM
     */
    public float heart_rate;

    public float heart_rate_max;

    public float heart_rate_min;

    /**
     * Timestamp where the measure starts;
     * it's a unix timestamp *1000 [including ms]
     */
    public long start_time;

    public long end_time;
}
