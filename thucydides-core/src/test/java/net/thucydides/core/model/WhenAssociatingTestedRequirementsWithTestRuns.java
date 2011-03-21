package net.thucydides.core.model;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import org.junit.Test;

public class WhenAssociatingTestedRequirementsWithTestRuns {

    @Test
    public void a_test_run_can_test_a_requirment() {
        AcceptanceTestRun testRun = new AcceptanceTestRun("Sample");
        testRun.testsRequirement("ABC");
        
        assertThat(testRun.getTestedRequirements(), hasItem("ABC"));
    }
    
    @Test
    public void a_test_run_can_test_several_requirments() {
        AcceptanceTestRun testRun = new AcceptanceTestRun("Sample");
        testRun.testsRequirement("ABC");
        testRun.testsRequirement("DEF");
        
        assertThat(testRun.getTestedRequirements(), hasItems("ABC", "DEF"));
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
        AcceptanceTestRun testRun = new AcceptanceTestRun("Sample");
        testRun.testsRequirement("A");
        testRun.testsRequirement("B");
        
        ConcreteTestStep step1 = new ConcreteTestStep("Step 1");
        step1.setResult(TestResult.SUCCESS);
        step1.testsRequirement("C");

        ConcreteTestStep step2 = new ConcreteTestStep("Step 2");
        step2.setResult(TestResult.SUCCESS);
        step2.testsRequirement("D");

        testRun.recordStep(step1);
        testRun.recordStep(step2);
        assertThat(testRun.getAllTestedRequirements(), hasItems("A", "B", "C", "D"));
    }
    

}
