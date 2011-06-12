package net.thucydides.core.model;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;

public class WhenAssociatingTestedRequirementsWithTestRuns {

    @Test
    public void a_test_run_can_test_a_requirment() {
        TestOutcome testOutcome = new TestOutcome("Sample");
        testOutcome.testsRequirement("ABC");
        
        assertThat(testOutcome.getTestedRequirements(), hasItem("ABC"));
    }
    
    @Test
    public void a_test_run_can_test_several_requirments() {
        TestOutcome testOutcome = new TestOutcome("Sample");
        testOutcome.testsRequirement("ABC");
        testOutcome.testsRequirement("DEF");
        
        assertThat(testOutcome.getTestedRequirements(), hasItems("ABC", "DEF"));
    }
    
    @Test
    public void a_test_step_can_test_several_requirments() {
        TestStep step = new ConcreteTestStep();
        step.testsRequirement("A");
        step.testsRequirement("B");
        assertThat(step.getTestedRequirements(), hasItems("A","B"));
    }

    @Test
    public void a_test_run_should_test_its_requirments_and_those_of_its_steps() {
        TestOutcome testOutcome = new TestOutcome("Sample");
        testOutcome.testsRequirement("A");
        testOutcome.testsRequirement("B");
        
        ConcreteTestStep step1 = new ConcreteTestStep("Step 1");
        step1.setResult(TestResult.SUCCESS);
        step1.testsRequirement("C");

        ConcreteTestStep step2 = new ConcreteTestStep("Step 2");
        step2.setResult(TestResult.SUCCESS);
        step2.testsRequirement("D");

        testOutcome.recordStep(step1);
        testOutcome.recordStep(step2);
        assertThat(testOutcome.getAllTestedRequirements(), hasItems("A", "B", "C", "D"));
    }
    

}
