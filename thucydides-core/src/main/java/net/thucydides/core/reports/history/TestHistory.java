package net.thucydides.core.reports.history;

import com.thoughtworks.xstream.XStream;
import net.thucydides.core.model.FeatureResults;
import net.thucydides.core.reports.html.history.TestResultSnapshot;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.sum;

/**
 * Keep track of the test results over time.
 */
public class TestHistory {

    private final File dataDirectory;
    private final String projectName;

    public TestHistory(final String projectName) {
        dataDirectory = new File(homeDirectory(), ".thucydides");
        this.projectName = projectName;
    }

    private String homeDirectory() {
        return System.getProperty("user.home");
    }

    public void updateData(List<FeatureResults> featureResults) {
        int totalStepCount = countTotalStepsIn(featureResults);
        int passingSteps =  countPassingStepsIn(featureResults);
        int failingSteps = countFailingStepsIn(featureResults);
        int skippedSteps = countSkippedStepsIn(featureResults);

        TestResultSnapshot newSnapshot = new TestResultSnapshot(totalStepCount,
                                                                passingSteps,
                                                                failingSteps,
                                                                skippedSteps);

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
        if (!dataDirectory.exists()) {
            dataDirectory.mkdirs();
        }
        return dataDirectory;
    }

    public List<TestResultSnapshot> getHistory() {
        File[] historyFiles = getHistoryFiles();

        List<TestResultSnapshot> resultSnapshots = new ArrayList<TestResultSnapshot>();

        XStream xstream = new XStream();
        for(File historyFile : historyFiles) {
            TestResultSnapshot snapshot = null;
            try {
                snapshot = (TestResultSnapshot) xstream.fromXML(new FileInputStream(historyFile));
            } catch (FileNotFoundException e) {
                throw new IllegalArgumentException("Unable to read history data", e);
            }
            resultSnapshots.add(snapshot);
        }
        return resultSnapshots;

    }

    private File[] getHistoryFiles() {
        return getDirectory().listFiles(new FileFilter() {
                public boolean accept(File file) {
                    return file.getName().startsWith(historyPrefix());
                }
            });
    }

    private String historyPrefix() {
        return "thucydides-" + projectName;
    }

    public void clearHistory() {
        File[] historyFiles = getHistoryFiles();
        for(File historyFile : historyFiles) {
            historyFile.delete();
        }
    }
}
