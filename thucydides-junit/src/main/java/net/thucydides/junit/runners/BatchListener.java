package net.thucydides.junit.runners;

import net.thucydides.core.batches.BatchManager;
import org.junit.runner.Description;
import org.junit.runner.notification.RunListener;

public class BatchListener extends RunListener {

    private final BatchManager batchManager;

    public BatchListener(BatchManager batchManager) {
        this.batchManager = batchManager;
    }


    @Override
    public void testRunStarted(Description description) throws Exception {
        super.testRunStarted(description);
        batchManager.registerTestCase(description.getTestClass());
    }
}

