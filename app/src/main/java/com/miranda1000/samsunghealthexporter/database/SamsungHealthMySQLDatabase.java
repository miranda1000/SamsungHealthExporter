package com.miranda1000.samsunghealthexporter.database;

import com.miranda1000.samsunghealthexporter.NotImplementedException;
import com.miranda1000.samsunghealthexporter.entities.*;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Instant;
import java.util.Arrays;

public class SamsungHealthMySQLDatabase implements SamsungHealthDatabase {
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
        String url = "jdbc:mysql://" + this.ddbb_ip + ":" + this.ddbb_port + "/" + this.ddbb_name;
        this.ddbb_connection = DriverManager.getConnection(url, this.ddbb_username, this.usernamePassword);
    }

    public boolean isConnected() throws SQLException {
        return this.ddbb_connection != null && !this.ddbb_connection.isClosed();
    }

    @Override
    public boolean canConnect() throws Exception {
        if (!this.isConnected()) {
            this.connect();
        }

        return this.isConnected();
    }

    public void createBreathRateTable() throws Exception {
        throw new NotImplementedException();
    }

    @Override
    public void exportBreathRate(BreathRate[] breathRates) throws Exception {
        throw new NotImplementedException();
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
        throw new NotImplementedException();
    }

    @Override
    public void exportOxygenSaturation(OxygenSaturation[] oxygenSaturations) throws Exception {
        throw new NotImplementedException();
    }

    public void createSleepStageTable() throws Exception {
        throw new NotImplementedException();
    }

    @Override
    public void exportSleepStage(SleepStage[] sleepStages) throws Exception {
        throw new NotImplementedException();
    }

    public void createTemperatureTable() throws Exception {
        throw new NotImplementedException();
    }

    @Override
    public void exportTemperature(Temperature[] temperatures) throws Exception {
        throw new NotImplementedException();
    }
}
