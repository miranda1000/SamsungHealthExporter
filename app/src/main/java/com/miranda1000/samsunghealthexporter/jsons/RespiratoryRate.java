package com.miranda1000.samsunghealthexporter.jsons;

/**
 * Respiratory rate values come from `jsons/com.samsung.health.respiratory_rate`
 */
public class RespiratoryRate {
    public long end_time;

    /**
     * Breaths per minute
     */
    public float respiratory_rate;

    /**
     * Timestamp where the measure starts;
     * it's a unix timestamp *1000 [including ms]
     */
    public long start_time;
}
