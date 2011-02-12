package net.thucydides.junit.runners;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.thucydides.core.model.AcceptanceTestRun;
import net.thucydides.core.reports.AcceptanceTestReporter;
import net.thucydides.core.webdriver.SupportedWebDriver;
import net.thucydides.core.webdriver.WebDriverFactory;
import net.thucydides.junit.annotations.Title;
import net.thucydides.junit.internals.ManagedWebDriverAnnotatedField;

import org.junit.runner.manipulation.Sorter;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import com.google.common.base.Preconditions;

/**
 * A test runner for WebDriver-based web tests. This test runner initializes a
 * WebDriver instance before running the tests in their order of appearance. At
 * the end of the tests, it closes and quits the WebDriver instance.
 * 
 * @depend - <listener> - NarrationListener
 * @depend - <listener> - FailureListener
 * 
 * @author johnsmart
 * 
 */
public class ThucydidesRunner extends BlockJUnit4ClassRunner {

    /**
     * Use this property to define the output directory in which reports will be stored.
     */
    public static final String OUTPUT_DIRECTORY_PROPERTY = "thucydides.outputDirectory";

    private static final String DEFAULT_OUTPUT_DIRECTORY = "target/thucydides";

    /**
     * Creates new browser instances. The Browser Factory's job is to provide
     * new web driver instances. It is designed to isolate the test runner from
     * the business of creating and managing WebDriver drivers.
     */
    private WebDriverFactory webDriverFactory;

    /**
     * A WebDriver instance is shared across all the tests executed by the runner in a given test run.
     */
    private ThreadLocal<WebDriver> webdriver = new ThreadLocal<WebDriver>();

    /**
     * HTML and XML reports will be generated in this directory.
     */
    private File outputDirectory;

    /**
     * Keeps track of whether any tests have failed so far.
     */

    private FailureListener failureListener;

    /**
     * Records screenshots for successful or failing tests.
     */
    private NarrationListener fieldReporter;

    /**
     * Retrieve the runner configuration from an external source.
     */
    private Configuration configuration;
    
    /**
     * The Field Reporter observes and records what happens during the execution of the test.
     * Once the test is over, the Field Reporter can provide the acceptance test outcome in the 
     * form of an AcceptanceTestRun object.
     */
    public NarrationListener getFieldReporter() {
        if (fieldReporter == null) {
            fieldReporter = new NarrationListener((TakesScreenshot) getDriver(), getOutputDirectory());
        }
        return fieldReporter;
    }

    /**
     * Inject a custom field reporter into the runner. You shouldn't normally
     * need to do this - the runner will use the default implementation
     * otherwise. But useful for testing or extending the framework.
     */
    public void setFieldReporter(final NarrationListener fieldReporter) {
        Preconditions.checkArgument(this.fieldReporter == null,
                "The field reporter object can only be assigned once.");
        this.fieldReporter = fieldReporter;
    }

    /**
     * Who needs to be notified when a test is done.
     */
    private List<AcceptanceTestReporter> subscribedReporters = new ArrayList<AcceptanceTestReporter>();

    /**
     * Takes cares of screenshots. The member variable makes for more convenient
     * testing. TODO: Is the photographer too tightly bound with the runner
     * class?
     */
//    private Photographer photographer;

    /**
     * Creates a new test runner for WebDriver web tests.
     * 
     * @throws InitializationError
     *             if some JUnit-related initialization problem occurred
     * @throws UnsupportedDriverException
     *             if the requested driver type is not supported
     */
    public ThucydidesRunner(final Class<?> klass) throws InitializationError {
        super(klass);
        checkRequestedDriverType();
        checkThatManagedFieldIsDefinedIn(klass);
        webDriverFactory = new WebDriverFactory();
    }

