package net.thucydides.junit.samples;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import net.thucydides.core.pages.Pages;
import net.thucydides.junit.annotations.Pending;
import net.thucydides.junit.annotations.Step;
import net.thucydides.junit.annotations.StepGroup;
import net.thucydides.junit.annotations.TestsRequirement;
import net.thucydides.junit.annotations.TestsRequirements;
import net.thucydides.junit.steps.ScenarioSteps;

import org.junit.Ignore;

public class SampleScenarioSteps extends ScenarioSteps {
    
    public SampleScenarioSteps(Pages pages) {
        super(pages);
    }

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
    public void stepThatFails() {
        assertThat(1, is(2));
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
    
    @StepGroup
    public void groupOfSteps() {
        stepThatSucceeds();
        stepThatFails();
        stepThatShouldBeSkipped();
        
    }

    @StepGroup
    public void groupOfStepsContainingAnError() {
        stepThatSucceeds();
        anotherStepThatSucceeds();
        String nullString = null;
        int thisShouldFail = nullString.length();
        
    }

}
