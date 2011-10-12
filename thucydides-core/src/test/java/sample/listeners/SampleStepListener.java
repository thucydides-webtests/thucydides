package sample.listeners;

import net.thucydides.core.model.Story;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.steps.ExecutedStepDescription;
import net.thucydides.core.steps.StepFailure;
import net.thucydides.core.steps.StepListener;
import org.openqa.selenium.WebDriver;

import java.util.List;

public class SampleStepListener implements StepListener {
    public void testSuiteStarted(Class<?> storyClass) {
        
    }

    public void testSuiteStarted(Story story) {
        
    }

    public void testStarted(String description) {
        
    }

    public void testFinished(TestOutcome result) {
        
    }

    public void stepStarted(ExecutedStepDescription description) {
        
    }

    public void stepFailed(StepFailure failure) {
        
    }

    public void stepIgnored() {
        
    }

    public void stepPending() {
        
    }

    public void stepFinished() {
        
    }

    public List<TestOutcome> getTestOutcomes() {
        return null;  
    }

    public void setDriver(WebDriver driver) {
        
    }

    public WebDriver getDriver() {
        return null;  
    }

    public boolean aStepHasFailed() {
        return false;  
    }

    public Throwable getTestFailureCause() {
        return null;  
    }

    public void testFailed(Throwable cause) {
        
    }

    public void testIgnored() {
        
    }
}
