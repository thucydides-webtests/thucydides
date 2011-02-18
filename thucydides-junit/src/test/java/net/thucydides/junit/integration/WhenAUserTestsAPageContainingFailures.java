package net.thucydides.junit.integration;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import net.thucydides.core.model.AcceptanceTestRun;
import net.thucydides.junit.integration.samples.FailingAndPendingTestsSample;
import net.thucydides.junit.runners.ThucydidesRunner;

import org.junit.Test;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.InitializationError;

public class WhenAUserTestsAPageContainingFailures {
    
    @Test
    public void the_test_report_should_count_the_successful_tests()
            throws InitializationError {

        ThucydidesRunner runner = new ThucydidesRunner(FailingAndPendingTestsSample.class);

        runner.run(new RunNotifier());

        AcceptanceTestRun testRun = runner.getFieldReporter().getAcceptanceTestRun();
        
        assertThat(testRun.getSuccessCount(), is(2));
    }
    
    @Test
    public void the_test_report_should_mention_all_tests()
            throws InitializationError {

        ThucydidesRunner runner = new ThucydidesRunner(FailingAndPendingTestsSample.class);

        runner.run(new RunNotifier());

        AcceptanceTestRun testRun = runner.getFieldReporter().getAcceptanceTestRun();
        
        assertThat(testRun.getTestSteps().size(), is(9));
    }
    
    @Test
    public void the_test_report_should_count_failed_tests()
            throws InitializationError {

        ThucydidesRunner runner = new ThucydidesRunner(FailingAndPendingTestsSample.class);

        runner.run(new RunNotifier());

        AcceptanceTestRun testRun = runner.getFieldReporter().getAcceptanceTestRun();
        
        assertThat(testRun.getFailureCount(), is(1));
    }
    
    @Test
    public void the_test_report_should_count_skipped_tests()
            throws InitializationError {

        ThucydidesRunner runner = new ThucydidesRunner(FailingAndPendingTestsSample.class);

        runner.run(new RunNotifier());

        AcceptanceTestRun testRun = runner.getFieldReporter().getAcceptanceTestRun();
        
        assertThat(testRun.getSkippedCount(), is(1));
    }
    
    @Test
    public void the_test_report_should_count_ignored_tests()
            throws InitializationError {

        ThucydidesRunner runner = new ThucydidesRunner(FailingAndPendingTestsSample.class);

        runner.run(new RunNotifier());

        AcceptanceTestRun testRun = runner.getFieldReporter().getAcceptanceTestRun();
        
        assertThat(testRun.getIgnoredCount(), is(2));
    }

    @Test
    public void the_test_report_should_count_pending_tests()
            throws InitializationError {

        ThucydidesRunner runner = new ThucydidesRunner(FailingAndPendingTestsSample.class);

        runner.run(new RunNotifier());

        AcceptanceTestRun testRun = runner.getFieldReporter().getAcceptanceTestRun();
        
        System.out.println(testRun.getTestSteps());
        assertThat(testRun.getPendingCount(), is(3));
    }

}
