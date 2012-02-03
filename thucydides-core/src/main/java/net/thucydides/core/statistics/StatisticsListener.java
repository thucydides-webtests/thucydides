package net.thucydides.core.statistics;

import net.thucydides.core.model.Story;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.steps.ExecutedStepDescription;
import net.thucydides.core.steps.StepFailure;
import net.thucydides.core.steps.StepListener;

public class StatisticsListener implements StepListener {
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
    public void stepIgnored() {
    }

    @Override
    public void stepPending() {
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
