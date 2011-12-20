package net.thucydides.core.batches;

import com.google.inject.Inject;
import net.thucydides.core.util.EnvironmentVariables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;

import static net.thucydides.core.ThucydidesSystemProperty.*;
import com.google.inject.Singleton;

/**
 * Manages running test cases (i.e. test classes) in batches.
 */

@Singleton
public class SystemVariableBasedBatchManager implements BatchManager {

    private final AtomicInteger testCaseCount = new AtomicInteger(0);
    private final int batchCount;
    private final int batchNumber;

    private Set<String> registeredTestCases = new CopyOnWriteArraySet<String>();

    private final Logger logger = LoggerFactory.getLogger(SystemVariableBasedBatchManager.class);

    /**
     * The batch manager is initiated using system properties.
     * These properties are "thucydides.batch.count" and "thucydides.batch.number".
     */
    @Inject
    public SystemVariableBasedBatchManager(EnvironmentVariables environmentVariables) {
        this.batchCount = environmentVariables.getPropertyAsInteger(BATCH_COUNT.getPropertyName(), 0);
        this.batchNumber = environmentVariables.getPropertyAsInteger(BATCH_NUMBER.getPropertyName(), 0);
    }

    @Override
    public int getCurrentTestCaseNumber() {
        return testCaseCount.get();
    }

    public void registerTestCase(Class<?> klass) {
        String testCaseName = klass.getName();
        registerTestCaseIfNew(testCaseName);
    }

    public void registerTestCase(String testCaseName) {
        registerTestCaseIfNew(testCaseName);
    }

    private synchronized void registerTestCaseIfNew(String testCaseName) {
        if (!registeredTestCases.contains(testCaseName)) {
            registeredTestCases.add(testCaseName);
            testCaseCount.getAndIncrement();
        }
    }

    @Override
    public boolean shouldExecuteThisTest() {
        if (batchCount > 0) {
            return (testCaseCount.get() % batchCount == (batchNumber % batchCount));
        } else {
            return true;
        }
    }
}
