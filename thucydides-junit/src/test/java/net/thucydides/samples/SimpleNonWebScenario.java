package net.thucydides.samples;

import net.thucydides.core.annotations.Managed;
import net.thucydides.core.annotations.ManagedPages;
import net.thucydides.core.annotations.Steps;
import net.thucydides.core.annotations.UserStoryCode;
import net.thucydides.core.pages.Pages;
import net.thucydides.junit.runners.ThucydidesRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;

@RunWith(ThucydidesRunner.class)
public class SimpleNonWebScenario {
    
    @Steps
    public SampleNonWebSteps steps;

    @Test
    public void happy_day_scenario() {
        steps.stepThatSucceeds();
        steps.anotherStepThatSucceeds();
    }

    @Test
    public void another_happy_day_scenario() {
        steps.stepThatSucceeds();
        steps.stepThatSucceeds();
        steps.anotherStepThatSucceeds();
    }

}
