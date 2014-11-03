package samples;

import net.thucydides.core.annotations.*;
import net.thucydides.core.pages.Pages;
import net.thucydides.core.steps.ScenarioSteps;
import org.junit.Ignore;

public class SampleScenarioSteps extends ScenarioSteps {
    
    public SampleScenarioSteps(Pages pages) {
        super(pages);
    }

    @Steps
    public SampleScenarioNestedSteps nestedSteps;
    
    @Step
    @TestsRequirement("LOW_LEVEL_BUSINESS_RULE")
    public void stepThatSucceeds() {
    }

    @Step
    @TestsRequirements({"LOW_LEVEL_BUSINESS_RULE_1","LOW_LEVEL_BUSINESS_RULE_2"})
    public void anotherStepThatSucceeds() {
    }

    @Step
    public void stepThree(String option) {
    }

    @Step
    public void stepFour(String option) {
    }
    
    @Step
    public void stepThatShouldBeSkipped() {
    }

    @StepGroup("Nested group of steps")
    public void stepThatCallsNestedSteps() {
        nestedSteps.stepThatSucceeds();
        nestedSteps.anotherStepThatSucceeds();
    }

    @Step
    @Pending
    public void stepThatIsPending() {
    }

    @Step
    @Ignore
    public void stepThatIsIgnored() {
    }

    public void anUnannotatedMethod() {
    }

    @Step
    public void stepWithAParameter(String value) {
    }
    
    @Step
    public void stepWithTwoParameters(String value, int number) {
    }
    
    @StepGroup("Group of steps")
    public void groupOfStepsContainingAFailure() {
        stepThatSucceeds();
        stepThatShouldBeSkipped();
        
    }

    @StepGroup("Another group of steps")
    public void anotherGroupOfSteps() {
        stepThatSucceeds();
        anotherStepThatSucceeds();
        stepThatIsPending();
        
    }

    @StepGroup("Group of steps")
    public void groupOfStepsContainingAnError() {
        stepThatSucceeds();
        anotherStepThatSucceeds();
        
    }

}
