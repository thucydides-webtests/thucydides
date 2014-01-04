package samples;

import net.thucydides.core.annotations.Steps;
import net.thucydides.core.annotations.UserStoryCode;
import net.thucydides.junit.runners.ThucydidesRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(ThucydidesRunner.class)
@UserStoryCode("US02")
public class TestScenarioWithGroups {

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
