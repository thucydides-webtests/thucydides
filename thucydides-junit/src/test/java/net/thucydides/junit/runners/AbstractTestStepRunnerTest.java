package net.thucydides.junit.runners;

import net.thucydides.core.webdriver.WebDriverFactory;
import org.junit.runners.model.InitializationError;

public abstract class AbstractTestStepRunnerTest {

    public AbstractTestStepRunnerTest() {
        super();
    }

    protected ThucydidesRunner getTestRunnerUsing(Class<?> testClass,
            WebDriverFactory browserFactory) throws InitializationError {
        ThucydidesRunner runner = new ThucydidesRunner(testClass); //new MockTestStepRunner(testClass, browserFactory);
        return runner;
    }
    
}