package net.thucydides.samples;

import net.thucydides.core.annotations.Steps;
import net.thucydides.core.pages.Pages;
import net.thucydides.core.annotations.ManagedPages;
import net.thucydides.core.annotations.UserStoryCode;
import net.thucydides.junit.runners.ThucydidesRunner;
import net.thucydides.junit.annotations.Managed;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;

@RunWith(ThucydidesRunner.class)
@UserStoryCode("US02")
public class SampleTestScenarioWithGroups {
    
    @Managed
    public WebDriver webdriver;

    @ManagedPages(defaultUrl = "http://www.google.com")
    public Pages pages;
    
    @Steps
    public SampleScenarioSteps steps;
        
    @Test
    public void happy_day_scenario() {
        steps.anotherGroupOfSteps();
        steps.stepThree("e");
        steps.stepFour("f");
    }    

    @Test
    public void failing_scenario() {
        steps.groupOfStepsContainingAFailure();
        steps.anotherGroupOfSteps();
        steps.stepThatSucceeds();
    }    
	
}
