package samples;

import net.thucydides.core.annotations.Steps;
import net.thucydides.core.annotations.Story;
import net.thucydides.junit.runners.ThucydidesRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(ThucydidesRunner.class)
@Story(Application.SellWidgets.SellWidgetsOnline.class)
public class TestSellWidgetsOnlineTestScenario {

    @Steps
    public SampleScenarioSteps steps;
        
    @Test
    public void another_happy_day_scenario() {
		steps.stepThatSucceeds();
		steps.anotherStepThatSucceeds();
        steps.stepThatCallsNestedSteps();
        steps.stepThree("a");
        steps.stepFour("c");
    }    

    @Test
    public void successful_scenario() {
		steps.stepThatSucceeds();
		steps.anotherStepThatSucceeds();
        steps.anotherGroupOfSteps();
        steps.stepFour("c");
    }    
	
}
