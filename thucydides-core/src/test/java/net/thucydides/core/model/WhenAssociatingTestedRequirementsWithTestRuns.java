package net.thucydides.core.model;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;

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
        
        assertThat(testRun.getTestedRequirements(), hasItem("ABC"));
        assertThat(testRun.getTestedRequirements(), hasItem("DEF"));
    }
    
}
