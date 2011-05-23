package net.thucydides.junit.runners.mocks;

import net.thucydides.core.webdriver.WebDriverFactory;
import net.thucydides.junit.listeners.JUnitStepListener;
import net.thucydides.junit.runners.ThucydidesRunner;
import org.junit.runners.model.InitializationError;

public class MockTestStepRunner extends ThucydidesRunner {

    private MockScenarioStepListener mockedScenarioStepListener;
    
    public MockTestStepRunner(Class<?> klass,
                                WebDriverFactory factory) throws InitializationError {
        super(klass);
        setWebDriverFactory(factory);  
    }

    public JUnitStepListener getStepListener() {
        return super.getStepListener();
    }

}
