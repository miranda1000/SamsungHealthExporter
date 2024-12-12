package com.miranda1000.samsunghealthexporter.database;

import com.miranda1000.samsunghealthexporter.entities.HeartRate;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.time.Instant;

import static org.junit.Assert.*;

import androidx.test.ext.junit.runners.AndroidJUnit4;

@RunWith(AndroidJUnit4.class)
public class GivenAnOpenMySQLDatabaseThenSamsungHealthMySQLDatabaseShould {
    private static final String ip = "192.168.1.80";
    private static final String username = "root",
                                password = "admin";
    private static final String ddbbName = "health"; //"health-it";

    @Test
    public void connectToTheDatabase() throws Exception {
        SamsungHealthMySQLDatabase database = new SamsungHealthMySQLDatabase(ip, username, password, ddbbName);
        assertTrue(database.canConnect());
    }

    @Test
    public void insertHeartDataOnANewDatabase() throws Exception {
        SamsungHealthMySQLDatabase database = new SamsungHealthMySQLDatabase(ip, username, password, ddbbName);
        database.exportHeartRate(new HeartRate[]{
                new HeartRate(Instant.parse("2010-12-01T01:00:00.00Z"), 80.0f, 10.0f, 10.0f, 10.0f),
                new HeartRate(Instant.parse("2010-12-01T01:00:01.00Z"), 100.0f, 10.0f, 11.0f, 10.1f),
                new HeartRate(Instant.parse("2010-12-01T01:00:02.00Z"), 110.0f)
        });

        // TODO no way to validate
    }

    @Test
    public void insertOnlyNewHeartData() throws Exception {
        SamsungHealthMySQLDatabase database = new SamsungHealthMySQLDatabase(ip, username, password, ddbbName);
        database.exportHeartRate(new HeartRate[]{
                new HeartRate(Instant.parse("2010-12-01T01:00:00.00Z"), 80.0f, 10.0f, 10.0f, 10.0f),
        });
        database.exportHeartRate(new HeartRate[]{
                new HeartRate(Instant.parse("2010-12-01T01:00:00.00Z"), 80.0f, 10.0f, 10.0f, 10.0f),
                new HeartRate(Instant.parse("2010-12-01T01:00:02.00Z"), 110.0f)
        });
    }
}
