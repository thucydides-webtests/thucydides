package samples;

import net.thucydides.core.annotations.Issue;
import net.thucydides.core.annotations.Issues;
import net.thucydides.core.annotations.Steps;
import net.thucydides.core.annotations.Title;
import net.thucydides.junit.runners.ThucydidesRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(ThucydidesRunner.class)
@Issues({"#200","#300"})
public class TestSampleTestScenario {
    
    @Steps
    public SampleScenarioSteps steps;

    @Title("Happy day scenario - fixes issues #123 and #456")
    @Test
    public void happy_day_scenario() {
        steps.anotherGroupOfSteps();
        steps.stepThree("e");
        steps.stepFour("f");
    }

    @Issue("400")
    @Test
    public void failing_scenario() {
        steps.groupOfStepsContainingAFailure();
        steps.anotherGroupOfSteps();
        steps.stepThatSucceeds();
    }    
	
}
