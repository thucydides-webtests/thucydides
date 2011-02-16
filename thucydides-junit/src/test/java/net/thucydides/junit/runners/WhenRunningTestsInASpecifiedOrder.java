package net.thucydides.junit.runners;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.util.Arrays;
import java.util.List;

import net.thucydides.junit.runners.listeners.TestExecutionListener;
import net.thucydides.junit.runners.mocks.TestableWebDriverFactory;
import net.thucydides.junit.runners.samples.TestsOrderedByStepAnnotationSample;
import net.thucydides.junit.runners.samples.TestsOrderedInAlphabeticalOrderSample;

import org.junit.Test;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.InitializationError;

/**
 * Managing the WebDriver instance during a test run The instance should be
 * created once at the start of the test run, and closed once at the end of the
 * tets.
 * 
 * @author johnsmart
 * 
 */
public class WhenRunningTestsInASpecifiedOrder extends AbstractWebDriverTest {

    @Test
    public void the_tests_should_be_executed_according_to_the_Step_annotations()
            throws InitializationError {
        TestableWebDriverFactory mockBrowserFactory = new TestableWebDriverFactory();
        ThucydidesRunner runner = getTestRunnerUsing(TestsOrderedByStepAnnotationSample.class, mockBrowserFactory);
        NarrationListener fieldReporter = createMockNarrationListener();        
        runner.setFieldReporter(fieldReporter);

        final RunNotifier notifier = new RunNotifier();

        final List<String> expectedTestOrder = Arrays.asList("shoud_do_this_step_first",
                "should_do_this_step_second", "should_do_this_step_third", "should_do_this_step_forth");

        TestExecutionListener testListener = new TestExecutionListener();
        notifier.addListener(testListener);
        runner.run(notifier);
        assertThat(testListener.getExecutedTests(), is(expectedTestOrder));
    }
    
    @Test
    public void the_tests_should_be_executed_in_alphabetical_order_if_no_steps_are_defined()
            throws InitializationError {
        TestableWebDriverFactory mockBrowserFactory = new TestableWebDriverFactory();
        ThucydidesRunner runner = getTestRunnerUsing(TestsOrderedInAlphabeticalOrderSample.class, mockBrowserFactory);
        NarrationListener fieldReporter = createMockNarrationListener();        
        runner.setFieldReporter(fieldReporter);

        final RunNotifier notifier = new RunNotifier();

        final List<String> expectedTestOrder = Arrays.asList("step_1_shoud_do_this_step_first",
                "step_2_should_do_this_step_second", "step_3_should_do_this_step_third", "step_4_should_do_this_step_forth");

        TestExecutionListener testListener = new TestExecutionListener();
        notifier.addListener(testListener);
        runner.run(notifier);
        assertThat(testListener.getExecutedTests(), is(expectedTestOrder));
    }
    
}
