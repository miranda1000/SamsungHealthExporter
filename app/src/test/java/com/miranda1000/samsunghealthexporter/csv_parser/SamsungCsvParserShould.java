package com.miranda1000.samsunghealthexporter.csv_parser;

import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.*;

public class SamsungCsvParserShould {
    private class SamsungCsvParserWrapper extends SamsungCsvParser<Object> {
        public SamsungCsvParserWrapper(String contents) {
            super(contents);
        }

        @Override
        protected Object[] parseDataToObject() {
            return null;
        }
    }

    @Test
    public void parseASamsungCsv() throws Exception {
        String in = "the first line should always be ignored\n" +
                "line1,line2\n" +
                "2,we specify an extra coma here," +
                "0,,";

        SamsungCsvParserWrapper parser = new SamsungCsvParserWrapper(in);

        ArrayList<HashMap<String,String>> data = parser.getRawData();
        assertEquals(2, data.size());
        assertEquals(2, data.get(0).size());
        assertEquals(2, data.get(1).size());
        assertTrue(data.get(0).containsKey("line1"));
        assertTrue(data.get(1).containsKey("line1"));
        assertTrue(data.get(0).containsKey("line2"));
        assertTrue(data.get(1).containsKey("line2"));
        assertEquals("2", data.get(0).get("line1"));
        assertEquals("0", data.get(1).get("line1"));
        assertEquals("we specify an extra coma here", data.get(0).get("line2"));
        assertEquals("", data.get(1).get("line2"));
    }

    @Test
    public void notCrashOnEmptyFiles() throws Exception {
        SamsungCsvParserWrapper parser = new SamsungCsvParserWrapper("");
        parser.getRawData();

        parser = new SamsungCsvParserWrapper("metadata\n");
        parser.getRawData();
    }

    @Test
    public void notCrashOnEmptyRows() throws Exception {
        SamsungCsvParserWrapper parser = new SamsungCsvParserWrapper("metadata\ndata\n");
        ArrayList<HashMap<String,String>> data = parser.getRawData();
        assertEquals(0, data.size());
    }

    @Test
    public void provideVerboseErrorData() throws Exception {
        SamsungCsvParserWrapper parser = new SamsungCsvParserWrapper("metadata\ndata\nasd,extra,");
        try {
            ArrayList<HashMap<String,String>> data = parser.getRawData();
            fail("Shouldn't reach; exception raised was expected to occur");
        } catch (IOException ex) {
            // include failed line
            assertTrue("Expected to include line on error msg; got otherwise instead.\nerror: " + ex.toString(), ex.getMessage().contains("line 3"));
            // include failed file (in this case, unspecified)
            assertTrue("Expected to include file name on error msg; got otherwise instead.\nerror: " + ex.toString(), ex.getMessage().contains("file ?"));
            // include failed row
            assertTrue("Expected to include failed line on error msg; got otherwise instead.\nerror: " + ex.toString(), ex.getMessage().contains("asd,extra"));
            // include header
            assertTrue("Expected to include file header on error msg; got otherwise instead.\nerror: " + ex.toString(), ex.getMessage().contains("data"));
        }
    }
}