package net.thucydides.junit.runners;

import net.thucydides.core.guice.Injectors;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.webdriver.Configuration;
import net.thucydides.core.webdriver.WebDriverFactory;
import net.thucydides.junit.annotations.Concurrent;
import org.apache.commons.lang.StringUtils;
import org.junit.runner.Runner;
import org.junit.runners.Suite;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Run a Thucydides test suite using a set of data.
 * Similar to the JUnit parameterized tests, but better ;-).
 *
 */
public class ThucydidesParameterizedRunner extends Suite {

    private static final int AVAILABLE_PROCESSORS = Runtime.getRuntime().availableProcessors();

    private final List<Runner> runners = new ArrayList<Runner>();

    private final Configuration configuration;

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
        List<Object[]> parametersList = getTestAnnotations().getParametersList();
        for (int i = 0; i < parametersList.size(); i++) {
            Class<?> testClass = getTestClass().getJavaClass();
            ThucydidesRunner runner = new TestClassRunnerForParameters(testClass,
                                                                       configuration,
                                                                       webDriverFactory,
                                                                       parametersList,
                                                                       i);
            runner.useQualifier(from(parametersList.get(i)));
            runners.add(runner);
        }
    }

    private void buildTestRunnersFromADataSourceUsing(final WebDriverFactory webDriverFactory) throws Throwable {

        List<?> testCases = getTestAnnotations().getDataAsInstancesOf(getTestClass().getJavaClass());

        for (int i = 0; i < testCases.size(); i++) {
            Object testCase = testCases.get(i);
            ThucydidesRunner runner = new TestClassRunnerForInstanciatedTestCase(testCase,
                                                                                 configuration,
                                                                                 webDriverFactory,
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

    private String from(final Object[] testData) {
        StringBuffer testDataQualifier = new StringBuffer();
        boolean firstEntry = true;
        for (Object testDataValue : testData) {
            if (!firstEntry) {
                testDataQualifier.append("_");
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

    public List<TestOutcome> getTestOutcomes() {
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
