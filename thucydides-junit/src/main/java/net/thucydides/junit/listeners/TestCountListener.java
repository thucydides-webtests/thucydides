package net.thucydides.junit.listeners;

import net.thucydides.core.Thucydides;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.logging.LoggingLevel;
import net.thucydides.core.model.Story;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.steps.ExecutedStepDescription;
import net.thucydides.core.steps.StepFailure;
import net.thucydides.core.steps.StepListener;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.junit.finder.TestFinder;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        String rootPackage = ThucydidesSystemProperty.TEST_ROOT_PACKAGE.from(environmentVariables);
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

    @Override
    public void testSuiteStarted(Class<?> storyClass) {
    }

    @Override
    public void testSuiteStarted(Story story) {
    }

    @Override
    public void testSuiteFinished() {
    }

    @Override
    public void testStarted(String description) {
        int currentTestCount = testCount.addAndGet(1);
        if (loggingLevelIsAtLeast(LoggingLevel.NORMAL)) {
            getLogger().info("TEST NUMBER: {}", currentTestCount);
        }
        System.out.println("TEST NUMBER: " + currentTestCount + "(" + description + ")");
    }

    @Override
    public void testFinished(TestOutcome result) {
    }

    @Override
    public void stepStarted(ExecutedStepDescription description) {
    }

    @Override
    public void skippedStepStarted(ExecutedStepDescription description) {
    }

    @Override
    public void stepFailed(StepFailure failure) {
    }

    @Override
    public void lastStepFailed(StepFailure failure) {
    }

    @Override
    public void stepIgnored() {
    }

    @Override
    public void stepIgnored(String message) {
    }

    @Override
    public void stepPending() {
    }

    @Override
    public void stepPending(String message) {
    }

    @Override
    public void stepFinished() {
    }

    @Override
    public void testFailed(TestOutcome testOutcome, Throwable cause) {
    }

    @Override
    public void testIgnored() {
    }

    @Override
    public void notifyScreenChange() {
    }
}
