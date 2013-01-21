package net.thucydides.junit.runners;

import net.thucydides.core.guice.Injectors;
import net.thucydides.core.model.DataTable;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.TestStep;
import net.thucydides.core.reports.AcceptanceTestReporter;
import net.thucydides.core.reports.ReportService;
import net.thucydides.core.webdriver.Configuration;
import net.thucydides.core.webdriver.WebDriverFactory;
import net.thucydides.junit.annotations.Concurrent;
import org.apache.commons.lang3.StringUtils;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.Suite;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Run a Thucydides test suite using a set of data.
 * Similar to the JUnit parameterized tests, but better ;-).
 *
 */
public class ThucydidesParameterizedRunner extends Suite {

    private static final int AVAILABLE_PROCESSORS = Runtime.getRuntime().availableProcessors();

    private final List<Runner> runners = new ArrayList<Runner>();

    private final Configuration configuration;
    private ReportService reportService;

    /**
     * Only used for testing.
     */
    public ThucydidesParameterizedRunner(final Class<?> klass,
                                         Configuration configuration,
                                         final WebDriverFactory webDriverFactory) throws Throwable {
        super(klass, Collections.<Runner>emptyList());
        this.configuration = configuration;

        if (runTestsInParallelFor(klass)) {
            scheduleParallelTestRunsFor(klass);
        }

        DataDrivenAnnotations testClassAnnotations = getTestAnnotations();
        if (testClassAnnotations.hasTestDataDefined()) {
            buildTestRunnersForEachDataSetUsing(webDriverFactory);
        } else if (testClassAnnotations.hasTestDataSourceDefined()) {
            buildTestRunnersFromADataSourceUsing(webDriverFactory);
        }
    }

    private void scheduleParallelTestRunsFor(final Class<?> klass) {
        setScheduler(new ParameterizedRunnerScheduler(klass, getThreadCountFor(klass)));
    }

    protected boolean runTestsInParallelFor(final Class<?> klass) {
        return (klass.getAnnotation(Concurrent.class) != null);
    }

    protected int getThreadCountFor(final Class<?> klass) {
        Concurrent concurrent = klass.getAnnotation(Concurrent.class);
        String threadValue = concurrent.threads();
        int threads = (AVAILABLE_PROCESSORS * 2);
        if (StringUtils.isNotEmpty(threadValue)) {
            if (StringUtils.isNumeric(threadValue)) {
                threads = Integer.valueOf(threadValue);
            } else if (threadValue.endsWith("x")) {
                threads = getRelativeThreadCount(threadValue);
            }

        }
        return threads;
    }

    private int getRelativeThreadCount(final String threadValue) {
        try {
            String threadCount = threadValue.substring(0, threadValue.length() - 1);
            return Integer.valueOf(threadCount) * AVAILABLE_PROCESSORS;
        } catch (NumberFormatException cause) {
            throw new IllegalArgumentException("Illegal thread value: " + threadValue, cause);
        }
    }

    private void buildTestRunnersForEachDataSetUsing(final WebDriverFactory webDriverFactory) throws Throwable {
        DataTable parametersTable = getTestAnnotations().getParametersTableFromTestDataAnnotation();
        for (int i = 0; i < parametersTable.getRows().size(); i++) {
            Class<?> testClass = getTestClass().getJavaClass();
            ThucydidesRunner runner = new TestClassRunnerForParameters(testClass,
                                                                       configuration,
                                                                       webDriverFactory,
                                                                       parametersTable,
                                                                       i);
            runner.useQualifier(from(parametersTable.getRows().get(i).getValues()));
            runners.add(runner);
        }
    }

    private void buildTestRunnersFromADataSourceUsing(final WebDriverFactory webDriverFactory) throws Throwable {

        List<?> testCases = getTestAnnotations().getDataAsInstancesOf(getTestClass().getJavaClass());
        DataTable parametersTable = getTestAnnotations().getParametersTableFromTestDataSource();

        for (int i = 0; i < testCases.size(); i++) {
            Object testCase = testCases.get(i);
            ThucydidesRunner runner = new TestClassRunnerForInstanciatedTestCase(testCase,
                                                                                 configuration,
                                                                                 webDriverFactory,
                                                                                 parametersTable,
                                                                                 i);
            runner.useQualifier(getQualifierFor(testCase));
            runners.add(runner);
        }
    }

    private String getQualifierFor(final Object testCase) {
        return QualifierFinder.forTestCase(testCase).getQualifier();
    }

    private DataDrivenAnnotations getTestAnnotations() {
        return DataDrivenAnnotations.forClass(getTestClass());
    }

