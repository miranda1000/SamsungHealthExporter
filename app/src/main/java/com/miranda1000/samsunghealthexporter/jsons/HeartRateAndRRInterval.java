package com.miranda1000.samsunghealthexporter.jsons;

/**
 * Hate rate and R-R interval values come from `jsons/com.samsung.shealth.cycle.daily_temperature.raw/.../*.hr_rri.json`
 */
public class HeartRateAndRRInterval {
    /**
     * BPM non-float
     */
    public float heart_rate;

    public long end_time;

    /**
     * Timestamp where the measure starts;
     * it's a unix timestamp *1000 [including ms]
     */
    public long start_time;

    /**
     * R-R interval [in ms]
     *
     * I haven't seen any decimal on the json files, but just in case I'll make it a float
     */
    public float rri;
}
