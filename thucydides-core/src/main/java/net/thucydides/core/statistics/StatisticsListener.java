package net.thucydides.core.statistics;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.model.Story;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.statistics.dao.TestOutcomeHistoryDAO;
import net.thucydides.core.steps.ExecutedStepDescription;
import net.thucydides.core.steps.StepFailure;
import net.thucydides.core.steps.StepListener;
import net.thucydides.core.util.EnvironmentVariables;
import org.apache.commons.collections.list.SynchronizedList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StatisticsListener implements StepListener {

    private final TestOutcomeHistoryDAO testOutcomeHistoryDAO;
    private final EnvironmentVariables environmentVariables;
    private final List<TestOutcome> testOutcomes;

    @Inject
    public StatisticsListener(TestOutcomeHistoryDAO testOutcomeHistoryDAO,
                              EnvironmentVariables environmentVariables) {
        this.testOutcomeHistoryDAO = testOutcomeHistoryDAO;
        this.environmentVariables = environmentVariables;
        testOutcomes = Collections.synchronizedList(new ArrayList<TestOutcome>());
    }

    @Override
    public void testSuiteStarted(Class<?> storyClass) {
        testOutcomes.clear();
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
            testOutcomes.add(result);
        }
    }

    @Override
    public void testSuiteFinished() {
        if (historyActivated()) {
            testOutcomeHistoryDAO.storeTestOutcomes(testOutcomes);
        }
    }

    private boolean historyActivated() {
        return environmentVariables.getPropertyAsBoolean(
                ThucydidesSystemProperty.RECORD_STATISTICS.getPropertyName(), false);
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
    public void testFailed(Throwable cause) {
    }

    @Override
    public void testIgnored() {
    }

    @Override
    public void notifyScreenChange() {
    }
}
