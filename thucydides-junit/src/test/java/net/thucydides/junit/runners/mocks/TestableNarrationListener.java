package net.thucydides.junit.runners.mocks;

import java.io.File;

import org.openqa.selenium.TakesScreenshot;

import net.thucydides.core.screenshots.Photographer;
import net.thucydides.junit.runners.NarrationListener;
import static org.mockito.Mockito.mock;

public class TestableNarrationListener extends NarrationListener {

    private Photographer mockPhotographer;
    
    public TestableNarrationListener(final TakesScreenshot driver, final File outputDirectory) {
        super(driver, outputDirectory);
        mockPhotographer = mock(Photographer.class);
    }

    @Override
    public Photographer getPhotographer() {
        return mockPhotographer;
    }
}
