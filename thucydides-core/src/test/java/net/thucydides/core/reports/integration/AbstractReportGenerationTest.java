package net.thucydides.core.reports.integration;

import net.thucydides.core.annotations.Feature;
import net.thucydides.core.annotations.Story;
import net.thucydides.core.junit.rules.SaveWebdriverSystemPropertiesRule;
import net.thucydides.core.model.ConcreteTestStep;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.reports.AcceptanceTestReporter;
import net.thucydides.core.reports.html.HtmlAcceptanceTestReporter;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.MethodRule;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class AbstractReportGenerationTest {

    @Rule
    public TemporaryFolder temporaryDirectory = new TemporaryFolder();

    @Rule
    public MethodRule saveSystemProperties = new SaveWebdriverSystemPropertiesRule();

    protected AcceptanceTestReporter reporter;

    protected File outputDirectory;

    public AbstractReportGenerationTest() {
    }

    @Before
    public void setupTestReporter() {
        reporter = new HtmlAcceptanceTestReporter();
        outputDirectory = temporaryDirectory.newFolder("target/thucydides");
        reporter.setOutputDirectory(outputDirectory);
    }

    protected void recordStepWithScreenshot(TestOutcome testOutcome, String stepName, String screenshot) throws IOException {
        String screenshotResource = "/screenshots/" + screenshot;
        URL sourcePath = this.getClass().getResource(screenshotResource);
        File sourceFile = new File(sourcePath.getPath());
        FileUtils.copyFileToDirectory(sourceFile, outputDirectory);

        ConcreteTestStep step = TestStepFactory.successfulTestStepCalled(stepName);
        step.setScreenshot(new File(outputDirectory, screenshot));
        testOutcome.recordStep(step);
    }

    protected class AUserStory {
    }

    @Story(WhenGeneratingAnHtmlReport.AUserStory.class)
    protected class SomeTestScenario {
        public void a_simple_test_case() {
        }

        ;

        public void should_do_this() {
        }

        ;

        public void should_do_that() {
        }

        ;
    }

    @Feature
    protected class AFeature {
        class AUserStoryInAFeature {
        }

        ;
    }

    @Story(WhenGeneratingAnHtmlReport.AFeature.AUserStoryInAFeature.class)
    protected class SomeTestScenarioInAFeature {
        public void should_do_this() {
        }

        ;

        public void should_do_that() {
        }

        ;
    }
}