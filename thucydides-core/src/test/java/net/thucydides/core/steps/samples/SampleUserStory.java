package net.thucydides.core.steps.samples;

import net.thucydides.core.annotations.Steps;
import org.junit.Test;

/**
 * A User story represents a way the user interacts with the system.
 * <p></p>A user story is illustrated validated by a set of examples. These examples are automated by test scenarios within
 * the story. A test scenario is an end-to-end test, illustrating a complete interaction between the user and the
 * system.</p>
 * <p>A user story can be associated with a ApplicationFeature, which is a higher-level functionality that is specified by several
 * stories.</p>
 * <p>In Thucydides, a user story can be implemented in several ways: a test case in JUnit, a story in easyb, and so on.
 * This user story illustrates the general principles for testing purposes,, without implementing the test in any
 * particular technology. If this were a JUnit test, it would need to use the ThucydidesRunner and Test annotations
 * on the test methods.</p>
 */

//@BelongsToFeature(SampleFeature.class)
public class SampleUserStory {

    @Steps
    SimpleScenarioSteps steps;

    @Test
    public void scenario_one() {
        steps.clickOnProjects();
        steps.clickOnCategories();
    }

    @Test
    public void scenario_two() {
        steps.clickOnProjectAndCheckTitle();
    }

    @Test
    public void scenario_three() {
        steps.clickOnProjects();
        steps.clickOnInexistantLink();
    }

}
