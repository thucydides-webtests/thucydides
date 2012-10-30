package net.thucydides.core.statistics;

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.guice.DatabaseConfig;
import net.thucydides.core.model.Story;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.statistics.dao.TestOutcomeHistoryDAO;
import net.thucydides.core.steps.ExecutedStepDescription;
import net.thucydides.core.steps.StepFailure;
import net.thucydides.core.steps.StepListener;
import net.thucydides.core.util.EnvironmentVariables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Records test results in a database, for reporting on test statistics.
 */
public class StatisticsListener implements StepListener {

    private final TestOutcomeHistoryDAO testOutcomeHistoryDAO;
    private final EnvironmentVariables environmentVariables;
    private final List<TestOutcome> testOutcomes;
    private final DatabaseConfig databaseConfig;

    private static final Logger LOGGER = LoggerFactory.getLogger(StatisticsListener.class);

    @Inject
    public StatisticsListener(TestOutcomeHistoryDAO testOutcomeHistoryDAO,
                              EnvironmentVariables environmentVariables,
                              DatabaseConfig databaseConfig) {
        this.testOutcomeHistoryDAO = testOutcomeHistoryDAO;
        this.environmentVariables = environmentVariables;
        this.databaseConfig = databaseConfig;
        testOutcomes = Collections.synchronizedList(new ArrayList<TestOutcome>());
    }

    @Override
    public void testSuiteStarted(Class<?> storyClass) {
    }

    @Override
    public void testSuiteStarted(Story story) {
    }

    @Override
    public void testStarted(String description) {
    }

    @Override
    public void testFinished(TestOutcome result) {

        if (historyActivated()) {
            if (!testOutcomes.contains(result)) {
                testOutcomes.add(result);
            }
        }
    }

    @Override
    public void testSuiteFinished() {
        if (historyActivated()) {
            synchronized (testOutcomes) {
                storePending(testOutcomes);
            }
        }
    }

    private void storePending(List<TestOutcome> testOutcomes) {
        List<TestOutcome> outcomesReadyToBeStored = ImmutableList.copyOf(testOutcomes);
        testOutcomeHistoryDAO.storeTestOutcomes(outcomesReadyToBeStored);
        testOutcomes.removeAll(outcomesReadyToBeStored);
    }


    private boolean historyActivated() {
        return databaseConfig.isActive() && environmentVariables.getPropertyAsBoolean(ThucydidesSystemProperty.RECORD_STATISTICS.getPropertyName(), true);
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
    public void testFailed(TestOutcome result, Throwable cause) {
    }

    @Override
    public void testIgnored() {
    }

    @Override
    public void notifyScreenChange() {
    }
}
