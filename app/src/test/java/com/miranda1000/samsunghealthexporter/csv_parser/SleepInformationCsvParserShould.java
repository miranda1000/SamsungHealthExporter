package com.miranda1000.samsunghealthexporter.csv_parser;

import com.miranda1000.samsunghealthexporter.jsons.SleepInformation;
import org.junit.Test;

import java.time.Instant;

import static org.junit.Assert.*;

public class SleepInformationCsvParserShould {
    @Test
    public void parseSamsungSleepCsvFiles() throws Exception {
        String in = "ignore this first line\n" +
                "original_efficiency,mental_recovery,factor_01,factor_02,factor_03,factor_04,factor_05,factor_06,factor_07,factor_08,factor_09,factor_10," +
                        "integrated_id,has_sleep_data,bedtime_detection_delay,wakeup_time_detection_delay,total_rem_duration,combined_id,sleep_type,sleep_latency," +
                        "data_version,physical_recovery,original_wake_up_time,movement_awakening,is_integrated,original_bed_time,goal_bed_time,quality,extra_data," +
                        "goal_wake_up_time,sleep_cycle,total_light_duration,efficiency,sleep_score,sleep_duration,stage_analyzed_type,com.samsung.health.sleep.create_sh_ver," +
                        "com.samsung.health.sleep.start_time,com.samsung.health.sleep.custom,com.samsung.health.sleep.modify_sh_ver,com.samsung.health.sleep.update_time," +
                        "com.samsung.health.sleep.create_time,com.samsung.health.sleep.time_offset,com.samsung.health.sleep.deviceuuid,com.samsung.health.sleep.comment," +
                        "com.samsung.health.sleep.pkg_name,com.samsung.health.sleep.end_time,com.samsung.health.sleep.datauuid\n" +
                ",34.0,33,33,16,1,16,226,225,0,0,,,0,840000,240000,19,,-1,540000,9,43.0,,15.0,,,,,,,2,158,92.0,48,226,2,100013,2024-12-02 15:00:00.000,," +
                        "62820030,2024-12-03 09:32:05.558,2024-12-02 18:46:00.000,UTC+0100,cZ0NQTEuC7,,com.sec.android.app.shealth,2024-12-02 18:46:00.000,00000193-87ef-a83e-4604-bca080861961,\n" +
                ",97.0,43,107,5,3,32,639,100,0,0,,,0,720000,0,162,,-1,660000,9,99.0,,10.0,,,,,,,8,338,94.0,79,639,2,100013,2024-12-02 22:38:00.000,," +
                        "62820030,2024-12-03 09:32:09.185,2024-12-03 09:17:00.000,UTC+0100,cZ0NQTEuC7,,com.sec.android.app.shealth,2024-12-03 09:17:00.000,00000193-8991-b09a-5704-bca080861961,\n";

        SleepInformationCsvParser parser = new SleepInformationCsvParser(in);
        SleepInformation []sleepInformations = parser.parse();

        assertEquals(2, sleepInformations.length);
        assertEquals(Instant.parse("2024-12-02T15:00:00.00Z"), sleepInformations[0].start_time);
        assertEquals(Instant.parse("2024-12-02T22:38:00.00Z"), sleepInformations[1].start_time);
        assertEquals(Instant.parse("2024-12-02T18:46:00.00Z"), sleepInformations[0].end_time);
        assertEquals(Instant.parse("2024-12-03T09:17:00.00Z"), sleepInformations[1].end_time);
        assertEquals(34.0f, sleepInformations[0].mental_recovery);
        assertEquals(97.0f, sleepInformations[1].mental_recovery);
        assertEquals(43.0f, sleepInformations[0].physical_recovery);
        assertEquals(99.0f, sleepInformations[1].physical_recovery);
    }
}
