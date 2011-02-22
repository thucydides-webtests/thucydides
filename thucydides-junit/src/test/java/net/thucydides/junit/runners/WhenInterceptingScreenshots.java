package net.thucydides.junit.runners;

import static org.mockito.Matchers.startsWith;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import net.thucydides.core.screenshots.Photographer;
import net.thucydides.junit.runners.mocks.TestableNarrationListener;
import net.thucydides.junit.samples.ManagedPagesSample;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.Description;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openqa.selenium.TakesScreenshot;

public class WhenInterceptingScreenshots {

    @Mock
    Photographer photographer;

    @Mock
    Description description;
    
    @Before
    public void setupMocks() {
        MockitoAnnotations.initMocks(this);
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void should_name_the_screenshots_after_each_test_based_on_the_test_name() throws Exception {
        TakesScreenshot driver = mock(TakesScreenshot.class);
        Configuration configuration = mock(Configuration.class);
        NarrationListener listener = new TestableNarrationListener(driver, configuration);
        
        Class testClass = ManagedPagesSample.class;
        when(description.getTestClass()).thenReturn(testClass);
        when(description.getMethodName()).thenReturn("should_do_this_step_1");

        listener.testStarted(description);
        listener.testFinished(description);

        Photographer photographer = listener.getPhotographer();        
        verify(photographer).takeScreenshot(startsWith("should_do_this_step_1"));
    }
}
