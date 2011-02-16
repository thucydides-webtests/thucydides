package net.thucydides.junit.runners.mocks;

import static org.mockito.Mockito.mock;
import net.thucydides.core.screenshots.Photographer;
import net.thucydides.junit.runners.Configuration;
import net.thucydides.junit.runners.NarrationListener;

import org.openqa.selenium.TakesScreenshot;

public class TestableNarrationListener extends NarrationListener {

    private Photographer mockPhotographer;
    
    public TestableNarrationListener(final TakesScreenshot driver, final Configuration configuration) {
        super(driver, configuration);
        mockPhotographer = mock(Photographer.class);
    }

    @Override
    public Photographer getPhotographer() {
        return mockPhotographer;
    }
}
