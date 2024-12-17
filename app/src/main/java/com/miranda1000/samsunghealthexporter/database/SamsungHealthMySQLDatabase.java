package com.miranda1000.samsunghealthexporter.database;

import android.util.Log;

import com.miranda1000.samsunghealthexporter.entities.*;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Instant;
import java.util.Arrays;

public class SamsungHealthMySQLDatabase implements SamsungHealthDatabase {
    private final String LOG_PREFIX = "SamsungHealthMySQLDatabase";

    private final String ddbb_ip;
    private final int ddbb_port;
    private final String ddbb_username;
    private final String usernamePassword;
    private final String ddbb_name;

    private java.sql.Connection ddbb_connection;

    public SamsungHealthMySQLDatabase(String ip, int port, String username, String password, String ddbbName) {
        this.ddbb_ip = ip;
        this.ddbb_port = port;
        this.ddbb_username = username;
        this.usernamePassword = password;
        this.ddbb_name = ddbbName;
    }

    public SamsungHealthMySQLDatabase(String ip, String username, String password, String ddbbName) {
        this(ip, 3306, username, password, ddbbName);
    }

    public void connect() throws Exception {
        Class.forName("com.mysql.jdbc.Driver");
        String url = "jdbc:mysql://" + this.ddbb_ip + ":" + this.ddbb_port + "/" + this.ddbb_name + "?useSSL=false";
        this.ddbb_connection = DriverManager.getConnection(url, this.ddbb_username, this.usernamePassword);
    }

    public boolean isConnected() throws SQLException {
        return this.ddbb_connection != null && !this.ddbb_connection.isClosed();
    }

    @Override
    public boolean canConnect() {
        try {
            if (!this.isConnected()) {
                this.connect();
            }

            return this.isConnected();
        } catch (Exception ex) {
            Log.w(LOG_PREFIX, "Cannot connect to database", ex);
            return false;
        }
    }

    public void createBreathRateTable() throws Exception {
        String query = "CREATE TABLE IF NOT EXISTS BreathRate (\n"
                + "time BIGINT PRIMARY KEY,\n"
                + "respiratory_rate FLOAT NOT NULL\n"
                + ")";
        this.ddbb_connection.prepareStatement(query)
                .execute();
    }

    @Override
    public void exportBreathRate(BreathRate[] breathRates) throws Exception {
        if (!this.isConnected()) this.connect();
        this.createBreathRateTable(); // just in case it's the first time

        // discard already pushed values
        String send_since_query = "SELECT IFNULL(MAX(time), -1) FROM BreathRate";
        java.sql.Statement st = this.ddbb_connection.createStatement();
        java.sql.ResultSet rs = st.executeQuery(send_since_query);
        if (!rs.next()) throw new SQLException("Couldn't select max time of sleep stages values");
        final Instant maxInsertedTimestamp = (rs.getLong(1) == -1) ? null : Instant.ofEpochMilli(rs.getLong(1));

        if (maxInsertedTimestamp != null) {
            // we have data inserted; insert only the new one
            breathRates = Arrays.stream(breathRates)
                    .filter(ss -> ss.getTime().compareTo(maxInsertedTimestamp) > 0)
                    .toArray(BreathRate[]::new);
        }

        Log.i(LOG_PREFIX, "Exporting breath info... (" + breathRates.length + " entries)");

        // add new values
        String insert_query = "INSERT INTO BreathRate(time, respiratory_rate) VALUES (?,?)";
        PreparedStatement statement = this.ddbb_connection.prepareStatement(insert_query);
        final int BATCH_SIZE = 1000;
        for (int i = 0; i < breathRates.length; i++) {
            BreathRate current = breathRates[i];
            statement.setLong(1, (current.getTime().getEpochSecond() * 1_000L) + (current.getTime().getNano() / 1_000_000L));
            statement.setFloat(2, current.getRespiratoryRate());
            statement.addBatch();

            if (i % BATCH_SIZE == BATCH_SIZE - 1) {
                statement.executeBatch();
                statement.clearBatch();
            }
        }
        if (breathRates.length % BATCH_SIZE != 0) statement.executeBatch();
    }

    public void createHeartRateTable() throws Exception {
        String query = "CREATE TABLE IF NOT EXISTS HeartRate (\n"
                                + "time BIGINT PRIMARY KEY,\n"
                                + "BPM FLOAT,\n"
                                + "RRI FLOAT,\n"
                                + "rMSSD FLOAT,\n"
                                + "SDNN FLOAT\n"
                            + ")";
        this.ddbb_connection.prepareStatement(query)
                .execute();
    }

