package net.thucydides.samples;

import net.thucydides.core.annotations.Issue;
import net.thucydides.core.annotations.Issues;
import net.thucydides.core.annotations.Managed;
import net.thucydides.core.annotations.ManagedPages;
import net.thucydides.core.annotations.Steps;
import net.thucydides.core.pages.Pages;
import net.thucydides.junit.runners.ThucydidesRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;

@RunWith(ThucydidesRunner.class)
@Issues({"#MY-PROJECT-200","#MY-PROJECT-300"})
public class TestSampleTestScenario {

    @Steps
    public SampleScenarioSteps steps;

    @Issues({"#MY-PROJECT-123", "#MY-PROJECT-456"})
    @Test
    public void happy_day_scenario() {
        steps.anotherGroupOfSteps();
        steps.stepThree("e");
        steps.stepFour("f");
    }

    @Issue("#MY-PROJECT-400")
    @Test
    public void failing_scenario() {
        steps.groupOfStepsContainingAFailure();
        steps.anotherGroupOfSteps();
        steps.stepThatSucceeds();
    }    
	
}
