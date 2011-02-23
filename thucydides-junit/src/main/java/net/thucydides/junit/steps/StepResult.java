package net.thucydides.junit.steps;

import java.util.ArrayList;
import java.util.List;

import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import com.google.common.collect.ImmutableList;

/**
 * A version of the JUnit Result class that we can use to publish step results.
 * @author johnsmart
 *
 */
public class StepResult extends Result {

    private List<Failure> failures = new ArrayList<Failure>();
    private int ignored = 0;
    private int run = 0;
    
    public void logFailure(final Failure failure) {
        failures.add(failure);
    }
    
    public void logIgnoredTest() {
        ignored++;
    }
    
    public void logExecutedTest() {
        run++;
    }
    
    @Override
    public int getFailureCount() {
        return failures.size();
    }
    @Override
    public List<Failure> getFailures() {
        return ImmutableList.copyOf(failures);
    }
    
    @Override
    public int getIgnoreCount() {
        return ignored;
    }
    
    @Override
    public int getRunCount() {
        return run;
    }
    
    @Override
    public boolean wasSuccessful() {
        return (getFailureCount() == 0);
    }
}
