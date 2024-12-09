package com.miranda1000.samsunghealthexporter.entities;

public enum SleepStage {
    AWAKE(40001),
    REM(40002),
    LIGHT(40003),
    HEAVY(40004);

    private int val;
    SleepStage(int val) {
        this.val = val;
    }

    public static SleepStage getFromValue(int val) {
        for (SleepStage stage : SleepStage.values()) {
            if (stage.val == val) return stage;
        }

        throw new IllegalArgumentException("Value '" + val + "' was not found in sleep phases list");
    }
}
