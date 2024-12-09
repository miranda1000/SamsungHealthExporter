package com.miranda1000.samsunghealthexporter.jsons;

import java.time.Instant;

/**
 * Sleep stage values come from `com.samsung.health.sleep_stage.*.csv`
 */
public class SleepStage {
    /**
     * UTC+0 time in format YYYY-MM-DD HH:MM:SS.mmm
     */
    public Instant start_time;

    /**
     * From 40_001 to 40_004, each one representing one different sleeping stage from 'awake' to 'heavy'
     */
    public int stage;
}
