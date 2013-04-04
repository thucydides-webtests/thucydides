package net.thucydides.core.statistics;

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.guice.DatabaseConfig;
import net.thucydides.core.model.DataTable;
import net.thucydides.core.model.Story;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.statistics.dao.TestOutcomeHistoryDAO;
import net.thucydides.core.steps.ExecutedStepDescription;
import net.thucydides.core.steps.StepFailure;
import net.thucydides.core.steps.StepListener;
import net.thucydides.core.util.EnvironmentVariables;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Records test results in a database, for reporting on test statistics.
 */
public class StatisticsListener implements StepListener {

    private final TestOutcomeHistoryDAO testOutcomeHistoryDAO;
    private final EnvironmentVariables environmentVariables;
    private final List<TestOutcome> testOutcomes;
    private final DatabaseConfig databaseConfig;

    @Inject
    public StatisticsListener(TestOutcomeHistoryDAO testOutcomeHistoryDAO,
                              EnvironmentVariables environmentVariables,
                              DatabaseConfig databaseConfig) {
        this.testOutcomeHistoryDAO = testOutcomeHistoryDAO;
        this.environmentVariables = environmentVariables;
        this.databaseConfig = databaseConfig;
        testOutcomes = Collections.synchronizedList(new ArrayList<TestOutcome>());
    }


    public void testSuiteStarted(Class<?> storyClass) {
    }


    public void testSuiteStarted(Story story) {
    }


    public void testStarted(String description) {
    }


    public void testFinished(TestOutcome result) {

        if (historyActivated()) {
            if (!testOutcomes.contains(result)) {
                testOutcomes.add(result);
            }
        }
    }


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


    public void testFailed(TestOutcome result, Throwable cause) {
    }


    public void testIgnored() {
    }


    public void notifyScreenChange() {
    }

    public void useExamplesFrom(DataTable table) {
    }

    public void exampleStarted(Map<String,String> data) {
    }

    @Override
    public void exampleStarted() {
    }

    public void exampleFinished() {
    }
}
