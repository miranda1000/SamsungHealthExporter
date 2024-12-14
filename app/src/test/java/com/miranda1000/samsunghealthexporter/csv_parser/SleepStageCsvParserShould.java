package com.miranda1000.samsunghealthexporter.csv_parser;

import static org.junit.Assert.assertEquals;

import com.miranda1000.samsunghealthexporter.jsons.SleepInformation;
import com.miranda1000.samsunghealthexporter.jsons.SleepStage;

import org.junit.Test;

import java.time.Instant;

public class SleepStageCsvParserShould {
    @Test
    public void parseSamsungSleepCsvFiles() throws Exception {
        String in = "ignore this first line\n" +
                "create_sh_ver,start_time,sleep_id,custom,modify_sh_ver,update_time,create_time,stage,time_offset,deviceuuid,pkg_name,end_time,datauuid\n" +
                ",2019-06-07 21:54:00.000,6ef8546f-158d-87ec-8df3-900b8114b105,,,2019-06-07 22:00:51.837,2019-06-07 22:00:51.837,40001,UTC+0200,2hPSWAZyEc,com.sec.android.app.shealth,2019-06-07 21:55:00.000,ac14d40a-67b5-eb51-a000-bcb2c0aa7663,\n" +
                ",2019-06-07 21:21:00.000,6ef8546f-158d-87ec-8df3-900b8114b105,,,2019-06-07 22:00:51.837,2019-06-07 22:00:51.837,40002,UTC+0200,2hPSWAZyEc,com.sec.android.app.shealth,2019-06-07 21:22:00.000,ba72d5b8-26ab-8e3f-78a4-785616cf96ad,\n";

        SleepStageCsvParser parser = new SleepStageCsvParser();
        SleepStage []sleepStages = parser.parse(in);

        assertEquals(2, sleepStages.length);
        assertEquals(Instant.parse("2019-06-07T21:54:00.00Z"), sleepStages[0].start_time);
        assertEquals(Instant.parse("2019-06-07T21:21:00.00Z"), sleepStages[1].start_time);
        assertEquals(40001, sleepStages[0].stage);
        assertEquals(40002, sleepStages[1].stage);
    }
}
