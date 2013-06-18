package net.thucydides.core.reports.adaptors;

import net.thucydides.core.model.TestOutcome;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Test outcome adaptors provide a way to read test results from an external source.
 *
 *
 */
public interface TestOutcomeAdaptor {
    public List<TestOutcome> loadOutcomesFrom(final File source) throws IOException;
}
