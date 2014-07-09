package net.thucydides.core.reports.history;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.StreamException;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.reports.TestOutcomes;
import net.thucydides.core.requirements.reports.RequirementsOutcomes;
import net.thucydides.core.util.EnvironmentVariables;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
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
    private final DateProvider dateProvider;

    protected EnvironmentVariables environmentVariables;

    public TestHistory(final String projectName) {
        this(projectName, Injectors.getInjector().getProvider(EnvironmentVariables.class).get() , null);
    }

    public TestHistory(final String projectName, File baseDirectory, DateProvider dateProvider) {
        this(projectName, Injectors.getInjector().getProvider(EnvironmentVariables.class).get() , baseDirectory, dateProvider);
    }

    public TestHistory(final String projectName, final EnvironmentVariables environmentVariables) {
        this(projectName, environmentVariables, null);
    }

    public TestHistory(final String projectName,
                       final EnvironmentVariables environmentVariables,
                       final File baseDirectory) {
        this(projectName, environmentVariables, baseDirectory, new SystemDateProvider());
    }

    private TestHistory(final String projectName,
                       final EnvironmentVariables environmentVariables,
                       final File baseDirectory,
                       final DateProvider dateProvider) {
        this.environmentVariables = environmentVariables;
        this.projectName = projectName;
        dataDirectory = (baseDirectory != null) ? baseDirectory : new File(getBaseDirectoryPath());
        this.dateProvider = dateProvider;
    }

    private String getBaseDirectoryPath() {
        String defaultBaseDirectory = new File(homeDirectory(), ".thucydides").getAbsolutePath();
        return environmentVariables.getProperty(ThucydidesSystemProperty.THUCYDIDES_HISTORY.getPropertyName(),
                                                defaultBaseDirectory);
    }

    private String homeDirectory() {
        return environmentVariables.getProperty("user.home");
    }

    public void updateData(RequirementsOutcomes requirementsOutcome) {
        updateData(requirementsOutcome.getTestOutcomes());
        updateProgressHistory(requirementsOutcome);
    }

    public void updateData(TestOutcomes testOutcomes) {
        int totalStepCount = testOutcomes.getStepCount();
        int passingSteps =  testOutcomes.getPassingTests().getStepCount();
        int failingSteps = testOutcomes.getFailingTests().getStepCount();
        int skippedSteps = totalStepCount - passingSteps - failingSteps;
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

    public void updateProgressHistory(RequirementsOutcomes requirementsOutcome) {
        String requirementType = requirementsOutcome.getType();
        int totalRequirements = requirementsOutcome.getRequirementCount();
        int completedRequirements = requirementsOutcome.getCompletedRequirementsCount();
        int failingRequirements = requirementsOutcome.getFailingRequirementsCount();
        String buildId = getEnvironmentVariables().getValue(BUILD_ID, "MANUAL");

        ProgressSnapshot newSnapshot = ProgressSnapshot.forRequirementType(requirementType)
                                                        .atTime(dateProvider.getCurrentTime())
                                                        .with(completedRequirements).completed()
                                                        .and(failingRequirements).failed()
                                                        .outOf(totalRequirements)
                                                        .forBuild(buildId);

        try {
            save(newSnapshot);
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException("Unable to store history data", e);
        }
    }

    private void save(TestResultSnapshot snapshot) throws FileNotFoundException {
        XStream xstream = new XStream();
        File snapshotFile = new File(getDirectory(), outcomesPrefix() + snapshot.getTime().getMillis());
        OutputStream out = null;
        Writer writer = null;

        try {
            out = new FileOutputStream(snapshotFile);
            writer = new OutputStreamWriter(out, Charset.forName("UTF-8"));
            xstream.toXML(snapshot, writer);
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } finally {
            close(writer);
            close(out);
        }

    }

    private void save(ProgressSnapshot snapshot) throws FileNotFoundException {
        XStream xstream = new XStream();
        File snapshotFile = new File(getDirectory(), progressPrefix() + snapshot.getTime().getMillis());
        OutputStream out = null;
        Writer writer = null;
        try {
            out = new FileOutputStream(snapshotFile);
            writer = new OutputStreamWriter(out, Charset.forName("UTF-8"));
            xstream.toXML(snapshot, writer);
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.

        } finally {
            close(writer);
            close(out);
        }

    }

    public List<TestResultSnapshot> getHistory() {
        File[] historyFiles = getOutcomeFiles();

        List<TestResultSnapshot> resultSnapshots = new ArrayList<TestResultSnapshot>();

        XStream xstream = new XStream();
        for (File historyFile : historyFiles) {
            TestResultSnapshot snapshot = null;
            InputStream inputStream = null;
            Reader reader = null;
            try {
                inputStream = new FileInputStream(historyFile);
                reader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
                snapshot = (TestResultSnapshot) xstream.fromXML(reader);
            } catch (FileNotFoundException e) {
                throw new IllegalArgumentException("Unable to read history data in " + historyFile, e);
            } catch (StreamException streamException) {
                throw new IllegalArgumentException("Unable to parse history data in " + historyFile, streamException);
            } finally {
                close(reader);
                close(inputStream);
            }
            resultSnapshots.add(snapshot);
        }
        Collections.sort(resultSnapshots);
        return resultSnapshots;
    }

    public List<ProgressSnapshot> getProgress() {
        File[] historyFiles = getProgressFiles();

        List<ProgressSnapshot> resultSnapshots = new ArrayList<ProgressSnapshot>();

        XStream xstream = new XStream();
        for (File historyFile : historyFiles) {
            ProgressSnapshot snapshot = null;
            InputStream inputStream = null;
            Reader reader = null;
            try {
                inputStream = new FileInputStream(historyFile);
                reader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
                snapshot = (ProgressSnapshot) xstream.fromXML(reader);

            } catch (FileNotFoundException e) {
                throw new IllegalArgumentException("Unable to read history data in " + historyFile, e);
            } catch (StreamException streamException) {
                throw new IllegalArgumentException("Unable to parse history data in " + historyFile, streamException);
            } finally {
                close(reader);
                close(inputStream);
            }
            resultSnapshots.add(snapshot);
        }
        Collections.sort(resultSnapshots);
        return resultSnapshots;
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
            //noinspection ResultOfMethodCallIgnored
            projectDirectory.mkdirs();
        }
        return projectDirectory;
    }

    private File[] getOutcomeFiles() {
        return getDirectory().listFiles(new FilenameFilter() {
            public boolean accept(File directory, String filename) {
                return filename.startsWith(outcomesPrefix());
            }
        });
    }


    private File[] getProgressFiles() {
        return getDirectory().listFiles(new FilenameFilter() {
            public boolean accept(File directory, String filename) {
                return filename.startsWith(progressPrefix());
            }
        });
    }

    private File[] getHistoryFiles() {
        return getDirectory().listFiles(new FilenameFilter() {
            public boolean accept(File directory, String filename) {
                return filename.startsWith(outcomesPrefix()) || filename.startsWith(progressPrefix());
            }
        });
    }

    private String outcomesPrefix() {
        return "thucydides-outcome-";
    }

    private String progressPrefix() {
        return "thucydides-progress-";
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
