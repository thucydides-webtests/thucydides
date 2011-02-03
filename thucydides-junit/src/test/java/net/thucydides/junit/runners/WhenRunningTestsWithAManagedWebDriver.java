package net.thucydides.junit.runners;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.thucydides.junit.runners.listeners.TestExecutionListener;
import net.thucydides.junit.runners.mocks.TestableWebDriverFactory;

import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.notification.RunListener;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.InitializationError;

/**
 * Managing the WebDriver instance during a test run
 * The instance should be created once at the start of the test run,
 * and closed once at the end of the tets.
 * 
 * @author johnsmart
 * 
 */
public class WhenRunningTestsWithAManagedWebDriver extends AbstractWebDriverTest {

    @Test
    public void the_tests_should_be_executed_in_the_order_of_appearance() throws InitializationError  {
        TestableWebDriverFactory mockBrowserFactory = new TestableWebDriverFactory();
        ThucydidesRunner runner = getTestRunnerUsing(mockBrowserFactory);
                
        final RunNotifier notifier = new RunNotifier();

        final List<String> expectedTestOrder 
            = Arrays.asList("should_do_this_step_1","should_do_that_step_2","then_gets_here_step_3", "finally_gets_here_step_4", "and_at_the_end_step_5");
        
        TestExecutionListener testListener = new TestExecutionListener();
        notifier.addListener(testListener);

       runner.run(notifier);
       
       assertThat(testListener.getExecutedTests(), is(expectedTestOrder));
    }  
    
}
