package net.thucydides.junit.runners.mocks;

import net.thucydides.core.pages.Pages;
import net.thucydides.core.screenshots.Photographer;
import net.thucydides.core.webdriver.Configuration;
import net.thucydides.junit.runners.JUnitStepListener;
import net.thucydides.junit.steps.ScenarioStepListener;
import org.openqa.selenium.WebDriver;

import java.io.File;

import static org.mockito.Mockito.mock;


public class MockScenarioStepListener extends JUnitStepListener {

    private Photographer mockPhotographer;
    
    public MockScenarioStepListener(final File outputDirectory, final Pages pages) {
        super(outputDirectory, pages);
        mockPhotographer = mock(Photographer.class);
    }

}
