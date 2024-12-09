package com.miranda1000.samsunghealthexporter;

import com.google.gson.Gson;
import com.miranda1000.samsunghealthexporter.jsons.HeartRateVariation;
import org.junit.Test;

import java.time.Instant;

import static org.junit.Assert.*;

public class SamsungHealthJsonParserShould {
    @Test
    public void parseOxygenSaturationJsonData() {
        String in = "[{\"spo2\":97,\"spo2_max\":97,\"spo2_min\":96,\"end_time\":1733152451000,\"start_time\":1733152392000}," +
                    "{\"spo2\":96,\"spo2_max\":97,\"spo2_min\":94,\"end_time\":1733152511000,\"start_time\":1733152452000}]";

        Gson gson = new Gson();
        com.miranda1000.samsunghealthexporter.jsons.OxygenSaturation []jsonOxygenSaturations =
                gson.fromJson(in, com.miranda1000.samsunghealthexporter.jsons.OxygenSaturation[].class);

        SamsungHealthJsonParser uut = new SamsungHealthJsonParser();
        com.miranda1000.samsunghealthexporter.entities.OxygenSaturation []parsedOxygenSaturations =
                uut.parseOxygenSaturation(jsonOxygenSaturations);
        uut.sortByTime(parsedOxygenSaturations);

        assertEquals(2, parsedOxygenSaturations.length);
        assertEquals(0.97f, parsedOxygenSaturations[0].getOxygenInBlood());
        assertEquals(Instant.parse("2024-12-02T15:13:12.00Z"), parsedOxygenSaturations[0].getTime());
        assertEquals(0.96f, parsedOxygenSaturations[1].getOxygenInBlood());
        assertEquals(Instant.parse("2024-12-02T15:14:12.00Z"), parsedOxygenSaturations[1].getTime());
    }

    @Test
    public void parseBreathRateJsonData() {
        String in = "[{\"end_time\":1733179859000,\"respiratory_rate\":17.34375,\"start_time\":1733179800000}," +
                "{\"end_time\":1733179919000,\"respiratory_rate\":16.21875,\"start_time\":1733179860000}]";
        assert false;
    }

    @Test
    public void parseSkinTemperatureJsonData() {
        String in = "[{\"end_time\":1733152511000,\"max\":27.085714,\"mean\":27.085714,\"min\":27.085714,\"start_time\":1733152452000}," +
                "{\"end_time\":1733152571000,\"max\":27.101429,\"mean\":27.101429,\"min\":27.101429,\"start_time\":1733152512000}]";
        assert false;
    }

    @Test
    public void parseHeartRateJsons() {
        String heartRateIn = "[{\"end_time\":1733205659000,\"heart_rate\":68,\"heart_rate_max\":71,\"heart_rate_min\":66,\"start_time\":1733205600000}," +
                "{\"end_time\":1733205719000,\"heart_rate\":67.0,\"heart_rate_max\":71.0,\"heart_rate_min\":63.0,\"start_time\":1733205660000}]";
        String hr_rri_in = "[{\"heart_rate\":86,\"end_time\":1733205659000,\"start_time\":1733205600000,\"rri\":715}," +
                "{\"heart_rate\":86,\"end_time\":1733352571000,\"start_time\":1733352512000,\"rri\":710}]";
        String heartRateVariationIn = "[{\"rmssd\":52.171165,\"sdnn\":51.123722,\"end_time\":1733205659000,\"start_time\":1733205600000}," +
                "{\"rmssd\":49.358044,\"sdnn\":49.285156,\"end_time\":1733487951000,\"start_time\":1733487651000}]";

        Gson gson = new Gson();
        com.miranda1000.samsunghealthexporter.jsons.HeartRate []jsonHeartRates =
                gson.fromJson(heartRateIn, com.miranda1000.samsunghealthexporter.jsons.HeartRate[].class);
        com.miranda1000.samsunghealthexporter.jsons.HeartRateAndRRInterval []jsonHeartRatesAndRRIntervals =
                gson.fromJson(hr_rri_in, com.miranda1000.samsunghealthexporter.jsons.HeartRateAndRRInterval[].class);
        com.miranda1000.samsunghealthexporter.jsons.HeartRateVariation []jsonHeartRateVariations =
                gson.fromJson(heartRateVariationIn, com.miranda1000.samsunghealthexporter.jsons.HeartRateVariation[].class);

        SamsungHealthJsonParser uut = new SamsungHealthJsonParser();
        com.miranda1000.samsunghealthexporter.entities.HeartRate []parsedHeartRates =
                uut.parseHeartRate(jsonHeartRates, jsonHeartRatesAndRRIntervals, jsonHeartRateVariations);
        uut.sortByTime(parsedHeartRates);


        assertEquals(4, parsedHeartRates.length);

        assertEquals((Float) 68.0f, parsedHeartRates[0].getBPM());
        assertEquals((Float) 715.0f, parsedHeartRates[0].getRRI());
        assertEquals((Float) 52.171165f, parsedHeartRates[0].get_rMSSD());
        assertEquals((Float) 51.123722f, parsedHeartRates[0].getSDNN());

        assertEquals((Float) 67.0f, parsedHeartRates[1].getBPM());
        assertNull(parsedHeartRates[1].getRRI());
        assertNull(parsedHeartRates[1].get_rMSSD());
        assertNull(parsedHeartRates[1].getSDNN());

        assertEquals((Float) 86.0f, parsedHeartRates[2].getBPM());
        assertEquals((Float) 710.0f, parsedHeartRates[2].getRRI());
        assertNull(parsedHeartRates[2].get_rMSSD());
        assertNull(parsedHeartRates[2].getSDNN());

        assertNull(parsedHeartRates[3].getBPM());
        assertNull(parsedHeartRates[3].getRRI());
        assertEquals((Float) 49.358044f, parsedHeartRates[3].get_rMSSD());
        assertEquals((Float) 49.285156f, parsedHeartRates[3].getSDNN());
    }

    @Test
    public void heartRateFileDataShouldOverrideRRData() {
        String heartRateIn = "[{\"end_time\":1733205659000,\"heart_rate\":68,\"heart_rate_max\":71,\"heart_rate_min\":66,\"start_time\":1733205600000}]";
        String hr_rri_in = "[{\"heart_rate\":86,\"end_time\":1733205659000,\"start_time\":1733205600000,\"rri\":715}]";

        Gson gson = new Gson();
        com.miranda1000.samsunghealthexporter.jsons.HeartRate []jsonHeartRates =
                gson.fromJson(heartRateIn, com.miranda1000.samsunghealthexporter.jsons.HeartRate[].class);
        com.miranda1000.samsunghealthexporter.jsons.HeartRateAndRRInterval []jsonHeartRatesAndRRIntervals =
                gson.fromJson(hr_rri_in, com.miranda1000.samsunghealthexporter.jsons.HeartRateAndRRInterval[].class);

        SamsungHealthJsonParser uut = new SamsungHealthJsonParser();
        com.miranda1000.samsunghealthexporter.entities.HeartRate []parsedHeartRates =
                uut.parseHeartRate(jsonHeartRates, jsonHeartRatesAndRRIntervals, new HeartRateVariation[]{});
        uut.sortByTime(parsedHeartRates);

        assertEquals(1, parsedHeartRates.length);
        assertEquals((Float) 68.0f, parsedHeartRates[0].getBPM());
    }
}
