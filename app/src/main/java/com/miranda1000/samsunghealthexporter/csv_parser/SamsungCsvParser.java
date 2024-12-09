package com.miranda1000.samsunghealthexporter.csv_parser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Samsung has an interesting way to output csv files...
 * The first row contains metadata (not the header),
 * and each data row is having an extra comma at the end.
 */
public abstract class SamsungCsvParser<T> {
    private final File csv;
    private String csvContents;
    protected ArrayList<HashMap<String,String>> data;

    public SamsungCsvParser(File csv) {
        this.csv = csv;
    }

    public SamsungCsvParser(String contents) {
        this.csv = null;
        this.csvContents = contents;
    }

    public T []parse() throws IOException {
        this.getRawData(); // ignore the result; just trigger the read file
        return this.parseDataToObject();
    }

    /**
     * Converts the read file into the data array
     */
    protected void parseCsvToData() throws IOException {
        this.data = new ArrayList<>();

        if (this.csvContents == null) {
            StringBuilder sb = new StringBuilder();
            try (Scanner reader = new Scanner(this.csv)) {
                while (reader.hasNextLine()) sb.append(reader.nextLine()).append('\n');
            }
            this.csvContents = sb.toString();
        }

        String []fileContents = this.csvContents.split("\n");
        int index = 1; // discard first row

        // read header
        String header = fileContents.length > index ? fileContents[index++] : "";
        String []columns = header.split(";");

        // read the rows
        while (fileContents.length > index) {
            String row = fileContents[index].trim();
            if (row.isEmpty()) continue; // empty

            String []arguments = row.split(";");
            if (arguments.length != columns.length+1) {
                throw new IOException("Failed reading file " + (this.csv != null ? this.csv.getName() : "?") + ": line " + (index+1) + " with different amount of parameters; " +
                        "expected " + columns.length + " got " + (arguments.length-1) + ".\nExtra information:\n\theader: " + header +
                        "\n\trow: " + row);
            }

            index++;
        }
    }

    /**
     * Converts the data array into a list of desired objects
     * @return List of parsed objects
     */
    protected abstract T []parseDataToObject();

    public ArrayList<HashMap<String,String>> getRawData() throws IOException {
        if (this.data == null) {
            // we must first get the data
            this.parseCsvToData();
        }

        return this.data;
    }
}
