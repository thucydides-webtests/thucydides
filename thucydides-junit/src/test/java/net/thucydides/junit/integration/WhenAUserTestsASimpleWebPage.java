package net.thucydides.junit.integration;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import java.util.Arrays;
import java.util.List;

import net.thucydides.junit.integration.samples.ManagedWebDriverSample;
import net.thucydides.junit.integration.samples.ManagedWebDriverSampleWithAFailingTest;
import net.thucydides.junit.rules.SaveWebdriverSystemPropertiesRule;
import net.thucydides.junit.runners.ThucydidesRunner;
import net.thucydides.junit.runners.listeners.TestExecutionListener;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.InitializationError;

public class WhenAUserTestsASimpleWebPage {
    
    @Rule
    public MethodRule saveSystemProperties = new SaveWebdriverSystemPropertiesRule();
    
    @Test
    public void the_tests_should_be_executed_in_the_right_order()
            throws InitializationError {

        ThucydidesRunner runner = new ThucydidesRunner(ManagedWebDriverSample.class);

        final RunNotifier notifier = new RunNotifier();

        final List<String> expectedTestOrder = Arrays.asList(
                "the_user_opens_the_page",
                "the_user_performs_a_search_on_cats",
                "the_results_page_title_should_contain_the_word_Cats");

        TestExecutionListener testListener = new TestExecutionListener();
        notifier.addListener(testListener);

        runner.run(notifier);

        assertThat(testListener.getExecutedTests(), is(expectedTestOrder));
    }

    @Test
    public void the_test_case_should_ignore_following_tests_if_a_test_fails()
    throws InitializationError {

        ThucydidesRunner runner = new ThucydidesRunner(ManagedWebDriverSampleWithAFailingTest.class);

        final RunNotifier notifier = new RunNotifier();

        final List<String> expectedTestOrder = Arrays.asList(
                "the_user_opens_the_page",
                "the_user_performs_a_search_on_cats");

        TestExecutionListener testListener = new TestExecutionListener();
        notifier.addListener(testListener);

        runner.run(notifier);

        assertThat(testListener.getExecutedTests(), is(expectedTestOrder));
        assertThat(testListener.getFailedTests(), hasItem("the_user_performs_a_search_on_cats"));
        assertThat(testListener.getIgnoredTests(), hasItem("the_results_page_title_should_contain_the_word_Cats"));
    }

}
