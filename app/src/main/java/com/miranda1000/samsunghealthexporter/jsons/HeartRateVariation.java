package com.miranda1000.samsunghealthexporter.jsons;

/**
 * Hate rate variation values come from `jsons/com.samsung.health.hrv`
 */
public class HeartRateVariation {
    /**
     * Root mean square of successive differences [in ms]
     */
    public float rmssd;

    /**
     * Standard deviation of NN intervals [in ms]
     */
    public float sdnn;

    public long end_time;

    /**
     * Timestamp where the measure starts;
     * it's a unix timestamp *1000 [including ms]
     */
    public long start_time;
}
