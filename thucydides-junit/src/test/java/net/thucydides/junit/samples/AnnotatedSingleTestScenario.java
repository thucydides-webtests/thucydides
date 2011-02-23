package net.thucydides.junit.samples;

import net.thucydides.core.pages.Pages;
import net.thucydides.junit.annotations.Managed;
import net.thucydides.junit.annotations.ManagedPages;
import net.thucydides.junit.annotations.Steps;
import net.thucydides.junit.runners.TestStepRunner;
import net.thucydides.junit.annotations.Title;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;

@RunWith(TestStepRunner.class)
public class AnnotatedSingleTestScenario {
    
    @Managed
    public WebDriver webdriver;

    @ManagedPages(defaultUrl = "http://www.google.com")
    public Pages pages;
    
    @Steps
    public AnnotatedSampleScenarioSteps steps;
        
    @Test
    @Title("Oh happy days!")
    public void happy_day_scenario() {
        steps.stepThatSucceeds();
        steps.stepThatIsIgnored();
        steps.stepThatIsPending();
        steps.anotherStepThatSucceeds();
        steps.stepThatFails();
        steps.stepThatShouldBeSkipped();
        steps.done();
    }    
}
