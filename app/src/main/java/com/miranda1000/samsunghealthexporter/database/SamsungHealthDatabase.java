package com.miranda1000.samsunghealthexporter.database;

import com.miranda1000.samsunghealthexporter.entities.*;

public interface SamsungHealthDatabase {
    void exportBreathRate(BreathRate []breathRates) throws Exception;
    void exportHeartRate(HeartRate []heartRates) throws Exception;
    void exportOxygenSaturation(OxygenSaturation []oxygenSaturations) throws Exception;
    void exportSleepStage(SleepStage[]sleepStages) throws Exception;
    void exportTemperature(Temperature []temperatures) throws Exception;

    boolean canConnect();
}
