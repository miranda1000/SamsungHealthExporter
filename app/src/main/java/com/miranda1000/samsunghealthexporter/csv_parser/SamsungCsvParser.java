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
    private final String splitChar;

    public SamsungCsvParser(char splitChar) {
        this.splitChar = String.valueOf(splitChar);
    }

    public SamsungCsvParser() {
        this(',');
    }

    public T []parse(String csvContents) throws IOException {
        ArrayList<HashMap<String,String>> rawData = this.parseCsvToData(csvContents);
        return this.parseDataToObject(rawData);
    }

    /**
     * Converts the read file into the data array
     */
    protected ArrayList<HashMap<String,String>> parseCsvToData(String csvContents) throws IOException {
        ArrayList<HashMap<String,String>> data = new ArrayList<>();

        String []fileContents = csvContents.split("\n");
        int index = 1; // discard first row

        // read header
        String header = fileContents.length > index ? fileContents[index++] : "";
        String []columns = header.split(this.splitChar);

        // read the rows
        while (fileContents.length > index) {
            String row = fileContents[index].trim();
            if (row.isEmpty()) continue; // empty

            String []arguments = row.split(this.splitChar, -1);
            if (arguments.length != columns.length+1) {
                throw new IOException("Failed parsing file: line " + (index+1) + " with different amount of parameters; " +
                        "expected " + columns.length + " got " + (arguments.length-1) + ".\nExtra information:\n\theader: " + header +
                        "\n\trow: " + row);
            }

            HashMap<String,String> rowData = new HashMap<>();
            for (int colNum = 0; colNum < columns.length; colNum++) {
                rowData.put(columns[colNum], arguments[colNum]);
            }
            data.add(rowData);

            index++;
        }

        return data;
    }

    /**
     * Converts the data array into a list of desired objects
     * @return List of parsed objects
     */
    protected abstract T []parseDataToObject(ArrayList<HashMap<String,String>> rawData);
}
