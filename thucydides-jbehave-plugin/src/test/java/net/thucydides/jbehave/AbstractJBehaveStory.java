package net.thucydides.jbehave;

import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.reports.xml.XMLTestOutcomeReporter;
import net.thucydides.core.util.MockEnvironmentVariables;
import net.thucydides.core.webdriver.Configuration;
import net.thucydides.core.webdriver.SystemPropertiesConfiguration;
import org.jbehave.core.reporters.StoryReporter;
import org.jbehave.core.reporters.TxtOutput;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.List;

public class AbstractJBehaveStory {
    protected OutputStream output;
    protected StoryReporter printOutput;

    protected MockEnvironmentVariables environmentVariables;
    protected Configuration systemConfiguration;

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    protected File outputDirectory;

    @Before
    public void prepareReporter() {

        environmentVariables = new MockEnvironmentVariables();

        outputDirectory = temporaryFolder.newFolder("output");
        environmentVariables.setProperty("thucydides.outputDirectory", outputDirectory.getAbsolutePath());

        output = new ByteArrayOutputStream();
        printOutput = new TxtOutput(new PrintStream(output));
        systemConfiguration = new SystemPropertiesConfiguration(environmentVariables);
    }


    protected void run(JUnitThucydidesStories stories) {
        try {
            stories.run();
        } catch(Throwable e) {
            e.printStackTrace();
        }
    }

    protected List<TestOutcome> loadTestOutcomes() throws IOException {
        XMLTestOutcomeReporter outcomeReporter = new XMLTestOutcomeReporter();
        return outcomeReporter.loadReportsFrom(outputDirectory);
    }
}
