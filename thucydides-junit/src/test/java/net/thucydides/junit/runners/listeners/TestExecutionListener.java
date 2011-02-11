package net.thucydides.junit.runners.listeners;

import java.util.ArrayList;
import java.util.List;

import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

/**
 * A test listener that records the names of the executed tests.
 * Good for testing what tests where executed and in what order.
 * 
 * @author johnsmart
 *
 */
public class TestExecutionListener extends RunListener {

    final List<String> executedTests = new ArrayList<String>();
    final List<String> failedTests = new ArrayList<String>();
    final List<String> ignoredTests = new ArrayList<String>();
    
    @Override
    public void testStarted(Description description) throws Exception {
        executedTests.add(description.getMethodName());
        super.testStarted(description);
    }

    @Override
    public void testFailure(Failure failure) throws Exception {
        failedTests.add(failure.getDescription().getMethodName());
        
        super.testFailure(failure);
    }
    
    @Override
    public void testIgnored(Description description) throws Exception {
        ignoredTests.add(description.getMethodName());
        super.testIgnored(description);
    }
    
    @Override
    public void testFinished(Description description) throws Exception {
        super.testFinished(description);
    }
    
    @Override
    public void testRunFinished(Result result) throws Exception {
        super.testRunFinished(result);
    }
    
    public List<String> getExecutedTests() {
        return new ArrayList<String>(executedTests);
    }
    
    public List<String> getFailedTests() {
        return new ArrayList<String>(failedTests);
    }
    
    public List<String> getIgnoredTests() {
        return new ArrayList<String>(ignoredTests);
    }
}
