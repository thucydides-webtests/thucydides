package net.thucydides.junit.samples;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import net.thucydides.core.pages.Pages;
import net.thucydides.junit.annotations.Pending;
import net.thucydides.junit.annotations.Step;
import net.thucydides.junit.annotations.TestsRequirement;
import net.thucydides.junit.steps.ScenarioSteps;

import org.junit.Ignore;

public class SampleScenarioSteps extends ScenarioSteps {
    
    public SampleScenarioSteps(Pages pages) {
        super(pages);
    }

    @Step
    @TestsRequirement("DEF")
    public void stepThatSucceeds() {
    }

    @Step
    @TestsRequirement("GHI")
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
    
}
