package net.thucydides.core.model;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.equalTo;

import java.io.File;
import java.io.IOException;

import net.thucydides.core.annotations.UserStory;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class WhenOrganizingTestRunsIntoUserStories {

    @UserStory
    private final class SomeUserStory {};
    
    private final class NotARealUserStory {};

    /**
     * Note: This is an illustrative test - it just shows how the API should be used.
     */
    @Test
    public void a_test_run_can_have_a_user_story() {
        AcceptanceTestRun testRun = new AcceptanceTestRun("Belongs to Story 1");
        testRun.setUserStory(SomeUserStory.class);
        
        assertThat(testRun.getUserStory().getName(), is(SomeUserStory.class.getName()));
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void a_test_run_can_only_be_assigned_a_properly_annotated_user_story() {
        AcceptanceTestRun testRun = new AcceptanceTestRun("Belongs to Story 1");
        testRun.setUserStory(NotARealUserStory.class);
    }

    
}