    @Override
    public void exportHeartRate(HeartRate[] heartRates) throws Exception {
        if (!this.isConnected()) this.connect();
        this.createHeartRateTable(); // just in case it's the first time

        // TODO check all attributes except of just the biggest time
        String send_since_query = "SELECT IFNULL(MAX(time), -1) FROM HeartRate";
        java.sql.Statement st = this.ddbb_connection.createStatement();
        java.sql.ResultSet rs = st.executeQuery(send_since_query);
        if (!rs.next()) throw new SQLException("Couldn't select max time of heart rates values");
        final Instant maxInsertedTimestamp = (rs.getLong(1) == -1) ? null : Instant.ofEpochMilli(rs.getLong(1));

        if (maxInsertedTimestamp != null) {
            // we have data inserted; insert only the new one
            heartRates = Arrays.stream(heartRates)
                    .filter(hr -> hr.getTime().compareTo(maxInsertedTimestamp) > 0)
                    .toArray(HeartRate[]::new);
        }

        Log.i(LOG_PREFIX, "Exporting heart rate info... (" + heartRates.length + " entries)");

        String insert_query = "INSERT INTO HeartRate(time, BPM, RRI, rMSSD, SDNN) VALUES (?,?,?,?,?)";
        PreparedStatement statement = this.ddbb_connection.prepareStatement(insert_query);
        final int BATCH_SIZE = 1000;
        for (int i = 0; i < heartRates.length; i++) {
            HeartRate current = heartRates[i];
            statement.setLong(1, (current.getTime().getEpochSecond() * 1_000L) + (current.getTime().getNano() / 1_000_000L));
            if (current.getBPM() == null) statement.setNull(2, java.sql.Types.NULL);
            else statement.setFloat(2, current.getBPM());
            if (current.getRRI() == null) statement.setNull(3, java.sql.Types.NULL);
            else statement.setFloat(3, current.getRRI());
            if (current.get_rMSSD() == null) statement.setNull(4, java.sql.Types.NULL);
            else statement.setFloat(4, current.get_rMSSD());
            if (current.getSDNN() == null) statement.setNull(5, java.sql.Types.NULL);
            else statement.setFloat(5, current.getSDNN());
            statement.addBatch();

            if (i % BATCH_SIZE == BATCH_SIZE - 1) {
                statement.executeBatch();
                statement.clearBatch();
            }
        }
        if (heartRates.length % BATCH_SIZE != 0) statement.executeBatch();
    }

    public void createOxygenSaturationTable() throws Exception {
        String query = "CREATE TABLE IF NOT EXISTS OxygenSaturation (\n"
                + "time BIGINT PRIMARY KEY,\n"
                + "oxygen_in_blood FLOAT NOT NULL\n"
                + ")";
        this.ddbb_connection.prepareStatement(query)
                .execute();
    }

    @Override
    public void exportOxygenSaturation(OxygenSaturation[] oxygenSaturations) throws Exception {
        if (!this.isConnected()) this.connect();
        this.createOxygenSaturationTable(); // just in case it's the first time


        // discard already pushed values
        String send_since_query = "SELECT IFNULL(MAX(time), -1) FROM OxygenSaturation";
        java.sql.Statement st = this.ddbb_connection.createStatement();
        java.sql.ResultSet rs = st.executeQuery(send_since_query);
        if (!rs.next()) throw new SQLException("Couldn't select max time of sleep stages values");
        final Instant maxInsertedTimestamp = (rs.getLong(1) == -1) ? null : Instant.ofEpochMilli(rs.getLong(1));

        if (maxInsertedTimestamp != null) {
            // we have data inserted; insert only the new one
            oxygenSaturations = Arrays.stream(oxygenSaturations)
                    .filter(ss -> ss.getTime().compareTo(maxInsertedTimestamp) > 0)
                    .toArray(OxygenSaturation[]::new);
        }

        Log.i(LOG_PREFIX, "Exporting oxygen info... (" + oxygenSaturations.length + " entries)");

        // add new values
        String insert_query = "INSERT INTO OxygenSaturation(time, oxygen_in_blood) VALUES (?,?)";
        PreparedStatement statement = this.ddbb_connection.prepareStatement(insert_query);
        final int BATCH_SIZE = 1000;
        for (int i = 0; i < oxygenSaturations.length; i++) {
            OxygenSaturation current = oxygenSaturations[i];
            statement.setLong(1, (current.getTime().getEpochSecond() * 1_000L) + (current.getTime().getNano() / 1_000_000L));
            statement.setFloat(2, current.getOxygenInBlood());
            statement.addBatch();

            if (i % BATCH_SIZE == BATCH_SIZE - 1) {
                statement.executeBatch();
                statement.clearBatch();
            }
        }
        if (oxygenSaturations.length % BATCH_SIZE != 0) statement.executeBatch();
    }

