package com.miranda1000.samsunghealthexporter.entities;

import java.time.Instant;

/**
 * Specifies the oxygen %
 */
public class OxygenSaturation extends SamsungHealthData {
    /**
     * Mean blood oxygen %
     */
    private final float oxygenInBlood;

    public OxygenSaturation(Instant time, float oxygenInBlood) {
        super(time);

        this.oxygenInBlood = oxygenInBlood;
    }

    public float getOxygenInBlood() {
        return this.oxygenInBlood;
    }
}
