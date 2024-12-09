package com.miranda1000.samsunghealthexporter.entities;

import java.time.Instant;

public class HeartRate extends SamsungHealthData {

    /**
     * Beats per minute
     */
    private final Float BPM;

    /**
     * R-R interval [in ms]
     */
    private final Float RRI;

    /**
     * Heart rate variation's root mean square of successive differences [in ms]
     */
    private final Float rMSSD;

    /**
     * Heart rate variation's standard deviation of NN intervals [in ms]
     */
    private final Float SDNN;

    public HeartRate(Instant time, Float BPM, Float RRI, Float rMSSD, Float SDNN) {
        super(time);

        if (BPM == null && RRI == null && rMSSD == null && SDNN == null) throw new IllegalArgumentException("At least one argument must be provided");

        this.BPM = BPM;
        this.RRI = RRI;
        this.rMSSD = rMSSD;
        this.SDNN = SDNN;
    }

    public HeartRate(Instant time, float BPM) {
        this(time, BPM, null, null, null);
    }

    public Float getBPM() {
        return this.BPM;
    }

    public Float getRRI() {
        return this.RRI;
    }

    public Float get_rMSSD() {
        return this.rMSSD;
    }

    public Float getSDNN() {
        return this.SDNN;
    }
}
