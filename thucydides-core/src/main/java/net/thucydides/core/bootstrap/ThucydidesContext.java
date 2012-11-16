package net.thucydides.core.bootstrap;

import net.thucydides.core.guice.Injectors;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.reports.ReportService;
import net.thucydides.core.steps.BaseStepListener;
import net.thucydides.core.steps.StepAnnotations;
import net.thucydides.core.steps.StepEventBus;
import net.thucydides.core.steps.StepFactory;
import net.thucydides.core.steps.StepListener;
import net.thucydides.core.webdriver.Configuration;

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

    private final BaseStepListener stepListener;
    /**
     * Generates reports once the test outcomes have been recorded and built.
     */
    private final ReportService reportService;

    /**
     * Where are the Thucydides reports written to.
     * Normally defined in the system properties.
     */
    private File outputDirectory;

    /**
     * Thucydides configuration data
     */
    Configuration configuration;

    private ThucydidesContext() {
        configuration = Injectors.getInjector().getInstance(Configuration.class);
        outputDirectory = configuration.getOutputDirectory();
        stepFactory = new StepFactory();
        stepListener = registerStepListener();
        reportService = new ReportService(outputDirectory,
                                          ReportService.getDefaultReporters());
    }

    public void setOutputDirectory(File outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    private BaseStepListener registerStepListener() {
        File outputDirectory = configuration.loadOutputDirectoryFromSystemProperties();
        BaseStepListener listener = new BaseStepListener(outputDirectory);
        StepEventBus.getEventBus().registerListener(listener);
        return listener;
    }

    public static ThucydidesContext getCurrentContext() {
        if (contextThreadLocal.get() == null) {
            contextThreadLocal.set(new ThucydidesContext());
        }
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

}
