package samples;

import net.thucydides.core.annotations.Steps;
import net.thucydides.core.annotations.Story;
import net.thucydides.junit.runners.ThucydidesRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(ThucydidesRunner.class)
@Story(Application.MakeWidgets.MakeBigWidgets.class)
public class TestMakeBigWidgetsTestScenario {

    @Steps
    public SampleScenarioSteps steps;

    @Test
    public void happy_day_scenario() {
        steps.anotherGroupOfSteps();
        steps.stepThree("e");
        steps.stepFour("f");
    }    

    @Test
    public void working_scenario() {
        steps.anotherGroupOfSteps();
        steps.stepThatSucceeds();
    }    
	
}
