package net.thucydides.junit.runners;

import net.thucydides.core.model.DataTable;
import net.thucydides.core.steps.BaseStepListener;
import net.thucydides.core.steps.StepEventBus;
import net.thucydides.core.steps.StepListener;
import net.thucydides.junit.listeners.JUnitStepListener;
import org.junit.runner.Description;
import org.junit.runner.notification.Failure;

public class ParameterizedJUnitStepListener extends JUnitStepListener {

    final int parameterSetNumber;
    private final DataTable parametersTable;


    public ParameterizedJUnitStepListener(final int parameterSetNumber, final DataTable parametersTable,
                                          BaseStepListener baseStepListener, StepListener... listeners) {
        super(baseStepListener, listeners);
        this.parameterSetNumber = parameterSetNumber;
        this.parametersTable = parametersTable;

    }

    @Override
    public void testStarted(final Description description) {
        if (testingThisDataSet(description)) {
            super.testStarted(description);
            StepEventBus.getEventBus().useExamplesFrom(dataTable());
            StepEventBus.getEventBus().exampleStarted(parametersTable.row(parameterSetNumber).toStringMap());
        }
    }

    private DataTable dataTable() {
        return DataTable.withHeaders(parametersTable.getHeaders()).andCopyRowDataFrom(parametersTable.getRows().get(parameterSetNumber)).build();
    }

    private boolean testingThisDataSet(Description description) {
        return (description.getMethodName().endsWith("[" + parameterSetNumber + "]"));
    }

    @Override
    public void testFinished(final Description description) throws Exception {
        if (testingThisDataSet(description)) {
            super.testFinished(description);
            StepEventBus.getEventBus().exampleFinished();
        }
    }

    @Override
    public void testFailure(final Failure failure) throws Exception {
        if (testingThisDataSet(failure.getDescription())) {
            super.testFailure(failure);
            StepEventBus.getEventBus().exampleFinished();
        }
    }

    @Override
    public void testIgnored(final Description description) throws Exception {
        if (testingThisDataSet(description)) {
            super.testIgnored(description);
            StepEventBus.getEventBus().exampleFinished();
        }
    }

}
