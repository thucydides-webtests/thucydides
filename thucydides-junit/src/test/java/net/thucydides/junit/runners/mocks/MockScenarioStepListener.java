package net.thucydides.junit.runners.mocks;

import org.openqa.selenium.TakesScreenshot;
import static org.mockito.Mockito.*;
import net.thucydides.core.screenshots.Photographer;
import net.thucydides.junit.runners.Configuration;
import net.thucydides.junit.runners.ScenarioStepListener;


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
