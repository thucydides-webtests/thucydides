package net.thucydides.core.steps.samples;

import net.thucydides.core.annotations.Pending;
import net.thucydides.core.annotations.Step;
import net.thucydides.core.pages.Pages;
import net.thucydides.core.steps.ScenarioSteps;
import org.junit.Ignore;

public class FlatScenarioSteps extends ScenarioSteps {

    public FlatScenarioSteps(Pages pages) {
        super(pages);
    }

    @Step
    public void step_one(){
    }
    
    @Step
    public void step_two() {
    }

    @Step
    public void step_three() {
    }

    @Step
    public void failingStep() {
        throw new AssertionError("Step failed");
    }

    @Ignore
    @Step
    public void ignoredStep() {}

    @Pending
    @Step
    public void pendingStep() {}

    @Step
    public void stepWithLongName() {}

    @Step
    public void stepWithParameters(String name) {}

    @Step
    public void step_with_long_name_and_underscores() {}
}
