package com.miranda1000.samsunghealthexporter.jsons;

import java.time.Instant;

/**
 * Sleep information values come from `com.samsung.shealth.sleep.*.csv`
 */
public class SleepInformation {
    /**
     * % of mental recovery
     */
    public float mental_recovery;

    /**
     * % of physical recovery
     */
    public float physical_recovery;

    /**
     * com.samsung.health.sleep.start_time
     *
     * UTC+0 time in format YYYY-MM-DD HH:MM:SS.mmm
     */
    public Instant start_time;

    /**
     * com.samsung.health.sleep.end_time
     *
     * UTC+0 time in format YYYY-MM-DD HH:MM:SS.mmm
     */
    public Instant end_time;
}
