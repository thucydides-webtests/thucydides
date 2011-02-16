package net.thucydides.junit.runners;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.thucydides.core.model.AcceptanceTestRun;
import net.thucydides.core.reports.AcceptanceTestReporter;
import net.thucydides.core.webdriver.WebDriverFactory;
import net.thucydides.junit.annotations.Pending;
import net.thucydides.junit.annotations.Step;
import net.thucydides.junit.annotations.Title;
import net.thucydides.junit.internals.ManagedWebDriverAnnotatedField;

import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

/**
 * A test runner for WebDriver-based web tests. This test runner initializes a
 * WebDriver instance before running the tests in their order of appearance. At
 * the end of the tests, it closes and quits the WebDriver instance.
 * 
 * The test runner will by default produce output in XML and HTML. This
 * can extended by subscribing more reporter implementations to the test runner.
 * TODO: I'm not sure about how to subscribe more report modules - probably via Maven or Ant.
 * 
 * @depend - <listener> - NarrationListener
 * @depend - <listener> - FailureListener
 * 
 * @author johnsmart
 * 
 */
public class ThucydidesRunner extends BlockJUnit4ClassRunner {

    /**
     * Creates new browser instances. The Browser Factory's job is to provide
     * new web driver instances. It is designed to isolate the test runner from
     * the business of creating and managing WebDriver drivers.
     */
    private WebDriverFactory webDriverFactory;

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

    
    private WebdriverManager webdriverManager;
    
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
            outputDirectory = getConfiguration().getOutputDirectory();
            outputDirectory.mkdirs();
        }
        return outputDirectory;
    }

    /**
     * The configuration manages output directories and driver types.
     * They can be defined as system values, or have sensible defaults.
     */
    private Configuration getConfiguration() {
        if (configuration == null) {
            configuration = new Configuration();
        }
        return configuration;
    }

    private void checkThatManagedFieldIsDefinedIn(final  Class<?> testCase) {
        ManagedWebDriverAnnotatedField.findFirstAnnotatedField(testCase);
    }

    /**
     * Ensure that the requested driver type is valid before we start the tests.
     * Otherwise, throw an InitializationError.
     */
    private void checkRequestedDriverType() {
        getConfiguration().getDriverType();
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
        webdriverManager = new WebdriverManager(webDriverFactory, getConfiguration());
        failureListener = new FailureListener();

        setupDefaultReporters();
        
        notifier.addListener(failureListener);
        notifier.addListener(getFieldReporter());

        System.out.println("START TESTS");
        super.run(notifier);
        System.out.println("END TESTS");

        generateReportsFor(getFieldReporter().getAcceptanceTestRun());
        
        webdriverManager.closeDriver();
    }
    
    private void setupDefaultReporters() {
        subscribedReporters.addAll(getConfiguration().getDefaultReporters());
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
    private void generateReportsFor(final AcceptanceTestRun acceptanceTestRun) {
        for (AcceptanceTestReporter reporter : getSubscribedReporters()) {
            try {
                reporter.setOutputDirectory(getOutputDirectory());
                reporter.generateReportFor(acceptanceTestRun);
            } catch (IOException e) {
                throw new IllegalArgumentException(
                        "Failed to generate reports using " + reporter, e);
            }
        }
    }

    /**
     * What reports is this test runner configured to generate?
     */
    public List<AcceptanceTestReporter> getSubscribedReporters() {
        return ImmutableList.copyOf(subscribedReporters);
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
    protected void runChild(final FrameworkMethod method, final RunNotifier notifier) {
        if (weShouldIgnore(method)) {
            notifier.fireTestIgnored(describeChild(method));
        } else {
            super.runChild(method, notifier);
        }
    }

    private boolean weShouldIgnore(final FrameworkMethod method) {
        return  ((failureListener.aPreviousTestHasFailed()) || isPending(method));
    }

    private boolean isPending(final FrameworkMethod method) {
        Method testMethod = method.getMethod();
        Pending pending = testMethod.getAnnotation(Pending.class);
        return (pending != null);
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


    protected WebDriver getDriver() {
        return webdriverManager.getWebdriver();
    }

    @Override
    protected List<FrameworkMethod> computeTestMethods() {
        List<FrameworkMethod> unorderedTests = super.computeTestMethods();       
        return orderTestStepsIn(unorderedTests);
    }
    
    private List<FrameworkMethod> orderTestStepsIn(final List<FrameworkMethod> unorderedTests) {
        // TODO : Rewrite all this cleanly
        List<OrderedTestStepMethod> orderedTests = new ArrayList<OrderedTestStepMethod>();
        for(FrameworkMethod testMethod : unorderedTests) {
            OrderedTestStepMethod orderedTest = null;
            Step step = testMethod.getAnnotation(Step.class);
            if (step != null) {
                orderedTest = new OrderedTestStepMethod(testMethod,step.value());
            } else {
                orderedTest = new OrderedTestStepMethod(testMethod,0);
            }
            orderedTests.add(orderedTest);
        }
        return orderedFrameworkMethodsIn(orderedTests);
    }

    private List<FrameworkMethod> orderedFrameworkMethodsIn(final List<OrderedTestStepMethod> orderedTests) {
        Collections.sort(orderedTests);
        
        List<FrameworkMethod> orderedFramework = new ArrayList<FrameworkMethod>();
        
        for(OrderedTestStepMethod orderedMethod : orderedTests) {
            orderedFramework.add(orderedMethod.getFrameworkMethod());
        }
        
        return orderedFramework;
    }
    
}
