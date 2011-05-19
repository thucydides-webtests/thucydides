package net.thucydides.junit.runners;

import net.thucydides.core.webdriver.WebDriverFactory;
import net.thucydides.junit.runners.mocks.MockTestStepRunner;

import org.junit.runners.model.InitializationError;

public abstract class AbstractTestStepRunnerTest {

    public AbstractTestStepRunnerTest() {
        super();
    }

    protected ThucydidesRunner getTestRunnerUsing(Class<?> testClass,
            WebDriverFactory browserFactory) throws InitializationError {
        ThucydidesRunner runner = new MockTestStepRunner(testClass, browserFactory);
        return runner;
    }
    
}