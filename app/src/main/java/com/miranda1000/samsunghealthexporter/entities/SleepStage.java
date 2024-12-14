package com.miranda1000.samsunghealthexporter.entities;

import java.time.Instant;

public class SleepStage extends SamsungHealthData {
    private final SleepPhase phase;

    public SleepStage(Instant time, SleepPhase phase) {
        super(time);
        this.phase = phase;
    }

    public SleepPhase getSleepPhase() {
        return this.phase;
    }
}
