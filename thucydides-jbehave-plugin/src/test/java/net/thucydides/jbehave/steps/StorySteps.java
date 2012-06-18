package net.thucydides.jbehave.steps;

import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Named;
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


    @Given("a stock of <symbol> and a threshold of <threshold>")
    public void givenAStock(@Named("symbol") String symbol, @Named("threshold") double threshold) {
        System.out.println("Stock " + symbol);
    }

    @When("the stock is traded at <price>")
    public void whenTheStockIsTradedAtprice(@Named("price") String price) {
        System.out.println("Stock traded at: " + price);
    }

    @Then("the alert status should be <status>")
    public void thenTheAlertStatusShouldBestatus(@Named("status") String status) {
        System.out.println("Expected status: " + status);
        if (status.equals("FAIL")) {
            throw new AssertionError();
        }
    }

    @Then("some other stuff should also work")
    public void someOtherStuffShouldWork() {}
}

