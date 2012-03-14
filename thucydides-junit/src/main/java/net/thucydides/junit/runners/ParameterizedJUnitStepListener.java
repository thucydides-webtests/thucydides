package net.thucydides.junit.runners;

import net.thucydides.core.pages.Pages;
import net.thucydides.core.steps.BaseStepListener;
import net.thucydides.core.steps.StepListener;
import net.thucydides.junit.listeners.JUnitStepListener;
import org.junit.runner.Description;
import org.junit.runner.notification.Failure;

import java.io.File;

public class ParameterizedJUnitStepListener extends JUnitStepListener {

    final int parameterSetNumber;

    public ParameterizedJUnitStepListener(final int parameterSetNumber, BaseStepListener baseStepListener, StepListener... listeners) {
        super(baseStepListener, listeners);
        this.parameterSetNumber = parameterSetNumber;
    }

    @Override
    public void testStarted(final Description description) {
        if (testingThisDataSet(description)) {
            super.testStarted(description);
        }
    }

    private boolean testingThisDataSet(Description description) {
        return (description.getMethodName().endsWith("[" + parameterSetNumber + "]"));
    }

    @Override
    public void testFinished(final Description description) throws Exception {
        if (testingThisDataSet(description)) {
            super.testFinished(description);
        }
    }

    @Override
    public void testFailure(final Failure failure) throws Exception {
        if (testingThisDataSet(failure.getDescription())) {
            super.testFailure(failure);
        }
    }

    @Override
    public void testIgnored(final Description description) throws Exception {
        if (testingThisDataSet(description)) {
            super.testIgnored(description);
        }
    }

}
