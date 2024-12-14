package com.miranda1000.samsunghealthexporter.jsons;

import com.miranda1000.samsunghealthexporter.entities.SamsungHealthData;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * Sleep stage values come from `com.samsung.health.sleep_stage.*.csv`
 */
public class SleepStage implements Comparable<SleepStage> {
    // format that the CSV follow
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    /**
     * UTC+0 time in format YYYY-MM-DD HH:MM:SS.mmm
     */
    public String start_time;

    /**
     * From 40_001 to 40_004, each one representing one different sleeping stage from 'awake' to 'heavy'
     */
    public int stage;

    /**
     * UTC+0 time in format YYYY-MM-DD HH:MM:SS.mmm
     */
    public String end_time;

    public SleepStage(String start_time, String end_time, int stage) {
        this.start_time = start_time;
        this.end_time = end_time;
        this.stage = stage;
    }

    public Instant getStartTime() {
        LocalDateTime localDateTime = LocalDateTime.parse(this.start_time, formatter);
        return localDateTime.toInstant(ZoneOffset.UTC);
    }

    public Instant getEndTime() {
        LocalDateTime localDateTime = LocalDateTime.parse(this.end_time, formatter);
        return localDateTime.toInstant(ZoneOffset.UTC);
    }

    @Override
    public int compareTo(SleepStage that) {
        return this.getStartTime().compareTo(that.getStartTime());
    }
}