    public void setOutputDirectory(final File outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    /**
     * The output directory is where the test runner writes the XML and HTML
     * reports to. By default, it will be in 'target/thucydides', but you can
     * override this value either programmatically or by providing a value in
     * the <b>thucydides.output.dir</b> system property.
     * 
     */
    public File getOutputDirectory() {
        if (outputDirectory == null) {
            outputDirectory = deriveOutputDirectoryFromSystemProperties();
            outputDirectory.mkdirs();
        }
        return outputDirectory;
    }

    private Configuration getConfiguration() {
        if (configuration == null) {
            configuration = new Configuration();
        }
        return configuration;
    }

    private File deriveOutputDirectoryFromSystemProperties() {
        String systemDefinedDirectory = System
                .getProperty(OUTPUT_DIRECTORY_PROPERTY);
        if (systemDefinedDirectory == null) {
            systemDefinedDirectory = DEFAULT_OUTPUT_DIRECTORY;
        }
        return new File(systemDefinedDirectory);
    }

    private void checkThatManagedFieldIsDefinedIn(final  Class<?> testCase) {
        ManagedWebDriverAnnotatedField.findFirstAnnotatedField(testCase);
    }

    /**
     * Ensure that the requested driver type is valid before we start the tests.
     * Otherwise, throw an InitializationError.
     */
    private void checkRequestedDriverType() {
        getConfiguration().findDriverType();
    }

    /**
     * Override the default web driver factory. Normal users shouldn't need to
     * do this very often.
     */
    public void setWebDriverFactory(final WebDriverFactory webDriverFactory) {
        this.webDriverFactory = webDriverFactory;
    }

    @Override
    public void run(final RunNotifier notifier) {
        initializeDriver();

        failureListener = new FailureListener();

        notifier.addListener(failureListener);
        notifier.addListener(getFieldReporter());
                
        super.run(notifier);

        closeDriver();

        generateReports(fieldReporter.getAcceptanceTestRun());
    }

    /**
     * A test runner can generate reports via Reporter instances that subscribe
     * to the test runner. The test runner tells the reporter what directory to
     * place the reports in. Then, at the end of the test, the test runner
     * notifies these reporters of the test outcomes. The reporter's job is to
     * process each test run outcome and do whatever is appropriate.
     * 
     * @throws IllegalArgumentException
     * @throws IOException
     * 
     */
    private void generateReports(final AcceptanceTestRun acceptanceTestRun) {
        for (AcceptanceTestReporter reporter : getSubscribedReporters()) {
            try {
                reporter.generateReportFor(acceptanceTestRun);
            } catch (IOException e) {
                throw new IllegalArgumentException(
                        "Failed to generate reports using " + reporter, e);
            }
        }
    }

    private List<AcceptanceTestReporter> getSubscribedReporters() {
        return subscribedReporters;
    }

    /**
     * To generate reports, different AcceptanceTestReporter instances need to
     * subscribe to the listener. The listener will tell them when the test is
     * done, and the reporter can decide what to do.
     */
    public void subscribeReported(final AcceptanceTestReporter reporter) {
        reporter.setOutputDirectory(getOutputDirectory());
        subscribedReporters.add(reporter);
    }

    @Override
    public void sort(final Sorter sorter) {
        super.sort(sorter);
    }

    @Override
    protected void runChild(final FrameworkMethod method, final RunNotifier notifier) {
        if (failureListener.aPreviousTestHasFailed()) {
            notifier.fireTestIgnored(describeChild(method));
        } else {
            super.runChild(method, notifier);
        }
    }

    @Override
    protected Statement methodInvoker(final FrameworkMethod method, final Object test) {
        injectDriverInto(test);
        setTestRunTitleIfAnnotationFoundOn(test);
        return super.methodInvoker(method, test);
    }

    private void setTestRunTitleIfAnnotationFoundOn(final Object test) {
        Title titleAnnotation = test.getClass().getAnnotation(Title.class);
        if (titleAnnotation != null) {
            String title = titleAnnotation.value();
            getFieldReporter().setTestRunTitle(title);
        }
    }

    /**
     * Instanciate the @Managed-annotated WebDriver instance with current WebDriver.
     */
    protected void injectDriverInto(final Object testCase) {
        ManagedWebDriverAnnotatedField webDriverField = ManagedWebDriverAnnotatedField
                .findFirstAnnotatedField(testCase.getClass());

        webDriverField.setValue(testCase, getDriver());
    }

    /**
     * A new WebDriver is created before any of the tests are run. The driver is
     * determined by the 'webdriver.driver' system property.
     * 
     * @throws UnsupportedDriverException
     */
    private void initializeDriver() {
        webdriver.set(newDriver());
    }

    /**
     * Create a new driver instance based on system property values. You can
     * override this method to use a custom driver if you really know what you
     * are doing.
     * 
     * @throws UnsupportedDriverException
     *             if the driver type is not supported.
     */
    protected WebDriver newDriver() {
        SupportedWebDriver supportedDriverType = getConfiguration().findDriverType();
        return webDriverFactory.newInstanceOf(supportedDriverType);
    }

    protected WebDriver getDriver() {
        return webdriver.get();
    }

    /**
     * We shut down the Webdriver at the end of the tests. This should shut down
     * the browser as well.
     */
    private void closeDriver() {
        if ((webdriver != null) && (webdriver.get() != null)) {
            webdriver.get().quit();
        }
    }
}
