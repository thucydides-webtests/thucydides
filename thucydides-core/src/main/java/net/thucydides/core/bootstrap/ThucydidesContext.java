package net.thucydides.core.bootstrap;

import net.thucydides.core.guice.Injectors;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.reports.ReportService;
import net.thucydides.core.steps.BaseStepListener;
import net.thucydides.core.steps.Listeners;
import net.thucydides.core.steps.StepAnnotations;
import net.thucydides.core.steps.StepEventBus;
import net.thucydides.core.steps.StepFactory;
import net.thucydides.core.steps.StepListener;
import net.thucydides.core.webdriver.Configuration;
import net.thucydides.core.webdriver.WebDriverFactory;

import java.io.File;
import java.util.List;

/**
 * Container holding thread-local data related to a Thucydides test run.
 * This includes the StepFactory and the associated step listeners. These need to be thread-local so that,
 * if tests are run in parallel in different threads, the step listeners will still build up correct result trees
 * and report data.
 */
class ThucydidesContext {
    private static final ThreadLocal<ThucydidesContext> contextThreadLocal = new ThreadLocal<ThucydidesContext>();

    /**
     * Instruments step libraries to that any @Step-annotated methods called will appear in the test reports.
     */
    private final StepFactory stepFactory;

    /**
     * The main step listener used to record test results and outcomes.
     */
    private BaseStepListener stepListener;

    /**
     * Generates reports once the test outcomes have been recorded and built.
     */
    private final ReportService reportService;

    /**
     * Where are the Thucydides reports written to.
     * Normally defined in the system properties.
     */
    private File outputDirectory;

    private WebDriverFactory webDriverFactory;

    /**
     * Thucydides configuration data
     */
    Configuration configuration;

    private ThucydidesContext(StepListener... additionalListeners) {
        configuration = Injectors.getInjector().getInstance(Configuration.class);
        outputDirectory = configuration.getOutputDirectory();
        stepFactory = new StepFactory();
        registerStepListeners(additionalListeners);
        reportService = new ReportService(outputDirectory,
                                          ReportService.getDefaultReporters());
    }

    public void setOutputDirectory(File outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    private void registerStepListeners(StepListener... additionalListeners) {
        stepListener = buildBaseStepListener();
        StepEventBus.getEventBus().registerListener(stepListener);
        for (StepListener listener : additionalListeners) {
            StepEventBus.getEventBus().registerListener(listener);
        }
    }

    public static ThucydidesContext newContext(StepListener... listeners) {
        ThucydidesContext context = new ThucydidesContext(listeners);
        contextThreadLocal.set(context);
        return context;
    }

    public static ThucydidesContext getCurrentContext() {
        return contextThreadLocal.get();
    }

    /**
     * Injects instrumented step classes into any @Step annotated fields of the specified class.
     *
     * @param testCase
     */
    public void initialize(Object testCase) {
        StepAnnotations.injectScenarioStepsInto(testCase, stepFactory);
    }


    public void generateReports() {
        reportService.generateReportsFor(latestTestOutcomes());
    }

    private List<TestOutcome> latestTestOutcomes() {
        return stepListener.getTestOutcomes();
    }

    public void dropListeners() {
        StepEventBus.getEventBus().dropAllListeners();
    }


    private BaseStepListener buildBaseStepListener() {
        return Listeners.getBaseStepListener().withOutputDirectory(outputDirectory);
    }
//    private BaseStepListener buildBaseStepListener() {
//        if (pageFactory != null) {
//            return Listeners.getBaseStepListener()
//                    .withPages(pageFactory)
//                    .and().withOutputDirectory(outputDirectory);
//        } else {
//            return Listeners.getBaseStepListener()
//                    .withOutputDirectory(outputDirectory);
//        }
//    }
}
