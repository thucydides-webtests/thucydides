package net.thucydides.core.reports.csv;

import au.com.bytecode.opencsv.CSVWriter;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.TestTag;
import net.thucydides.core.reports.TestOutcomes;
import net.thucydides.core.reports.ThucydidesReporter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;

/**
 * Stores test outcomes as CSV files
 */
public class CSVReporter extends ThucydidesReporter {
    private static final String CSV_RESULTS_FILE = "results.csv";
    private static final String[] TITLE_LINE = {"Story", "Title", "Result", "Date", "Stability", "Duration (s)"};
    private static final String[] OF_STRINGS = new String[]{};

    public CSVReporter(File outputDirectory) {
        this.setOutputDirectory(outputDirectory);
    }

    public File generateReportFor(TestOutcomes testOutcomes) throws IOException {
        CSVWriter writer = new CSVWriter(new FileWriter(getOutputFile()));
        writeTitleRow(writer);
        writeEachRow(testOutcomes.withHistory(), writer);
        writer.close();
        return getOutputFile();
    }

    private void writeTitleRow(CSVWriter writer) {
        writer.writeNext(TITLE_LINE);
    }

    private void writeEachRow(TestOutcomes testOutcomes, CSVWriter writer) {
        for (TestOutcome outcome : testOutcomes.getTests()) {
            writer.writeNext(
                    withRowData(
                            outcome.getStoryTitle(),
                            outcome.getTitle(),
                            outcome.getResult(),
                            outcome.getStartTime(),
                            passRateFor(outcome),
                            outcome.getDurationInSeconds()
                    )
            );
        }
    }

    private Double passRateFor(TestOutcome outcome) {
        return outcome.getStatistics().getPassRate().overTheLast(5).testRuns();
    }

    private String[] withRowData(Object... values) {
        return extract(values, on(Object.class).toString()).toArray(OF_STRINGS);
    }

    private File getOutputFile() {
        return new File(getOutputDirectory(), CSV_RESULTS_FILE);
    }
}
