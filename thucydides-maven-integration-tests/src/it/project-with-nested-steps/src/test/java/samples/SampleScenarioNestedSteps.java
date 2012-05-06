package samples;

import net.thucydides.core.annotations.Pending;
import net.thucydides.core.annotations.Step;
import net.thucydides.core.pages.Pages;
import net.thucydides.core.steps.ScenarioSteps;
import org.junit.Ignore;

public class SampleScenarioNestedSteps extends ScenarioSteps {
    
    public SampleScenarioNestedSteps(Pages pages) {
        super(pages);
    }

    @Step
    public void stepThatSucceeds() {
    }

    @Step
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
    
    @Step
    public void groupOfSteps() {
        stepThatSucceeds();
        stepThatShouldBeSkipped();
        
    }

    @Step
    public void groupOfNestedSteps() {
        stepThatSucceeds();
        stepThatShouldBeSkipped();
        groupOfSteps();
    }


    @Step
    public void anotherGroupOfSteps() {
        stepThatSucceeds();
        anotherStepThatSucceeds();
        stepThatIsPending();
        groupOfSteps();
        groupOfNestedSteps();
    }

    @Step
    public void groupOfStepsContainingAnError() {
        stepThatSucceeds();
        anotherStepThatSucceeds();
        groupOfSteps();
    }

}
