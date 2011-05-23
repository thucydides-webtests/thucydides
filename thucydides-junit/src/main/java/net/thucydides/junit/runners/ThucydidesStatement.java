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

    final private Statement statement;
    final private StepListener listener;

    public ThucydidesStatement(final Statement statement, StepListener listener) {
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
