package net.thucydides.junit.integration;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import net.thucydides.core.reports.xml.XMLAcceptanceTestReporter;
import net.thucydides.junit.integration.samples.ManagedWebDriverSample;
import net.thucydides.junit.integration.samples.ManagedWebDriverWithAFailingTestSample;
import net.thucydides.junit.rules.SaveWebdriverSystemPropertiesRule;
import net.thucydides.junit.runners.ThucydidesRunner;
import net.thucydides.junit.runners.listeners.TestExecutionListener;

import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;
import org.junit.rules.TemporaryFolder;
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

        ThucydidesRunner runner = new ThucydidesRunner(ManagedWebDriverWithAFailingTestSample.class);

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

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();
    
    @Test
    public void the_test_case_should_generate_an_xml_report() throws Exception {
  
        ThucydidesRunner runner = new ThucydidesRunner(ManagedWebDriverSample.class);
        File outputDirectory = temporaryFolder.newFolder("output");
        runner.setOutputDirectory(outputDirectory);
        
        XMLAcceptanceTestReporter reporter = new XMLAcceptanceTestReporter();
        runner.subscribeReported(reporter);
        
        runner.run(new RunNotifier());

        File expectedXMLReport = new File(outputDirectory, "managed_web_driver_sample.xml");
        assertThat(expectedXMLReport.exists(), is(true));
    }
    
    @Test
    public void the_xml_report_should_by_default_go_to_a_maven_compatible_directory() throws Exception {
  
        ThucydidesRunner runner = new ThucydidesRunner(ManagedWebDriverSample.class);
        File outputDirectory = new File("target/thucydides");
        
        XMLAcceptanceTestReporter reporter = new XMLAcceptanceTestReporter();
        runner.subscribeReported(reporter);
        
        runner.run(new RunNotifier());

        File expectedXMLReport = new File(outputDirectory, "managed_web_driver_sample.xml");
        assertThat(expectedXMLReport.exists(), is(true));
    }

}
