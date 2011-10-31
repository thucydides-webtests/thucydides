package net.thucydides.junit.runners;

import net.thucydides.core.util.MockEnvironmentVariables;
import net.thucydides.core.webdriver.Configuration;
import net.thucydides.core.webdriver.SystemPropertiesConfiguration;
import net.thucydides.core.webdriver.WebDriverFactory;
import org.junit.Before;
import org.junit.runners.model.InitializationError;

public abstract class AbstractTestStepRunnerTest {

    protected MockEnvironmentVariables environmentVariables;

    public AbstractTestStepRunnerTest() {
        super();
    }

    @Before
    public void initEnvironment() {
        environmentVariables = new MockEnvironmentVariables();
    }

    protected ThucydidesRunner getTestRunnerUsing(Class<?> testClass) throws InitializationError {
        Configuration configuration = new SystemPropertiesConfiguration(environmentVariables);
        WebDriverFactory factory = new WebDriverFactory(environmentVariables);
        ThucydidesRunner runner = new ThucydidesRunner(testClass, factory, configuration);
        return runner;
    }
    
}