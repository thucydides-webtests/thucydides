package net.thucydides.junit.runners;

import net.thucydides.core.webdriver.WebDriverFactory;
import net.thucydides.junit.runners.mocks.MockThucydidesRunner;
import net.thucydides.junit.runners.samples.ManagedWebDriverSample;

import org.junit.runners.model.InitializationError;

public abstract class AbstractWebDriverTest {

    public AbstractWebDriverTest() {
        super();
    }

    protected ThucydidesRunner getTestRunnerUsing(WebDriverFactory browserFactory)
            throws InitializationError {
        return new MockThucydidesRunner(ManagedWebDriverSample.class, browserFactory);
    }

    protected ThucydidesRunner getTestRunnerUsing(Class<?> testClass,
            WebDriverFactory browserFactory) throws InitializationError {
        ThucydidesRunner runner = new MockThucydidesRunner(testClass, browserFactory);
        return runner;
    }

}