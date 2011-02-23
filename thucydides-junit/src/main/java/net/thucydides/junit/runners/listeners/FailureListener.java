package net.thucydides.junit.runners.listeners;

import java.util.ArrayList;
import java.util.List;

import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

import com.google.common.collect.ImmutableList;

/**
 * Keeps track of failures as they occur.
 * @author johnsmart
 *
 */
public class FailureListener extends RunListener {

    private List<Failure> failures = new ArrayList<Failure>();
    
    @Override
    public void testFailure(final Failure failure) throws Exception {
        failures.add(failure);
        super.testFailure(failure);
    }
    
    @Override
    public void testStarted(Description description) throws Exception {
        super.testStarted(description);
    }
    
    @Override
    public void testFinished(Description description) throws Exception {
        super.testFinished(description);
    }
    public List<Failure> getFailures() {
        return ImmutableList.copyOf(failures);
    }
}
