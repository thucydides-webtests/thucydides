package net.thucydides.core.steps.samples;

import net.thucydides.core.annotations.Pending;
import net.thucydides.core.annotations.Step;
import net.thucydides.core.pages.Pages;
import net.thucydides.core.pages.WrongPageError;
import net.thucydides.core.steps.ScenarioSteps;
import org.junit.Ignore;

public class FlatScenarioSteps extends ScenarioSteps {

    public FlatScenarioSteps(Pages pages) {
        super(pages);
    }

    @Step
    public void step1() throws WrongPageError {
    }
    
    @Step
    public void step2() throws WrongPageError {
    }

    @Step
    public void step3() throws WrongPageError {
    }

    @Step
    public void failingStep() throws WrongPageError {
        throw new AssertionError("Step failed");
    }

    @Ignore
    @Step
    public void ignoredStep() {}

    @Pending
    @Step
    public void pendingStep() {}
}
