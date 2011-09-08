package net.thucydides.core.reports.history;

import com.thoughtworks.xstream.XStream;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.model.FeatureResults;
import net.thucydides.core.reports.html.history.TestResultSnapshot;
import net.thucydides.core.util.EnvironmentVariables;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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
        dataDirectory = new File(getBaseDirectoryPath());
        environmentVariables = Injectors.getInjector().getInstance(EnvironmentVariables.class);
        this.projectName = projectName;
    }


    private String getBaseDirectoryPath() {
        String defaultBaseDirectory = new File(homeDirectory(), ".thucydides").getAbsolutePath();
        return ThucydidesSystemProperty.getValue(ThucydidesSystemProperty.HISTORY_BASE_DIRECTORY, defaultBaseDirectory);
    }

    private String homeDirectory() {
        return System.getProperty("user.home");
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
        OutputStream out = new FileOutputStream(snapshotFile);
        xstream.toXML(snapshot, out);

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
                throw new IllegalArgumentException("Unable to read history data", e);
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            resultSnapshots.add(snapshot);
        }
        Collections.sort(resultSnapshots);
        return resultSnapshots;

    }

    private File[] getHistoryFiles() {
        return getDirectory().listFiles();
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

    protected void setEnvironmentVariables(final EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;
    }
}
