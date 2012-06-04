package net.thucydides.jbehave.steps;

import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Pending;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class StorySteps {
    @Given("I have an implemented JBehave scenario")
    public void givenIHaveAnImplementedJBehaveScenario() {
    }

    @Given("I have a badly implemented JBehave scenario")
    public void givenIHaveABadlyImplementedJBehaveScenario() {
    }

    @Given("the scenario works")
    public void givenTheScenarioWorks() {
    }

    @When("I run the scenario")
    public void whenIRunTheScenario() {
    }

    @Then("I should get a successful result")
    public void thenIShouldGetASuccessfulResult() {
    }

    @Given("the scenario fails")
    public void givenTheScenarioFails() {
        assertThat(true,is(false));
    }

    @Then("I should get a failed result")
    public void thenIShouldGetAFailedResult() {
    }

    @Then("I should skip subsequent results")
    public void andIShouldSkipSubsequentResults() {}

    @Given("a JBehave story with a pending implementation")
    @Pending
    public void aJBehaveStoryWithAPendingImplementation() {}


}
