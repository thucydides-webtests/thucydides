package net.thucydides.junit.integration;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.containsString;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import net.thucydides.core.junit.rules.SaveWebdriverSystemPropertiesRule;
import net.thucydides.core.reports.xml.XMLAcceptanceTestReporter;
import net.thucydides.junit.integration.samples.ManagedWebDriverSample;
import net.thucydides.junit.integration.samples.ManagedWebDriverWithAFailingTestSample;
import net.thucydides.junit.runners.Configuration;
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
        File outputDirectory = temporaryFolder.newFolder("target/output");
        setOuputDirectory(runner, outputDirectory);
        
        XMLAcceptanceTestReporter reporter = new XMLAcceptanceTestReporter();
        runner.subscribeReporter(reporter);
        
        runner.run(new RunNotifier());

        File expectedXMLReport = new File(outputDirectory, "managed_web_driver_sample.xml");
        assertThat(expectedXMLReport.exists(), is(true));
    }
    
    @Test
    public void a_failing_test_should_record_the_error_in_the_xml_report() throws Exception {

        ThucydidesRunner runner = new ThucydidesRunner(ManagedWebDriverWithAFailingTestSample.class);
        File outputDirectory = temporaryFolder.newFolder("target/output");
        setOuputDirectory(runner, outputDirectory);

        XMLAcceptanceTestReporter reporter = new XMLAcceptanceTestReporter();
        runner.subscribeReporter(reporter);

        final RunNotifier notifier = new RunNotifier();

        TestExecutionListener testListener = new TestExecutionListener();
        notifier.addListener(testListener);

        runner.run(notifier);
        
        File expectedXMLReport = new File(outputDirectory, "managed_web_driver_with_a_failing_test_sample.xml");
        assertThat(expectedXMLReport.exists(), is(true));
        String xmlContents = FileUtils.readFileToString(expectedXMLReport);
        
        System.out.println("xmlContents = " + xmlContents);
        assertThat(xmlContents, containsString("<error>"));
        assertThat(xmlContents, containsString("<exception>"));
        assertThat(xmlContents, containsString("Expected: is &lt;2&gt;"));
        assertThat(xmlContents, containsString("got: &lt;1&gt;"));
    }

    private void setOuputDirectory(ThucydidesRunner runner, File outputDirectory) {
        Configuration configuration = new Configuration();
        configuration.setOutputDirectory(outputDirectory);
        runner.setConfiguration(configuration);
    }    

    @Test
    public void the_xml_report_should_by_default_go_to_a_maven_compatible_directory() throws Exception {
  
        ThucydidesRunner runner = new ThucydidesRunner(ManagedWebDriverSample.class);
        File outputDirectory = new File("target/thucydides");
        
        XMLAcceptanceTestReporter reporter = new XMLAcceptanceTestReporter();
        runner.subscribeReporter(reporter);
        
        runner.run(new RunNotifier());

        File expectedXMLReport = new File(outputDirectory, "managed_web_driver_sample.xml");
        assertThat(expectedXMLReport.exists(), is(true));
    }

    
    @Test
    public void an_html_report_should_also_be_generated_in_the_output_directory() throws Exception {
  
        ThucydidesRunner runner = new ThucydidesRunner(ManagedWebDriverSample.class);
        File outputDirectory = new File("target/thucydides");
        
        XMLAcceptanceTestReporter reporter = new XMLAcceptanceTestReporter();
        runner.subscribeReporter(reporter);
        
        runner.run(new RunNotifier());

        File expectedXMLReport = new File(outputDirectory, "managed_web_driver_sample.html");
        assertThat(expectedXMLReport.exists(), is(true));
    }

}
