package net.thucydides.junit.runners;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;

import net.thucydides.core.model.AcceptanceTestRun;
import net.thucydides.core.reports.AcceptanceTestReporter;
import net.thucydides.core.reports.ReportGenerationFailedError;
import net.thucydides.junit.runners.mocks.TestableWebDriverFactory;
import net.thucydides.samples.AnnotatedSingleTestScenario;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.InitializationError;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Managing the WebDriver instance during a test run The instance should be
 * created once at the start of the test run, and closed once at the end of the
 * tets.
 * 
 * @author johnsmart
 * 
 */
public class WhenGeneratingTestReports extends AbstractTestStepRunnerTest {

    @Mock
    AcceptanceTestReporter mockReporter;
    
    TestableWebDriverFactory mockBrowserFactory;
    
    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
        mockBrowserFactory = new TestableWebDriverFactory();
    }
    
    @Test
    public void a_test_reporter_can_subscribe_to_the_runner() throws InitializationError, IOException {
        
        ThucydidesRunner runner = getTestRunnerUsing(AnnotatedSingleTestScenario.class, mockBrowserFactory);
        runner.subscribeReporter(mockReporter);

        runner.run(new RunNotifier());

        verify(mockReporter).generateReportFor(any(AcceptanceTestRun.class));
    }


    @Test
    public void the_runer_should_tell_the_reporter_what_directory_to_use()
            throws InitializationError, IOException {
        
        ThucydidesRunner runner = getTestRunnerUsing(AnnotatedSingleTestScenario.class, mockBrowserFactory);
        runner.subscribeReporter(mockReporter);

        runner.run(new RunNotifier());;

        verify(mockReporter,atLeast(1)).setOutputDirectory(any(File.class));
    }
    
    @Test
    public void multiple_test_reporters_can_subscribe_to_the_runner()
            throws InitializationError, IOException {

        ThucydidesRunner runner = getTestRunnerUsing(AnnotatedSingleTestScenario.class, mockBrowserFactory);
        
        AcceptanceTestReporter reporter1 = mock(AcceptanceTestReporter.class);
        AcceptanceTestReporter reporter2 = mock(AcceptanceTestReporter.class);

        runner.subscribeReporter(reporter1);
        runner.subscribeReporter(reporter2);

        runner.run(new RunNotifier());

        verify(reporter1).generateReportFor(any(AcceptanceTestRun.class));
        verify(reporter2).generateReportFor(any(AcceptanceTestRun.class));
    } 
    
    @Test(expected=ReportGenerationFailedError.class)
    public void the_test_should_fail_with_an_error_if_the_reporter_breaks()
            throws InitializationError, IOException {

        ThucydidesRunner runner = getTestRunnerUsing(AnnotatedSingleTestScenario.class, mockBrowserFactory);

        when(mockReporter.generateReportFor(any(AcceptanceTestRun.class))).thenThrow(new IOException());
        
        runner.subscribeReporter(mockReporter);
        runner.run(new RunNotifier());
    }
}
