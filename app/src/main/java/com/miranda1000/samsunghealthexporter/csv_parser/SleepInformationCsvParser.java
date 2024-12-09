package com.miranda1000.samsunghealthexporter.csv_parser;

import com.miranda1000.samsunghealthexporter.jsons.SleepInformation;

import java.io.File;

public class SleepInformationCsvParser extends SamsungCsvParser<SleepInformation> {
    public SleepInformationCsvParser(File csv) {
        super(csv);
    }

    public SleepInformationCsvParser(String contents) {
        super(contents);
    }

    @Override
    protected SleepInformation[] parseDataToObject() {
        return null;
    }
}
