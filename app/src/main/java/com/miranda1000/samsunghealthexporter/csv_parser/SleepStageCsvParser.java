package com.miranda1000.samsunghealthexporter.csv_parser;

import com.miranda1000.samsunghealthexporter.jsons.SleepStage;

import java.util.ArrayList;
import java.util.HashMap;

public class SleepStageCsvParser extends SamsungCsvParser<SleepStage> {
    @Override
    protected SleepStage[] parseDataToObject(ArrayList<HashMap<String,String>> data) {
        int index = 0;
        SleepStage[]sleepStages = new SleepStage[data.size()];

        for (HashMap<String,String> entry : data) {
            sleepStages[index++] = new SleepStage(
                    entry.get("start_time"),
                    entry.get("end_time"),
                    Integer.parseInt(entry.get("stage"))
            );
        }
        return sleepStages;
    }
}
