package com.miranda1000.samsunghealthexporter.csv_parser;

import com.miranda1000.samsunghealthexporter.jsons.SleepInformation;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class SleepInformationCsvParser extends SamsungCsvParser<SleepInformation> {
    @Override
    protected SleepInformation[] parseDataToObject(ArrayList<HashMap<String,String>> data) {
        int index = 0;
        SleepInformation []sleepInformations = new SleepInformation[data.size()];
        for (HashMap<String,String> entry : data) {
            /*sleepInformations[index++] = new SleepInformation(
                    entry.get("mental_recovery"),
                    entry.get("physical_recovery"),
                    entry.get("com.samsung.health.sleep.start_time"),
                    entry.get("com.samsung.health.sleep.end_time")
            );*/
        }
        return sleepInformations;
    }
}