    private String from(final Collection testData) {
        StringBuffer testDataQualifier = new StringBuffer();
        boolean firstEntry = true;
        for (Object testDataValue : testData) {
            if (!firstEntry) {
                testDataQualifier.append("/");
            }
            testDataQualifier.append(testDataValue);
            firstEntry = false;
        }
        return testDataQualifier.toString();
    }


    /**
     * Only called reflectively. Do not use programmatically.
     */
    public ThucydidesParameterizedRunner(final Class<?> klass) throws Throwable {
        this(klass, Injectors.getInjector().getInstance(Configuration.class), new WebDriverFactory());
    }

    @Override
    protected List<Runner> getChildren() {
        return runners;
    }

    @Override
    public void run(final RunNotifier notifier) {
        try {
            super.run(notifier);
        } finally {
            generateReports();
        }
    }

    public void generateReports() {
        generateReportsFor(aggregateTestOutcomesByTestMethods());
    }

    private void generateReportsFor(List<TestOutcome> testOutcomes) {
        getReportService().generateReportsFor(testOutcomes);
    }

    private ReportService getReportService() {
        if (reportService == null) {
            reportService = new ReportService(getOutputDirectory(), getDefaultReporters());
        }
        return reportService;

    }

    private Collection<AcceptanceTestReporter> getDefaultReporters() {
        return ReportService.getDefaultReporters();
    }

    private File getOutputDirectory() {
        return this.configuration.getOutputDirectory();
    }

    public void subscribeReporter(final AcceptanceTestReporter reporter) {
        getReportService().subscribe(reporter);
    }


    public List<TestOutcome> aggregateTestOutcomesByTestMethods() {
        List<TestOutcome> aggregatedScenarioOutcomes = new ArrayList<TestOutcome>();
        List<TestOutcome> allOutcomes = getTestOutcomesForAllParameterSets();

        if (allOutcomes.isEmpty()) {
            return aggregatedScenarioOutcomes;
        }

        Map<String, TestOutcome> scenarioOutcomes = new HashMap<String,TestOutcome>();

        TestOutcome firstParameterizedOutcome = allOutcomes.remove(0);

        TestOutcome firstScenarioOutcome = getScenarioOutcome(firstParameterizedOutcome);
        scenarioOutcomes.put(firstScenarioOutcome.getMethodName(), firstScenarioOutcome);

        for(TestOutcome testOutcome : allOutcomes) {
            String normalizedMethodName = normalizeMethodName(testOutcome.getMethodName());
            if (scenarioOutcomes.containsKey(normalizedMethodName)) {
                List<TestStep> testSteps = testOutcome.getTestSteps();
                if (! testSteps.isEmpty()) {
                    TestStep nextStep = testSteps.get(0);
                    nextStep.setDescription(normalizeTestStepDescription(nextStep.getDescription(), scenarioOutcomes.get(normalizedMethodName).getTestSteps().size() + 1));
                    scenarioOutcomes.get(normalizedMethodName).recordStep(nextStep);
                }

                scenarioOutcomes.get(normalizedMethodName).getDataTable().addRows(testOutcome.getDataTable().getRows());

            } else {
                 TestOutcome scenarioOutcome = getScenarioOutcome(testOutcome);
                 scenarioOutcomes.put(scenarioOutcome.getMethodName(), scenarioOutcome);
            }
        }
        aggregatedScenarioOutcomes.addAll(scenarioOutcomes.values());
        return aggregatedScenarioOutcomes;
    }

    private String normalizeTestStepDescription(String description, int index) {
        return StringUtils.replace(description,"[1]","[" + index + "]");
    }

    private TestOutcome getScenarioOutcome(TestOutcome parameterizedOutcome) {
        TestOutcome scenarioOutcome = parameterizedOutcome.withMethodName(normalizeMethodName(parameterizedOutcome.getMethodName()));
        scenarioOutcome.endGroup(); //pop group stack so next item gets added as sibling
        return scenarioOutcome;
    }

    private String normalizeMethodName(String methodName) {
        return methodName.replaceAll("\\[\\d\\]","");
    }


    public List<TestOutcome> getTestOutcomesForAllParameterSets() {
        List<TestOutcome> testOutcomes = new ArrayList<TestOutcome>();

        testOutcomes.addAll( ((ThucydidesRunner) runners.get(0)).getTestOutcomes());
        for (Runner runner : runners) {
            for(TestOutcome testOutcome : ((ThucydidesRunner) runner).getTestOutcomes()) {
                if (!testOutcomes.contains(testOutcome)) {
                    testOutcomes.add(testOutcome);
                }
            }
        }
        return testOutcomes;
    }

}
