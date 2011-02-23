package net.thucydides.junit.runners;

import java.util.ArrayList;
import java.util.List;

import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

import com.google.common.collect.ImmutableList;

public class TestFailureListener extends RunListener {
    
    private List<Failure> failures = new ArrayList<Failure>();
    
    @Override
    public void testFailure(Failure failure) throws Exception {
        failures.add(failure);
        super.testFailure(failure);
    }
    
    public List<Failure> getFailures() {
        return ImmutableList.copyOf(failures);
    }
}
