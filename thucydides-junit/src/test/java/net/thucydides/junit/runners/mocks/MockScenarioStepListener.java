package net.thucydides.junit.runners.mocks;

import org.openqa.selenium.TakesScreenshot;
import static org.mockito.Mockito.*;
import net.thucydides.core.screenshots.Photographer;
import net.thucydides.core.webdriver.Configuration;
import net.thucydides.junit.steps.junit.ScenarioStepListener;


public class MockScenarioStepListener extends ScenarioStepListener {

    private Photographer mockPhotographer;
    
    public MockScenarioStepListener(TakesScreenshot driver, Configuration configuration) {
        super(driver, configuration);
        mockPhotographer = mock(Photographer.class);
    }
    
    @Override
    public Photographer getPhotographer() {
        return mockPhotographer;
    }

}
