package net.thucydides.core;

import net.thucydides.core.annotations.TestCaseAnnotations;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.pages.Pages;
import net.thucydides.core.sessions.TestSessionVariables;
import net.thucydides.core.steps.BaseStepListener;
import net.thucydides.core.steps.StepAnnotations;
import net.thucydides.core.steps.StepEventBus;
import net.thucydides.core.steps.StepFactory;
import net.thucydides.core.steps.StepListener;
import net.thucydides.core.util.LocalPreferences;
import net.thucydides.core.webdriver.Configuration;
import net.thucydides.core.webdriver.WebDriverFactory;
import net.thucydides.core.webdriver.WebdriverManager;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * A utility class that provides services to initialize web testing and reporting-related fields in arbitrary objects.
 * It is designed to help integrate Thucydides into other testing tools such as Cucumber.
 */
public class Thucydides {

    private static final ThreadLocal<WebDriverFactory> factoryThreadLocal = new ThreadLocal<WebDriverFactory>();
    private static final ThreadLocal<WebdriverManager> webdriverManagerThreadLocal = new ThreadLocal<WebdriverManager>();
    private static final ThreadLocal<Pages> pagesThreadLocal = new ThreadLocal<Pages>();
    private static final ThreadLocal<StepFactory> stepFactoryThreadLocal = new ThreadLocal<StepFactory>();
    private static final ThreadLocal<StepListener> stepListenerThreadLocal = new ThreadLocal<StepListener>();
    private static final ThreadLocal<TestSessionVariables> testSessionThreadLocal = new ThreadLocal<TestSessionVariables>();

    /**
     * Initialize Thucydides-related fields in the specified object.
     * This includes managed WebDriver instances,
     */
    public static void initialize(final Object testCase) {
        setupWebDriverFactory();
        setupWebdriverManager();

        initPagesObjectUsing(getDriver());
        initStepListener();
        initStepFactoryUsing(getPages());

        injectDriverInto(testCase);
        injectAnnotatedPagesObjectInto(testCase);
        injectScenarioStepsInto(testCase);
    }

    private static void initStepListener() {
        Configuration configuration = Injectors.getInjector().getInstance(Configuration.class);
        File outputDirectory = configuration.loadOutputDirectoryFromSystemProperties();
        StepListener listener  = new BaseStepListener(outputDirectory, getPages());
        stepListenerThreadLocal.set(listener);
        StepEventBus.getEventBus().registerListener(getStepListener());
    }

    private static void setupWebDriverFactory() {
        factoryThreadLocal.set(new WebDriverFactory());
    }

    private static void initPagesObjectUsing(final WebDriver driver) {
        pagesThreadLocal.set(new Pages(driver));
    }

    private static void initStepFactoryUsing(final Pages pagesObject) {
        stepFactoryThreadLocal.set(new StepFactory(pagesObject));
    }

    /**
     * Instantiate the @Managed-annotated WebDriver instance with current WebDriver.
     */
    protected static void injectDriverInto(final Object testCase) {
        TestCaseAnnotations.forTestCase(testCase).injectDriver(getDriver());
    }

    /**
     * Instantiates the @ManagedPages-annotated Pages instance using current WebDriver.
     */
    protected static void injectScenarioStepsInto(final Object testCase) {
        StepAnnotations.injectScenarioStepsInto(testCase, getStepFactory());

    }

    /**
     * Instantiates the @ManagedPages-annotated Pages instance using current WebDriver.
     */
    protected static void injectAnnotatedPagesObjectInto(final Object testCase) {
        StepAnnotations.injectAnnotatedPagesObjectInto(testCase, getPages());
    }

    /**
     * Indicate that the test run using this object is finished, and reports can be generated.
     */
    public static void done() {
        getWebdriverManager().closeAllDrivers();

    }

    public static String getCurrentSessionID() {
        if ((getWebdriverManager() != null) && (getWebdriverManager().getSessionId() != null)) {
            return getWebdriverManager().getSessionId().toString();
        }
        return null;
    }

    protected static WebDriver getDriver() {
        return getWebdriverManager().getWebdriver();
    }

    protected static Pages getPages() {
        return pagesThreadLocal.get();
    }

    protected static void stopUsingMockDriver() {
        setupWebdriverManager();
    }

    private static WebdriverManager getWebdriverManager() {
        return webdriverManagerThreadLocal.get();
    }

    private static StepFactory getStepFactory() {
        return stepFactoryThreadLocal.get();
    }

    private static void setupWebdriverManager() {
        setupWebdriverManager(Injectors.getInjector().getInstance(WebdriverManager.class));
    }

    private static void setupWebdriverManager(WebdriverManager webdriverManager) {
        webdriverManagerThreadLocal.set(webdriverManager);
    }

    public static StepListener getStepListener() {
        return stepListenerThreadLocal.get();
    }


    public static void loadLocalPreferences() throws IOException {
        LocalPreferences localPreferences = Injectors.getInjector().getInstance(LocalPreferences.class);
        localPreferences.loadPreferences();
    }

    public static void initializeTestSession() {
        getCurrentSession().clear();
    }

    public static Map getCurrentSession() {

        if (testSessionThreadLocal.get() == null) {
            testSessionThreadLocal.set(new TestSessionVariables());
        }
        return testSessionThreadLocal.get();
    }

    public static void pendingStep(String reason) {
        throw new PendingStepException(reason);
    }

    public static void ignoredStep(String reason) {
        throw new IgnoredStepException(reason);
    }

    /**
     * The current working directory name is used as a default project key if no other key is provided.
     */
    public static String getDefaultProjectKey() {
        String workingDirPath = System.getProperty("user.dir");
        return new File(workingDirPath).getName();
    }
}
