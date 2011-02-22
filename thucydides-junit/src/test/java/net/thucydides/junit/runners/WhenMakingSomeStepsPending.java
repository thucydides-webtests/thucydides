package net.thucydides.junit.runners;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.util.Arrays;
import java.util.List;

import net.thucydides.junit.runners.listeners.TestExecutionListener;
import net.thucydides.junit.runners.mocks.TestableWebDriverFactory;
import net.thucydides.junit.samples.TestUsingPendingAnnotationSample;

import org.junit.Test;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.InitializationError;

public class WhenMakingSomeStepsPending extends AbstractWebDriverTest{
    @Test
    public void pending_tests_should_be_ignored()
            throws InitializationError {
        TestableWebDriverFactory mockBrowserFactory = new TestableWebDriverFactory();
        ThucydidesRunner runner = getTestRunnerUsing(TestUsingPendingAnnotationSample.class, mockBrowserFactory);
        NarrationListener fieldReporter = createMockNarrationListener();        
        runner.setFieldReporter(fieldReporter);

        final RunNotifier notifier = new RunNotifier();

        final List<String> expectedTestOrder = Arrays.asList("should_do_this_step_1",
                "should_do_that_step_2");

        final List<String> expectedIgnoredTests = Arrays.asList("skip_this_pending_step",
                                                                "this_step_is_pending_too");

        TestExecutionListener testListener = new TestExecutionListener();
        notifier.addListener(testListener);
        
        runner.run(notifier);
        
        assertThat(testListener.getExecutedTests(), is(expectedTestOrder));
        assertThat(testListener.getIgnoredTests(), is(expectedIgnoredTests));
    }

}
