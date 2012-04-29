package net.thucydides.core.reports;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.reports.xml.XMLTestOutcomeReporter;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Loads test outcomes from a given directory, and reports on their contents.
 * This class is used for aggregate reporting.
 */
public class TestOutcomeLoader {

    /**
     * Load the test outcomes from a given directory.
     * @param reportDirectory An existing directory that contains the test outcomes in XML format.
     * @return The full list of test outcomes.
     * @throws java.io.IOException Thrown if the specified directory was invalid.
     */
    public List<TestOutcome> loadFrom(final File reportDirectory) throws IOException {

        XMLTestOutcomeReporter testOutcomeReporter = new XMLTestOutcomeReporter();

        List<File> reportFiles = getAllXMLFilesFrom(reportDirectory);

        List<TestOutcome> testOutcomes = Lists.newArrayList();
        for (File reportFile : reportFiles) {
            Optional<TestOutcome> testOutcome = testOutcomeReporter.loadReportFrom(reportFile);
            testOutcomes.addAll(testOutcome.asSet());
        }

        return ImmutableList.copyOf(testOutcomes);
    }


    private List<File> getAllXMLFilesFrom(final File reportsDirectory) throws IOException{
        File[] matchingFiles = reportsDirectory.listFiles(new XmlFilenameFilter());
        if (matchingFiles == null) {
            throw new IOException("Could not findBy directory " + reportsDirectory);
        }
        return ImmutableList.copyOf(matchingFiles);
    }

    public static TestOutcomes testOutcomesIn(final File reportsDirectory) throws IOException {
        TestOutcomeLoader loader = new TestOutcomeLoader();
        return TestOutcomes.of(loader.loadFrom(reportsDirectory));
    }

    private static final class XmlFilenameFilter implements FilenameFilter {
        public boolean accept(final File file, final String filename) {
            return filename.toLowerCase(Locale.getDefault()).endsWith(".xml");
        }
    }
}
