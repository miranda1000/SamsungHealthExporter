package com.miranda1000.samsunghealthexporter.csv_parser;

import com.miranda1000.samsunghealthexporter.jsons.SleepStage;

import java.io.File;

public class SleepStageCsvParser extends SamsungCsvParser<SleepStage> {
    public SleepStageCsvParser(File csv) {
        super(csv);
    }

    public SleepStageCsvParser(String contents) {
        super(contents);
    }

    @Override
    protected SleepStage[] parseDataToObject() {
        return null;
    }
}
