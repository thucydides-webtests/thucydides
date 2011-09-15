package net.thucydides.junit.runners;

import net.thucydides.core.steps.StepPublisher;
import org.junit.runners.model.Statement;

/**
 * A JUnit statement that runs a Thucydides-enabled test and then publishes the results via JUnit.
 */
public class ThucydidesStatement extends Statement {

    private final Statement statement;
    private final StepPublisher publisher;

    public ThucydidesStatement(final Statement statement, final StepPublisher publisher) {
        this.statement = statement;
        this.publisher = publisher;
    }

    @Override
    public void evaluate() throws Throwable {
        statement.evaluate();
        if (publisher.aStepHasFailed()) {
            throw publisher.getTestFailureCause();
        }
    }
}
