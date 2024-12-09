package com.miranda1000.samsunghealthexporter.jsons;

/**
 * Oxygen saturation values come from `jsons/com.samsung.shealth.tracker.oxygen_saturation`
 */
public class OxygenSaturation {
    /**
     * Mean blood oxygen %
     */
    public byte spo2;

    public byte spo2_max;

    public byte spo2_min;

    public long end_time;

    /**
     * Timestamp where the measure starts;
     * it's a unix timestamp *1000 [including ms]
     */
    public long start_time;
}
