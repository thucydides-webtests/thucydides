package net.thucydides.easyb.samples;


import net.thucydides.core.annotations.Step
import net.thucydides.core.annotations.StepGroup
import net.thucydides.core.annotations.Steps
import net.thucydides.core.pages.Pages
import net.thucydides.core.pages.WrongPageError
import net.thucydides.core.steps.ScenarioSteps

public class NestedScenarioSteps extends ScenarioSteps {

    @Steps
    public SampleSteps innerSteps;

    public NestedScenarioSteps(Pages pages) {
        super(pages);
    }

    @StepGroup("Step group 1")
    public void step1() throws WrongPageError {
        innerSteps.step1();
        innerSteps.step2();
        innerSteps.step3();
    }
    
    @StepGroup("Step group 2")
    public void step2() throws WrongPageError {
        innerSteps.step1();
        innerSteps.step3();
    }

    @Step
    public void step3() throws WrongPageError {
    }

    @Step
    public void step_with_nested_failure() throws WrongPageError {
        innerSteps.step1();
        innerSteps.failingStep();
    }

}
