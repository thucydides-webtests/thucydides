package net.thucydides.junit.runners.mocks;

import net.thucydides.core.webdriver.WebDriverFactory;
import net.thucydides.junit.runners.ScenarioStepListener;
import net.thucydides.junit.runners.ThucydidesRunner;

import org.junit.runners.model.InitializationError;
import org.openqa.selenium.TakesScreenshot;

public class MockTestStepRunner extends ThucydidesRunner {

    private MockScenarioStepListener mockedScenarioStepListener;
    
    public MockTestStepRunner(Class<?> klass,
                                WebDriverFactory factory) throws InitializationError {
        super(klass);
        setWebDriverFactory(factory);  
    }

    public ScenarioStepListener getStepListener() {
        if (mockedScenarioStepListener == null) {
            mockedScenarioStepListener = new MockScenarioStepListener((TakesScreenshot) getDriver(), getConfiguration());
        }
        return mockedScenarioStepListener;
    }

}
