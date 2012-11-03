package net.thucydides.junit.pipeline;

import au.com.bytecode.opencsv.CSVReader;
import net.thucydides.core.csv.FieldName;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CSVData {

    private final String path;

    public static CSVData fromFile(String path) throws FileNotFoundException {
        return new CSVData(path);

    }

    public CSVData(String path) {
        this.path = path;
    }

    private Reader readerFor(String path) throws FileNotFoundException {
        if (isAClasspathResource(path)) {
            try {
                return new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream(path));
            } catch(Throwable e) {
                throw new FileNotFoundException("Could not load test data from " + path);
            }
        }
        return new FileReader(new File(path));
    }

    private boolean isAClasspathResource(final String path) {
        return (!validFileSystemPath(path));
    }

    private boolean validFileSystemPath(final String path) {
        File file = new File(path);
        return file.exists();
    }

    public List<Map<String,String>> asMaps() throws IOException {
        Reader dataReader = readerFor(path);
        CSVReader reader = new CSVReader(dataReader, CSVReader.DEFAULT_SEPARATOR);
        List<String[]> rows = reader.readAll();

        List<Map<String, String>> loadedData = new ArrayList<Map<String, String>>();
        String[] titleRow = rows.get(0);

        for (int i = 1; i < rows.size(); i++) {
            String[] dataRow = rows.get(i);
            loadedData.add(dataEntryFrom(titleRow, dataRow));
        }
        return loadedData;
    }

    private Map<String, String> dataEntryFrom(final String[] titleRow, final String[] dataRow) {
        Map<String, String> dataset = new HashMap<String, String>();

        for (int column = 0; column < titleRow.length; column++) {
            if (column < dataRow.length) {
                dataset.put(titleFrom(titleRow[column]), dataValueFrom(dataRow[column]));
            }
        }

        return dataset;
    }

    private String dataValueFrom(String columnValue) {
        return columnValue.trim();
    }

    private String titleFrom(String columnName) {
        return FieldName.from(columnName).inNormalizedForm();
    }

}
