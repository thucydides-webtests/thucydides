package net.thucydides.junit.runners;

import net.thucydides.core.screenshots.Photographer;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.Result;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

public class WhenRecordingTheTestOutcomes {

    @Mock
    Photographer photographer;

    @Mock
    Description description;

    @Mock
    Result result;
    
    @Before
    public void setupMocks() {
        MockitoAnnotations.initMocks(this);
        
        when(description.getClassName()).thenReturn("ATestClassName");
        when(description.getMethodName()).thenReturn("when_I_run_a_test");
    }
    

    @Test
    public void the_narration_listener_builds_a_model_from_the_junit_test_execution() throws Exception {
        NarrationListener listener = new NarrationListener(photographer);
        
        listener.testRunStarted(description);
        listener.testStarted(description);
        listener.testFinished(description);
        listener.testRunFinished(result);
        
    }
}

