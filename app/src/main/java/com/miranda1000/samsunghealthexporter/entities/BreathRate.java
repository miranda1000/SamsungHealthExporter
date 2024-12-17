package com.miranda1000.samsunghealthexporter.entities;

import java.time.Instant;

public class BreathRate extends SamsungHealthData {
    /**
     * Breaths per minute
     */
    private final float respiratoryRate;

    public BreathRate(Instant time, float respiratoryRate) {
        super(time);
        this.respiratoryRate = respiratoryRate;
    }

    /**
     * Get the breaths per minute
     * @return respiratoryRate
     */
    public float getRespiratoryRate() {
        return this.respiratoryRate;
    }
}
