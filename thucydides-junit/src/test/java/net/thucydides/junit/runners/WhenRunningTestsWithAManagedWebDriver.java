package net.thucydides.junit.runners;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.startsWith;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import net.thucydides.core.screenshots.Photographer;
import net.thucydides.junit.runners.listeners.TestExecutionListener;
import net.thucydides.junit.runners.mocks.TestableWebDriverFactory;
import net.thucydides.junit.samples.ManagedWebDriverWithFailingTestSample;

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
public class WhenRunningTestsWithAManagedWebDriver extends AbstractWebDriverTest {

    @Test
    public void should_take_a_screenshot_after_each_test() throws InitializationError, IOException {

        TestableWebDriverFactory mockBrowserFactory = new TestableWebDriverFactory();
        ThucydidesRunner runner = getTestRunnerUsing(mockBrowserFactory);
        NarrationListener fieldReporter = createMockNarrationListener();        
        runner.setFieldReporter(fieldReporter);
        
        runner.run(new RunNotifier());

        final Photographer mockPhotographer = fieldReporter.getPhotographer();

        verify(mockPhotographer).takeScreenshot(startsWith("should_do_this_step_1"));
        verify(mockPhotographer).takeScreenshot(startsWith("should_do_that_step_2"));
        verify(mockPhotographer).takeScreenshot(startsWith("then_gets_here_step_3"));
        verify(mockPhotographer).takeScreenshot(startsWith("finally_gets_here_step_4"));
        verify(mockPhotographer).takeScreenshot(startsWith("and_at_the_end_step_5"));
    }

    @Test
    public void should_not_take_a_screenshot_for_skipped_tests() throws InitializationError,
            IOException {

        TestableWebDriverFactory mockBrowserFactory = new TestableWebDriverFactory();
        ThucydidesRunner runner = getTestRunnerUsing(ManagedWebDriverWithFailingTestSample.class,
                mockBrowserFactory);

        NarrationListener fieldReporter = createMockNarrationListener();        
        runner.setFieldReporter(fieldReporter);
        
        runner.run(new RunNotifier());

        final Photographer mockPhotographer = fieldReporter.getPhotographer();

        verify(mockPhotographer).takeScreenshot(startsWith("should_do_this_step_1"));
        verify(mockPhotographer).takeScreenshot(startsWith("should_do_that_step_2"));
        verify(mockPhotographer).takeScreenshot(startsWith("but_fail_here_in_step_3"));
        verify(mockPhotographer, never()).takeScreenshot(startsWith("dont_get_to_here"));
        verify(mockPhotographer, never()).takeScreenshot(startsWith("or_to_here"));
    }

    @Test
    public void should_skip_all_tests_following_a_test_failure() throws InitializationError,
            IOException {

        TestableWebDriverFactory mockBrowserFactory = new TestableWebDriverFactory();
        ThucydidesRunner runner = getTestRunnerUsing(ManagedWebDriverWithFailingTestSample.class,
                mockBrowserFactory);

        NarrationListener fieldReporter = createMockNarrationListener();        
        runner.setFieldReporter(fieldReporter);

        final RunNotifier notifier = new RunNotifier();

        final List<String> expectedTestOrder = Arrays.asList("should_do_this_step_1",
                                                             "should_do_that_step_2", 
                                                             "but_fail_here_in_step_3");

        TestExecutionListener testListener = new TestExecutionListener();
        notifier.addListener(testListener);

        runner.run(notifier);

        assertThat(testListener.getExecutedTests(), is(expectedTestOrder));
        assertThat(testListener.getFailedTests(), hasItem("but_fail_here_in_step_3"));
        assertThat(testListener.getIgnoredTests(), hasItems("dont_get_to_here","or_to_here"));
    }
    
}
