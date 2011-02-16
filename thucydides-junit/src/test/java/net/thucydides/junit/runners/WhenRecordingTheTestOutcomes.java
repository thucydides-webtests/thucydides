package net.thucydides.junit.runners;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import net.thucydides.core.screenshots.Photographer;
import net.thucydides.junit.integration.samples.OpenGoogleHomePageSample;
import net.thucydides.junit.runners.mocks.TestableNarrationListener;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.Result;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openqa.selenium.TakesScreenshot;

public class WhenRecordingTheTestOutcomes {

    @Mock
    Photographer photographer;

    @Mock
    Description description;

    @Mock
    Result result;
    
    @Before
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void setupMocks() {
        MockitoAnnotations.initMocks(this);
        
        Class testClass = OpenGoogleHomePageSample.class;
        when(description.getTestClass()).thenReturn(testClass);
        when(description.getMethodName()).thenReturn("when_I_run_a_test");
    }
    

    @Test
    public void the_narration_listener_builds_a_model_from_the_junit_test_execution() throws Exception {
        NarrationListener listener = createMockNarrationListener();
        
        listener.testRunStarted(description);
        listener.testStarted(description);
        listener.testFinished(description);
        listener.testRunFinished(result);
        
        assertThat(listener.getAcceptanceTestRun(), notNullValue());

    }
    
    @Test
    public void the_narration_listener_builds_a_humanized_title_for_the_test_case() throws Exception {
        NarrationListener listener = createMockNarrationListener();
        
        listener.testRunStarted(description);
        listener.testStarted(description);
        listener.testFinished(description);
        listener.testRunFinished(result);
        
        assertThat(listener.getAcceptanceTestRun().getTitle(), is("Open google home page sample"));
        
    }

    
    private NarrationListener createMockNarrationListener() {
        TakesScreenshot driver = mock(TakesScreenshot.class);
        Configuration configuration = mock(Configuration.class);
        return new TestableNarrationListener(driver, configuration);
    }
    
}

