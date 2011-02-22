package net.thucydides.core.model;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.equalTo;

import java.io.File;
import java.io.IOException;

import net.thucydides.core.annotations.UserStory;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.when;

public class WhenOrganizingTestRunsIntoUserStories {

    @UserStory
    private final class SomeUserStory {};
    
    private final class NotARealUserStory {};

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
    
    @Mock
    AcceptanceTestRun testRun1;

    @Mock
    AcceptanceTestRun testRun2;
    
    @Mock
    AcceptanceTestRun testRun3;

    @Mock
    AcceptanceTestRun testRun4;

    @Mock
    AcceptanceTestRun testRun5;

    UserStoryTestRun userStoryTestRun;
    
    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
        when(testRun1.isSuccess()).thenReturn(true);
        when(testRun1.isFailure()).thenReturn(false);

        when(testRun2.isSuccess()).thenReturn(true);
        when(testRun2.isFailure()).thenReturn(false);
        
        when(testRun3.isSuccess()).thenReturn(false);
        when(testRun3.isFailure()).thenReturn(true);

        when(testRun1.isSuccess()).thenReturn(false);
        when(testRun2.isFailure()).thenReturn(false);
        
        UserStoryTestRun userStoryTestRun = new UserStoryTestRun();

        userStoryTestRun.addAcceptanceTestRun(testRun1);
        userStoryTestRun.addAcceptanceTestRun(testRun2);
        userStoryTestRun.addAcceptanceTestRun(testRun3);

    }
    
    
    @Ignore("To finish - plane landing")
    public void test_runs_can_be_grouped_in_a_UserStoryTestRun_instance() {
        UserStoryTestRun userStoryTestRun = new UserStoryTestRun();
        
        userStoryTestRun.addAcceptanceTestRun(testRun1);
        userStoryTestRun.addAcceptanceTestRun(testRun2);
        userStoryTestRun.addAcceptanceTestRun(testRun3);
        
        assertThat(userStoryTestRun.getTestRunCount(), is(3));
    }
    
    @Ignore("To finish - plane landing")
    public void should_provide_the_number_of_successful_test_runs() {
        
        assertThat(userStoryTestRun.getTestRunCount(), is(3));
    }

    @Ignore("To finish - plane landing")
    @Test
    public void should_provide_the_number_of_failed_test_runs() {
        when(testRun1.isSuccess()).thenReturn(true);
        when(testRun2.isSuccess()).thenReturn(true);
        when(testRun3.isFailure()).thenReturn(true);
                
        UserStoryTestRun userStoryTestRun = new UserStoryTestRun();
        userStoryTestRun.addAcceptanceTestRun(testRun1);
        userStoryTestRun.addAcceptanceTestRun(testRun2);
        userStoryTestRun.addAcceptanceTestRun(testRun3);
        
        assertThat(userStoryTestRun.getFailedTestRunCount(), is(1));
    }

}