    public void createSleepStageTable() throws Exception {
        String query = "CREATE TABLE IF NOT EXISTS SleepStage (\n"
                            + "time BIGINT PRIMARY KEY,\n"
                            + "phase ENUM('Awake', 'REM', 'Light', 'Heavy') NOT NULL\n"
                        + ")";
        this.ddbb_connection.prepareStatement(query)
                .execute();
    }

    @Override
    public void exportSleepStage(SleepStage[] sleepStages) throws Exception {
        if (!this.isConnected()) this.connect();
        this.createSleepStageTable(); // just in case it's the first time

        // discard already pushed values
        String send_since_query = "SELECT IFNULL(MAX(time), -1) FROM SleepStage";
        java.sql.Statement st = this.ddbb_connection.createStatement();
        java.sql.ResultSet rs = st.executeQuery(send_since_query);
        if (!rs.next()) throw new SQLException("Couldn't select max time of sleep stages values");
        final Instant maxInsertedTimestamp = (rs.getLong(1) == -1) ? null : Instant.ofEpochMilli(rs.getLong(1));

        if (maxInsertedTimestamp != null) {
            // we have data inserted; insert only the new one
            sleepStages = Arrays.stream(sleepStages)
                    .filter(ss -> ss.getTime().compareTo(maxInsertedTimestamp) > 0)
                    .toArray(SleepStage[]::new);
        }

        Log.i(LOG_PREFIX, "Exporting sleep info... (" + sleepStages.length + " entries)");

        // add new values
        String insert_query = "INSERT INTO SleepStage(time, phase) VALUES (?,?)";
        PreparedStatement statement = this.ddbb_connection.prepareStatement(insert_query);
        final int BATCH_SIZE = 1000;
        for (int i = 0; i < sleepStages.length; i++) {
            SleepStage current = sleepStages[i];
            statement.setLong(1, (current.getTime().getEpochSecond() * 1_000L) + (current.getTime().getNano() / 1_000_000L));
            statement.setString(2, current.getSleepPhase().toString());
            statement.addBatch();

            if (i % BATCH_SIZE == BATCH_SIZE - 1) {
                statement.executeBatch();
                statement.clearBatch();
            }
        }
        if (sleepStages.length % BATCH_SIZE != 0) statement.executeBatch();
    }

    public void createTemperatureTable() throws Exception {
        String query = "CREATE TABLE IF NOT EXISTS Temperature (\n"
                + "time BIGINT PRIMARY KEY,\n"
                + "temperature FLOAT NOT NULL\n"
                + ")";
        this.ddbb_connection.prepareStatement(query)
                .execute();
    }

    @Override
    public void exportTemperature(Temperature[] temperatures) throws Exception {
        if (!this.isConnected()) this.connect();
        this.createTemperatureTable(); // just in case it's the first time

        // discard already pushed values
        String send_since_query = "SELECT IFNULL(MAX(time), -1) FROM Temperature";
        java.sql.Statement st = this.ddbb_connection.createStatement();
        java.sql.ResultSet rs = st.executeQuery(send_since_query);
        if (!rs.next()) throw new SQLException("Couldn't select max time of sleep stages values");
        final Instant maxInsertedTimestamp = (rs.getLong(1) == -1) ? null : Instant.ofEpochMilli(rs.getLong(1));

        if (maxInsertedTimestamp != null) {
            // we have data inserted; insert only the new one
            temperatures = Arrays.stream(temperatures)
                    .filter(ss -> ss.getTime().compareTo(maxInsertedTimestamp) > 0)
                    .toArray(Temperature[]::new);
        }

        Log.i(LOG_PREFIX, "Exporting temperature info... (" + temperatures.length + " entries)");

        // add new values
        String insert_query = "INSERT INTO Temperature(time, temperature) VALUES (?,?)";
        PreparedStatement statement = this.ddbb_connection.prepareStatement(insert_query);
        final int BATCH_SIZE = 1000;
        for (int i = 0; i < temperatures.length; i++) {
            Temperature current = temperatures[i];
            statement.setLong(1, (current.getTime().getEpochSecond() * 1_000L) + (current.getTime().getNano() / 1_000_000L));
            statement.setFloat(2, current.getTemperature());
            statement.addBatch();

            if (i % BATCH_SIZE == BATCH_SIZE - 1) {
                statement.executeBatch();
                statement.clearBatch();
            }
        }
        if (temperatures.length % BATCH_SIZE != 0) statement.executeBatch();
    }
}
