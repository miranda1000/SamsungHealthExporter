package com.miranda1000.samsunghealthexporter;

import com.miranda1000.samsunghealthexporter.entities.BreathRate;
import com.miranda1000.samsunghealthexporter.entities.SamsungHealthData;
import com.miranda1000.samsunghealthexporter.entities.SleepPhase;
import com.miranda1000.samsunghealthexporter.entities.SleepStage;
import com.miranda1000.samsunghealthexporter.entities.Temperature;
import com.miranda1000.samsunghealthexporter.jsons.HeartRateAndRRInterval;
import com.miranda1000.samsunghealthexporter.jsons.HeartRateVariation;
import com.miranda1000.samsunghealthexporter.jsons.RespiratoryRate;
import com.miranda1000.samsunghealthexporter.jsons.SkinTemperature;

import java.time.Instant;
import java.util.*;

public class SamsungHealthJsonParser {

    public com.miranda1000.samsunghealthexporter.entities.OxygenSaturation []parseOxygenSaturation(com.miranda1000.samsunghealthexporter.jsons.OxygenSaturation []oxygenSaturations) {
        com.miranda1000.samsunghealthexporter.entities.OxygenSaturation []result = new com.miranda1000.samsunghealthexporter.entities.OxygenSaturation[oxygenSaturations.length];

        for (int n = 0; n < result.length; n++) {
            long androidTimestamp = oxygenSaturations[n].start_time;
            byte spo2 = oxygenSaturations[n].spo2;
            result[n] = new com.miranda1000.samsunghealthexporter.entities.OxygenSaturation(
                    Instant.ofEpochMilli(androidTimestamp),
                    spo2 / 100f
            );
        }

        return result;
    }

    public BreathRate []parseRespiratoryRate(RespiratoryRate []respiratoryRates) {
        BreathRate[]result = new BreathRate[respiratoryRates.length];

        for (int n = 0; n < result.length; n++) {
            long androidTimestamp = respiratoryRates[n].start_time;
            result[n] = new BreathRate(
                    Instant.ofEpochMilli(androidTimestamp),
                    respiratoryRates[n].respiratory_rate
            );
        }

        return result;
    }


    public Temperature []parseSkinTemperature(SkinTemperature []skinTemperatures) {
        Temperature[]result = new Temperature[skinTemperatures.length];

        for (int n = 0; n < result.length; n++) {
            long androidTimestamp = skinTemperatures[n].start_time;
            result[n] = new Temperature(
                    Instant.ofEpochMilli(androidTimestamp),
                    skinTemperatures[n].mean
            );
        }

        return result;
    }

