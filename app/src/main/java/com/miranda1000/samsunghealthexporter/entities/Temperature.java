package com.miranda1000.samsunghealthexporter.entities;

import java.time.Instant;

public class Temperature extends SamsungHealthData {
    public final float temperature;

    public Temperature(Instant time, float temperature) {
        super(time);
        this.temperature = temperature;
    }

    /**
     * Gets the temperature of the body in ºC
     * @return temperature
     */
    public float getTemperature() {
        return this.temperature;
    }
}
