# SamsungHealth Exporter

This app helps upload the exported data from SamsungHealth into a database.

The data to include is:
- Weight [TODO]
- Steps [TODO]
- Time doing activity [TODO]
- Calories burnt [TODO]
- Floor count [TODO]
- Heart rate
- Oxygen in blood
- Body temperature
- Stress [TODO]
- Respiratory rate
- Sleep information
  - Sleep phases
  - Movement during sleep [TODO]
  - Physical recuperation [TODO]
  - Mental recuperation [TODO]
  - Relaxing [TODO]

Also, you'll have this features:
- Multiple database models to choose [TODO; for now only MySQL]
- Remind you to export periodically the data [TODO]
- Integration with Home Assistant [TODO]

## (more) TODO list
- Export progress bar
- Specify latest export time
- Change DDBB connection from within the APP
- Guide for the user on how to export SamsungHealth data
- An app icon
- Optimization: discard data at first if it's older instead of parsing all then discarding
- Lock rotation

## Database preparations

You'll need a **MySQL database with a schema and a user with insert and select permissions**.

### Database recommendations

We store the time in UNIX timestamp + 3 digits as milliseconds to avoid errors in countries that change the timezone;
to more easily see the dates I suggest you to create views that turn that into a human-readable time:

```
CREATE VIEW HeartRateHumanTime AS
SELECT from_unixtime(HeartRate.time / 1000, '%Y-%m-%d %H:%i:%s') as `time`, BPM, RRI, rMSSD, SDNN
FROM HeartRate;

CREATE VIEW SleepStageHumanTime AS
SELECT from_unixtime(SleepStage.time / 1000, '%Y-%m-%d %H:%i:%s') as `time`, phase
FROM SleepStage;

CREATE VIEW BreathRateHumanTime AS
SELECT from_unixtime(BreathRate.time / 1000, '%Y-%m-%d %H:%i:%s') as `time`, respiratory_rate
FROM BreathRate;

CREATE VIEW OxygenSaturationHumanTime AS
SELECT from_unixtime(OxygenSaturation.time / 1000, '%Y-%m-%d %H:%i:%s') as `time`, oxygen_in_blood
FROM OxygenSaturation;

CREATE VIEW TemperatureHumanTime AS
SELECT from_unixtime(Temperature.time / 1000, '%Y-%m-%d %H:%i:%s') as `time`, temperature
FROM Temperature;
```