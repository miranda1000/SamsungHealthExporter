package com.miranda1000.samsunghealthexporter.jsons;

/**
 * Skin temperature values come from `jsons/com.samsung.health.skin_temperature`
 * It could also be obtained from `jsons/com.samsung.shealth.cycle.daily_temperature.raw/.../*.temperature.json`'s object_temperature
 */
public class SkinTemperature {
    public long end_time;
    public float max;
    public float mean;
    public float min;
    public long start_time;
}
