package com.miranda1000.samsunghealthexporter;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.documentfile.provider.DocumentFile;

import com.google.gson.Gson;
import com.miranda1000.samsunghealthexporter.entities.SamsungHealthData;
import com.miranda1000.samsunghealthexporter.jsons.HeartRateAndRRInterval;
import com.miranda1000.samsunghealthexporter.jsons.HeartRateVariation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SamsungHealthDiskSystem {
    private final String LOG_PREFIX = "SamsungHealthDiskSystem";
    private final SamsungHealthJsonParser samsungHealthJsonParser = new SamsungHealthJsonParser();

    private final Context context;
    private final Gson gson;

    public SamsungHealthDiskSystem(Context context) {
        this.context = context;
        this.gson = new Gson();
    }

    @Nullable
    public DocumentFile getLatestExport(Uri downloadsPath) {
        // we expect 'Download/Samsung Health' to be passed
        DocumentFile directory = DocumentFile.fromTreeUri(this.context, downloadsPath);
        if (directory == null || !directory.isDirectory()) {
            Log.e(LOG_PREFIX, "Specified a file instead of a directory");
            return null;
        }

        Instant mostRecentExport = Instant.MIN;
        DocumentFile mostRecentExportFile = null;

        Pattern samsungHealthExportPattern = Pattern.compile("^samsunghealth_.+_(\\d{4})(\\d{2})(\\d{2})(\\d{2})(\\d{2})");
        for (DocumentFile subdir : directory.listFiles()) {
            if (!subdir.isDirectory()) continue;

            Matcher m = samsungHealthExportPattern.matcher(subdir.getName());
            if (!m.find()) continue;

            String year = m.group(1),
                    month = m.group(2),
                    day = m.group(3),
                    hour = m.group(4),
                    minute = m.group(5);

            Instant exportTime = Instant.parse(year + "-" + month + "-" + day + "T" + hour + ":" + minute + ":00.00Z");
            Log.i(LOG_PREFIX, "Found SamsungHealth export made at " + exportTime.toString());
            if (mostRecentExport.compareTo(exportTime) < 0) {
                Log.v(LOG_PREFIX, "Export was more recent than the last one; keeping this instead");
                mostRecentExport = exportTime;
                mostRecentExportFile = subdir;
            }
        }

        if (mostRecentExportFile == null) Log.i(LOG_PREFIX, "Couldn't find any export");
        return mostRecentExportFile;
    }

    public com.miranda1000.samsunghealthexporter.entities.HeartRate []extractHeartRate(@NonNull DocumentFile samsungHealth) {
        return this.samsungHealthJsonParser.sortByTime(
                this.samsungHealthJsonParser.parseHeartRate(
                        this.getHeartRateParsedFiles(samsungHealth),
                        this.getHeartRateAndRRIntervalParsedFiles(samsungHealth),
                        this.getHeartRateVariation(samsungHealth)
                )
        );
    }

    /**
     * Almost every data is within the `jsons/` folder; this method returns it.
     * @return DocumentFile within the jsons; shouldn't be null
     */
    protected DocumentFile getJsonsFolder(@NonNull DocumentFile samsungHealth) {
        for (DocumentFile subdir : samsungHealth.listFiles()) {
            if (!subdir.isDirectory()) continue;

            if (subdir.getName().equals("jsons")) return subdir;
        }

        Log.i(LOG_PREFIX, "Requested to get the jsons folder within " + samsungHealth.getName() + ", but found nothing");
        return null;
    }

    /**
     * Parses a JSON file into the specified type.
     *
     * @param file The DocumentFile representing the JSON file.
     * @param type The class of the desired type.
     * @param <T>  The generic type to parse into.
     * @return An instance of the specified type, or null if parsing fails.
     */
    protected <T> T parseJsonFile(DocumentFile file, Class<T> type) {
        if (file == null || !file.isFile()) {
            throw new IllegalArgumentException("The provided DocumentFile is invalid or not a file.");
        }

        Uri fileUri = file.getUri();
        ContentResolver contentResolver = this.context.getContentResolver();

        try (InputStream inputStream = contentResolver.openInputStream(fileUri);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

            // Read the JSON content into a string
            StringBuilder jsonBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonBuilder.append(line);
            }

            // Parse the JSON string into the desired type using Gson
            Log.i(LOG_PREFIX, "Parsing " + file.getName() + "...");
            return gson.fromJson(jsonBuilder.toString(), type);

        } catch (IOException e) {
            Log.e(LOG_PREFIX, "Exception while trying to read the file " + file.getName(), e);
            return null; // Return null in case of an error
        }
    }

    protected <T> List<T> parseJsonFiles(DocumentFile root, @Nullable Pattern folderMatch, Pattern fileMatch, Class<T> outObjectType) {
        List<T> r = new ArrayList<>();

        for (DocumentFile entry : root.listFiles()) {
            if (entry.isDirectory()) {
                if (folderMatch != null) {
                    Matcher m = folderMatch.matcher(entry.getName());
                    if (!m.find()) continue; // we're filtering by folder but doesn't match
                }
                // folder matches; enter
                // add any file from there; this time don't filter folder
                r.addAll(this.parseJsonFiles(entry, null, fileMatch, outObjectType));
            }

            if (entry.isFile()) {
                Matcher m = fileMatch.matcher(entry.getName());
                if (m.find()) r.add(this.parseJsonFile(entry, outObjectType));
            }
        }

        return r;
    }

    private com.miranda1000.samsunghealthexporter.jsons.HeartRate []getHeartRateParsedFiles(@NonNull DocumentFile samsungHealth) {
        return this.parseJsonFiles(
                this.getJsonsFolder(samsungHealth),
                Pattern.compile("^com\\.samsung\\.shealth\\.tracker\\.heart_rate$"),
                Pattern.compile("\\.json$"),
                com.miranda1000.samsunghealthexporter.jsons.HeartRate[].class
        ).stream().flatMap(Arrays::stream).toArray(com.miranda1000.samsunghealthexporter.jsons.HeartRate[]::new);
    }

    private HeartRateAndRRInterval []getHeartRateAndRRIntervalParsedFiles(@NonNull DocumentFile samsungHealth) {
        return this.parseJsonFiles(
                this.getJsonsFolder(samsungHealth),
                Pattern.compile("^com\\.samsung\\.shealth\\.cycle\\.daily_temperature\\.raw$"),
                Pattern.compile("\\.hr_rri\\.json$"),
                HeartRateAndRRInterval[].class
        ).stream().flatMap(Arrays::stream).toArray(HeartRateAndRRInterval[]::new);
    }

    private HeartRateVariation []getHeartRateVariation(@NonNull DocumentFile samsungHealth) {
        return this.parseJsonFiles(
                this.getJsonsFolder(samsungHealth),
                Pattern.compile("^com\\.samsung\\.health\\.hrv$"),
                Pattern.compile("\\.json$"),
                HeartRateVariation[].class
        ).stream().flatMap(Arrays::stream).toArray(HeartRateVariation[]::new);
    }
}
