package net.thucydides.junit.listeners;

import net.thucydides.core.Thucydides;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.logging.LoggingLevel;
import net.thucydides.core.model.DataTable;
import net.thucydides.core.model.Story;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.steps.ExecutedStepDescription;
import net.thucydides.core.steps.StepFailure;
import net.thucydides.core.steps.StepListener;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.junit.finder.TestFinder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class TestCountListener implements StepListener {

    private final Logger logger;
    private final EnvironmentVariables environmentVariables;
    private final AtomicInteger testCount = new AtomicInteger();

    protected TestCountListener(EnvironmentVariables environmentVariables, Logger logger) {
        this.logger = logger;
        this.environmentVariables = environmentVariables;

        logTotalTestCount();
    }

    public TestCountListener(EnvironmentVariables environmentVariables) {
        this(environmentVariables, LoggerFactory.getLogger(Thucydides.class));
    }

    private void logTotalTestCount() {
        String rootPackage = ThucydidesSystemProperty.THUCYDIDES_TEST_ROOT.from(environmentVariables);
        if (StringUtils.isNotEmpty(rootPackage)) {
            TestFinder finder = TestFinder.thatFinds().allTests().inPackage(rootPackage);
            int testMethodCount = finder.countTestMethods();
            if (loggingLevelIsAtLeast(LoggingLevel.NORMAL)) {
                getLogger().info("PREPARING TO EXECUTE {} TESTS", testMethodCount);
            }
        }
    }

    private boolean loggingLevelIsAtLeast(LoggingLevel minimumLoggingLevel) {
        return (getLoggingLevel().compareTo(minimumLoggingLevel) >= 0);
    }

    protected Logger getLogger() {
        return logger;
    }

    private LoggingLevel getLoggingLevel() {
        String logLevel = ThucydidesSystemProperty.LOGGING.from(environmentVariables, LoggingLevel.NORMAL.name());

        return LoggingLevel.valueOf(logLevel);
    }


    public void testSuiteStarted(Class<?> storyClass) {
    }


    public void testSuiteStarted(Story story) {
    }


    public void testSuiteFinished() {
    }


    public void testStarted(String description) {
        int currentTestCount = testCount.addAndGet(1);
        if (loggingLevelIsAtLeast(LoggingLevel.NORMAL)) {
            getLogger().info("TEST NUMBER: {}", currentTestCount);
        }
    }


    public void testFinished(TestOutcome result) {
    }


    public void stepStarted(ExecutedStepDescription description) {
    }


    public void skippedStepStarted(ExecutedStepDescription description) {
    }


    public void stepFailed(StepFailure failure) {
    }


    public void lastStepFailed(StepFailure failure) {
    }


    public void stepIgnored() {
    }


    public void stepIgnored(String message) {
    }


    public void stepPending() {
    }


    public void stepPending(String message) {
    }


    public void stepFinished() {
    }


    public void testFailed(TestOutcome testOutcome, Throwable cause) {
    }


    public void testIgnored() {
    }


    public void notifyScreenChange() {
    }

    public void useExamplesFrom(DataTable table) {
    }

    public void exampleStarted(Map<String, String> data) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void exampleFinished() {
    }
}
