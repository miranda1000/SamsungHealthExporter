package com.miranda1000.samsunghealthexporter.entities;

import androidx.annotation.NonNull;

public enum SleepPhase {
    AWAKE(40001),
    REM(40004),
    LIGHT(40002),
    HEAVY(40003);

    private int val;
    SleepPhase(int val) {
        this.val = val;
    }

    public static SleepPhase getFromValue(int val) throws IllegalArgumentException {
        for (SleepPhase stage : SleepPhase.values()) {
            if (stage.val == val) return stage;
        }

        throw new IllegalArgumentException("Value '" + val + "' was not found in sleep phases list");
    }

    @NonNull
    @Override
    public String toString() {
        // only REM is all uppercase
        if (this == SleepPhase.REM) return this.name();
        else return this.name().substring(0, 1).toUpperCase() + this.name().substring(1);
    }
}
