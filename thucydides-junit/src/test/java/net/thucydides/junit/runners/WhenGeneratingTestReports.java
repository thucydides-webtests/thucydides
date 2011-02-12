package net.thucydides.junit.runners;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;

import net.thucydides.core.model.AcceptanceTestRun;
import net.thucydides.core.reports.AcceptanceTestReporter;
import net.thucydides.junit.integration.samples.OpenGoogleHomePageSample;
import net.thucydides.junit.integration.samples.OpenGoogleHomePageWithTitleSample;
import net.thucydides.junit.runners.mocks.TestableWebDriverFactory;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.InitializationError;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openqa.selenium.TakesScreenshot;

/**
 * Managing the WebDriver instance during a test run The instance should be
 * created once at the start of the test run, and closed once at the end of the
 * tets.
 * 
 * @author johnsmart
 * 
 */
public class WhenGeneratingTestReports extends AbstractWebDriverTest {

    @Mock
    AcceptanceTestReporter mockReporter;
    
    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }
    
    @Test
    public void a_test_reporter_can_subscribe_to_the_runner()
            throws InitializationError, IOException {
        TestableWebDriverFactory mockBrowserFactory = new TestableWebDriverFactory();
        ThucydidesRunner runner = getTestRunnerUsing(mockBrowserFactory);
        runner.subscribeReported(mockReporter);

        runner.run(new RunNotifier());

        verify(mockReporter).generateReportFor(any(AcceptanceTestRun.class));
    }

    @Test
    public void the_runner_should_assign_the_test_case_title_based_on_the_class_name_by_default()
            throws InitializationError, IOException {
        TestableWebDriverFactory mockBrowserFactory = new TestableWebDriverFactory();
        
        NarrationListener fieldReporter = createMockNarrationListener();
        
        ThucydidesRunner runner = getTestRunnerUsing(OpenGoogleHomePageSample.class, mockBrowserFactory);
        runner.setFieldReporter(fieldReporter);
        
        runner.subscribeReported(mockReporter);
        runner.run(new RunNotifier());
        
        assertThat(fieldReporter.getAcceptanceTestRun().getTitle(), is("Open google home page sample"));

    }

    private NarrationListener createMockNarrationListener() {
        TakesScreenshot driver = mock(TakesScreenshot.class);
        File outputDirectory = mock(File.class);
        NarrationListener fieldReporter = new NarrationListener(driver, outputDirectory);
        return fieldReporter;
    }
    
    @Test
    public void the_developer_can_overrire_the_test_case_title_using_the_Title_annotation()
            throws InitializationError, IOException {
        TestableWebDriverFactory mockBrowserFactory = new TestableWebDriverFactory();
        
        NarrationListener fieldReporter = createMockNarrationListener();
        
        ThucydidesRunner runner = getTestRunnerUsing(OpenGoogleHomePageWithTitleSample.class, mockBrowserFactory);
        runner.setFieldReporter(fieldReporter);
        
        runner.subscribeReported(mockReporter);
        runner.run(new RunNotifier());
        
        assertThat(fieldReporter.getAcceptanceTestRun().getTitle(), is("Open the Google home page"));

    }

    @Test
    public void the_runer_should_tell_the_reporter_what_directory_to_use()
            throws InitializationError, IOException {
        TestableWebDriverFactory mockBrowserFactory = new TestableWebDriverFactory();
        ThucydidesRunner runner = getTestRunnerUsing(mockBrowserFactory);
        runner.subscribeReported(mockReporter);

        runner.run(new RunNotifier());

        verify(mockReporter).setOutputDirectory(any(File.class));
    }

    @Test
    public void multiple_test_reporters_can_subscribe_to_the_runner()
            throws InitializationError, IOException {
        TestableWebDriverFactory mockBrowserFactory = new TestableWebDriverFactory();
        ThucydidesRunner runner = getTestRunnerUsing(mockBrowserFactory);
        
        AcceptanceTestReporter reporter1 = mock(AcceptanceTestReporter.class);
        AcceptanceTestReporter reporter2 = mock(AcceptanceTestReporter.class);

        runner.subscribeReported(reporter1);
        runner.subscribeReported(reporter2);

        runner.run(new RunNotifier());

        verify(reporter1).generateReportFor(any(AcceptanceTestRun.class));
        verify(reporter2).generateReportFor(any(AcceptanceTestRun.class));
    }
        
    @Test(expected=IllegalArgumentException.class)
    public void the_test_should_fail_with_an_error_if_the_reporter_breaks()
            throws InitializationError, IOException {
        TestableWebDriverFactory mockBrowserFactory = new TestableWebDriverFactory();
        ThucydidesRunner runner = getTestRunnerUsing(mockBrowserFactory);

        when(mockReporter.generateReportFor(any(AcceptanceTestRun.class))).thenThrow(new IOException());
        
        runner.subscribeReported(mockReporter);
        runner.run(new RunNotifier());
    }
    
    
}
