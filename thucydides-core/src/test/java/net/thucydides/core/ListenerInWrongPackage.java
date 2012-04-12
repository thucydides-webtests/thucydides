package net.thucydides.core;

import net.thucydides.core.model.Story;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.steps.ExecutedStepDescription;
import net.thucydides.core.steps.StepFailure;
import net.thucydides.core.steps.StepListener;
import org.openqa.selenium.WebDriver;

import java.util.List;

public class ListenerInWrongPackage implements StepListener {
    public void testSuiteStarted(Class<?> storyClass) {
        
    }

    public void testSuiteStarted(Story story) {
        
    }

    @Override
    public void testSuiteFinished() {
    }

    public void testStarted(String description) {
        
    }

    public void testFinished(TestOutcome result) {
        
    }

    public void stepStarted(ExecutedStepDescription description) {
        
    }

    public void skippedStepStarted(ExecutedStepDescription description) {
    }

    public void stepFailed(StepFailure failure) {
        
    }

    @Override
    public void lastStepFailed(StepFailure failure) {
    }

    public void stepIgnored() {
        
    }

    @Override
    public void stepIgnored(String message) {
    }

    public void stepPending() {
        
    }

    @Override
    public void stepPending(String message) {
    }

    public void stepFinished() {
        
    }

    @Override
    public void testFailed(TestOutcome testOutcome, Throwable cause) {
    }

    @Override
    public void testIgnored() {
    }

    public List<TestOutcome> getTestOutcomes() {
        return null;  
    }

    public WebDriver getDriver() {
        return null;  
    }

    @Override
    public void notifyScreenChange() {
    }
}
