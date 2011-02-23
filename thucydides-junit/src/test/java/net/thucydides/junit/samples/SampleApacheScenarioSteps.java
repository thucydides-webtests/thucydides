package net.thucydides.junit.samples;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import net.thucydides.core.pages.Pages;
import net.thucydides.junit.annotations.Step;
import net.thucydides.junit.steps.ScenarioSteps;

public class SampleApacheScenarioSteps extends ScenarioSteps {
    
    public SampleApacheScenarioSteps(Pages pages) {
        super(pages);
    }

    @Step(1)
    public void openHomePage() {
    }

    @Step(2)
    public void stepTwo() {
    }

    @Step(3)
    public void stepThree(String option) {
        assertThat(1, is(2));
    }

    @Step(4)
    public void stepFour(String option) {
    }
}