    public com.miranda1000.samsunghealthexporter.entities.HeartRate []parseHeartRate(
            com.miranda1000.samsunghealthexporter.jsons.HeartRate []heartRates,
            HeartRateAndRRInterval []heartRatesAndRRIntervals,
            HeartRateVariation []heartRateVariations) {
        HashMap<Instant,List<Object>> groupedHeartData = new HashMap<>();

        //
        // Group all the same time data
        //

        // heart rates
        for (com.miranda1000.samsunghealthexporter.jsons.HeartRate heartRate : heartRates) {
            Instant time = Instant.ofEpochMilli(heartRate.start_time);

            List<Object> toAdd = new ArrayList<>();
            toAdd.add(heartRate);
            groupedHeartData.put(time, toAdd);
        }

        // heart rates/rr intervals
        for (HeartRateAndRRInterval heartRateAndRRInterval : heartRatesAndRRIntervals) {
            Instant time = Instant.ofEpochMilli(heartRateAndRRInterval.start_time);

            List<Object> objectsForThatTime = groupedHeartData.get(time);
            if (objectsForThatTime == null) {
                objectsForThatTime = new ArrayList<>();
                groupedHeartData.put(time, objectsForThatTime);
            }

            objectsForThatTime.add(heartRateAndRRInterval);
        }

        // heart rate variations
        for (HeartRateVariation heartRateVariation : heartRateVariations) {
            Instant time = Instant.ofEpochMilli(heartRateVariation.start_time);

            List<Object> objectsForThatTime = groupedHeartData.get(time);
            if (objectsForThatTime == null) {
                objectsForThatTime = new ArrayList<>();
                groupedHeartData.put(time, objectsForThatTime);
            }

            objectsForThatTime.add(heartRateVariation);
        }

        //
        // All data got; create instances
        //
        com.miranda1000.samsunghealthexporter.entities.HeartRate []r =
                new com.miranda1000.samsunghealthexporter.entities.HeartRate[groupedHeartData.size()];

        int index = 0;
        for (Map.Entry<Instant, List<Object>> entry : groupedHeartData.entrySet()) {
            Instant time = entry.getKey();
            Float BPM = null;
            Float RRI = null;
            Float rMSSD = null;
            Float SDNN = null;

            for (Object objectForTime : entry.getValue()) {
                if (objectForTime instanceof com.miranda1000.samsunghealthexporter.jsons.HeartRate) {
                    BPM = ((com.miranda1000.samsunghealthexporter.jsons.HeartRate)objectForTime).heart_rate;
                }
                else if (objectForTime instanceof HeartRateAndRRInterval) {
                    if (BPM == null) BPM = ((HeartRateAndRRInterval)objectForTime).heart_rate;
                    RRI = ((HeartRateAndRRInterval)objectForTime).rri;
                }
                else if (objectForTime instanceof HeartRateVariation) {
                    rMSSD = ((HeartRateVariation)objectForTime).rmssd;
                    SDNN = ((HeartRateVariation)objectForTime).sdnn;
                }
            }

            r[index++] = new com.miranda1000.samsunghealthexporter.entities.HeartRate(
                    time,
                    BPM,
                    RRI,
                    rMSSD,
                    SDNN
            );
        }

        return r;
    }

    public com.miranda1000.samsunghealthexporter.entities.SleepStage []parseSleepStage(com.miranda1000.samsunghealthexporter.jsons.SleepStage []sleepStages) throws IllegalArgumentException {
        final long AWAKE_MARGIN = 5*60;
        List<com.miranda1000.samsunghealthexporter.entities.SleepStage> r = new ArrayList<>(sleepStages.length);

        if (sleepStages.length > 0) {
            Arrays.sort(sleepStages); // as we're linearly checking times, it has to be sorted

            for (int n = 0; n < sleepStages.length; n++) {
                r.add(new com.miranda1000.samsunghealthexporter.entities.SleepStage(
                        sleepStages[n].getStartTime(),
                        SleepPhase.getFromValue(sleepStages[n].stage)
                ));

                if (n+1 < sleepStages.length) {
                    // is the next sleep very off to the current one? maybe they were awake
                    long differenceBetweenPhases = sleepStages[n+1].getStartTime().getEpochSecond() - sleepStages[n].getEndTime().getEpochSecond();
                    if (differenceBetweenPhases >= AWAKE_MARGIN) {
                        // too much difference; there need to be a awake stage in the middle
                        r.add(new SleepStage(
                                sleepStages[n].getEndTime(),
                                SleepPhase.AWAKE
                        ));
                    }
                }
            }

            // we still have to check the last value
            if (SleepPhase.getFromValue(sleepStages[sleepStages.length-1].stage) != SleepPhase.AWAKE) {
                // last stage got was not awake, so we have to add it
                r.add(new SleepStage(
                        sleepStages[sleepStages.length-1].getEndTime(),
                        SleepPhase.AWAKE
                ));
            }
        }


        return r.stream().toArray(com.miranda1000.samsunghealthexporter.entities.SleepStage[]::new);
    }

    public <T extends SamsungHealthData> T []sortByTime(T []data) {
        Arrays.sort(data);
        return data;
    }
}
