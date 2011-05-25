package net.thucydides.junit.runners;

import net.thucydides.core.steps.StepListener;
import org.junit.runners.model.Statement;

/**
 * Created by IntelliJ IDEA.
 * User: johnsmart
 * Date: 21/05/11
 * Time: 9:47 AM
 * To change this template use File | Settings | File Templates.
 */
public class ThucydidesStatement extends Statement{

    private final Statement statement;
    private final StepListener listener;

    public ThucydidesStatement(final Statement statement, final StepListener listener) {
        this.statement = statement;
        this.listener = listener;
    }

    @Override
    public void evaluate() throws Throwable {
        statement.evaluate();
        if (listener.aStepHasFailed()) {
            throw listener.getStepError();
        }
    }
}
