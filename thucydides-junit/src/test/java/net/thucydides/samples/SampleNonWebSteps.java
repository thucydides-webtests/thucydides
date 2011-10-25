package net.thucydides.samples;

import net.thucydides.core.annotations.Pending;
import net.thucydides.core.annotations.Step;
import net.thucydides.core.annotations.StepGroup;
import net.thucydides.core.annotations.Steps;
import net.thucydides.core.annotations.TestsRequirement;
import net.thucydides.core.annotations.TestsRequirements;
import net.thucydides.core.pages.Pages;
import net.thucydides.core.steps.ScenarioSteps;
import net.thucydides.junit.runners.ThucydidesRunner;
import org.junit.Ignore;
import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.NoSuchElementException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;

@SuppressWarnings("serial")
public class SampleNonWebSteps {

    private static final Logger LOGGER = LoggerFactory.getLogger(ThucydidesRunner.class);

    public SampleNonWebSteps() {
    }

    @Step("A pending step")
    @Pending
    public void stepThatIsPending() {}

    @Step
    @Ignore
    public void stepThatIsIgnored() {}

    @Step
    public void stepThatSucceeds() {}

    @Step
    public void anotherStepThatSucceeds() {}

    @Step
    public void stepThatFails() {
        throw new AssertionError("Oh bother!");
    }

    @Step
    public void stepWithAParameter(String param) {}

    @Step
    public void stepWithTwoParameters(String param, int i) {}
}
