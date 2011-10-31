package net.thucydides.core.reports.history;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.StreamException;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.model.FeatureResults;
import net.thucydides.core.reports.html.history.TestResultSnapshot;
import net.thucydides.core.util.EnvironmentVariables;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Keep track of the test results over time.
 */
public class TestHistory {

    private static final String BUILD_ID = "BUILD_ID";
    private final File dataDirectory;
    private final String projectName;

    protected EnvironmentVariables environmentVariables;

    public TestHistory(final String projectName) {
        this(projectName, Injectors.getInjector().getInstance(EnvironmentVariables.class));
    }
    public TestHistory(final String projectName, final EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;
        this.projectName = projectName;
        dataDirectory = new File(getBaseDirectoryPath());
    }


    private String getBaseDirectoryPath() {
        String defaultBaseDirectory = new File(homeDirectory(), ".thucydides").getAbsolutePath();
        return environmentVariables.getProperty(ThucydidesSystemProperty.HISTORY_BASE_DIRECTORY.getPropertyName(),
                                                defaultBaseDirectory);
    }

    private String homeDirectory() {
        return environmentVariables.getProperty("user.home");
    }

    public void updateData(List<FeatureResults> featureResults) {
        int totalStepCount = countTotalStepsIn(featureResults);
        int passingSteps =  countPassingStepsIn(featureResults);
        int failingSteps = countFailingStepsIn(featureResults);
        int skippedSteps = countSkippedStepsIn(featureResults);
        String buildId = getEnvironmentVariables().getValue(BUILD_ID, "MANUAL");

        TestResultSnapshot newSnapshot = new TestResultSnapshot(totalStepCount,
                                                                passingSteps,
                                                                failingSteps,
                                                                skippedSteps,
                                                                buildId);

        try {
            save(newSnapshot);
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException("Unable to store history data", e);
        }
    }

    private int countTotalStepsIn(final List<FeatureResults> featureResults) {
        int total = 0;
        for(FeatureResults results : featureResults) {
            total += results.getEstimatedTotalSteps();
        }
        return total;
    }

    private int countPassingStepsIn(final List<FeatureResults> featureResults) {
        int total = 0;
        for(FeatureResults results : featureResults) {
            total += results.getPassingSteps();
        }
        return total;
    }

    private int countFailingStepsIn(final List<FeatureResults> featureResults) {
        int total = 0;
        for(FeatureResults results : featureResults) {
            total += results.getFailingSteps();
        }
        return total;
    }

    private int countSkippedStepsIn(final List<FeatureResults> featureResults) {
        int total = 0;
        for(FeatureResults results : featureResults) {
            total += results.getSkippedSteps();
        }
        return total;
    }

    private void save(TestResultSnapshot snapshot) throws FileNotFoundException {
        XStream xstream = new XStream();
        File snapshotFile = new File(getDirectory(), historyPrefix() + snapshot.getTime().getMillis());
        OutputStream out = null;
        try {
            out = new FileOutputStream(snapshotFile);
            xstream.toXML(snapshot, out);
        } finally {
            close(out);
        }

    }

    private void close(Closeable stream) {
        try {
            stream.close();
        } catch (IOException e) {
            throw new IllegalArgumentException("Unable to close history file", e);
        }
    }

    public File getDirectory() {
        File projectDirectory = new File(dataDirectory, projectName);
        if (!projectDirectory.exists()) {
            projectDirectory.mkdirs();
        }
        return projectDirectory;
    }

    public List<TestResultSnapshot> getHistory() {
        File[] historyFiles = getHistoryFiles();

        List<TestResultSnapshot> resultSnapshots = new ArrayList<TestResultSnapshot>();

        XStream xstream = new XStream();
        for(File historyFile : historyFiles) {
            TestResultSnapshot snapshot = null;
            InputStream inputStream = null;
            try {
                inputStream = new FileInputStream(historyFile);
                snapshot = (TestResultSnapshot) xstream.fromXML(inputStream);
            } catch (FileNotFoundException e) {
                throw new IllegalArgumentException("Unable to read history data in " + historyFile, e);
            } catch (StreamException streamException) {
                throw new IllegalArgumentException("Unable to parse history data in " + historyFile, streamException);
            } finally {
                close(inputStream);
            }
            resultSnapshots.add(snapshot);
        }
        Collections.sort(resultSnapshots);
        return resultSnapshots;

    }

    private File[] getHistoryFiles() {
        return getDirectory().listFiles(new FilenameFilter() {
            public boolean accept(File directory, String filename) {
                return filename.startsWith(historyPrefix());
            }
        });
    }

    private String historyPrefix() {
        return "thucydides-";
    }

    public void clearHistory() {
        File[] historyFiles = getHistoryFiles();
        for(File historyFile : historyFiles) {
            historyFile.delete();
        }
    }

    protected EnvironmentVariables getEnvironmentVariables() {
        return environmentVariables;
    }
}
