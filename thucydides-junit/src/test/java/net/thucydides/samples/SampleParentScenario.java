package net.thucydides.samples;

import net.thucydides.core.annotations.ManagedPages;
import net.thucydides.core.annotations.Steps;
import net.thucydides.core.annotations.TestsRequirement;
import net.thucydides.core.pages.Pages;
import net.thucydides.junit.annotations.Managed;
import net.thucydides.junit.runners.ThucydidesRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RunWith(ThucydidesRunner.class)
public class SampleParentScenario {
    
    @Managed
    private WebDriver webdriver;

    @ManagedPages(defaultUrl = "http://www.google.com")
    private Pages pages;
    
    @Steps
    private SampleScenarioSteps steps;

    protected SampleScenarioSteps getSteps() {
        return steps;
    }

}
